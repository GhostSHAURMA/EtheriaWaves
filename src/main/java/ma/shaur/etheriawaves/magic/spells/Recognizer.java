	package ma.shaur.etheriawaves.magic.spells;

/**
 * Provided bellow is an implementation of <a href="https://depts.washington.edu/acelab/proj/dollar/qdollar.html">$Q Recogniser</a>.
 * **Heavily inspired** by their C# implementation.
 */
public class Recognizer 
{
    public static class Point
    {
        private float X, Y;
        private int strokeID, intX, intY;

        public Point(float x, float y, int strokeId)
        {
            this.X = x;
            this.Y = y;
            this.strokeID = strokeId;
            this.intX = 0;
            this.intY = 0;
        }
        
        public float getX() 
        {
			return X;
		}
        
        public float getY() 
        {
			return Y;
		}
        
        public int getStrokeID() 
        {
			return strokeID;
		}
    }

    public static class Geometry
    {
        public static float SqrEuclideanDistance(Point a, Point b)
        {
        	return (a.X - b.X) * (a.X - b.X) + (a.Y - b.Y) * (a.Y - b.Y);
        }

        public static float EuclideanDistance(Point a, Point b)
        {
        	return (float)Math.sqrt(SqrEuclideanDistance(a, b));
        }
    }
	
    public static class Gesture
    {
        private Point[] points = null;
        private Point[] pointsRaw = null;
       
        private static final int SAMPLING_RESOLUTION = 64;
        private static final int MAX_INT_COORDINATES = 1024;
        private static int LUT_SIZE = 64;
        private static int LUT_SCALE_FACTOR = MAX_INT_COORDINATES / LUT_SIZE;

        private int[][] LUT = null;
        
        public static void setLutSize(int lut)
        {
        	LUT_SIZE = lut;
        	LUT_SCALE_FACTOR = MAX_INT_COORDINATES / LUT_SIZE;
        }
        
        public Gesture(Point[] points)
        {
        	this(points, true);
        }
        
        public Gesture(Point[] points, boolean computeLUT)
        {
            this.pointsRaw = points;
            
            normalize(computeLUT);
        }
        
        public Point[] getPoints() 
        {
			return points;
		}

        public void normalize(boolean computeLUT)
        {
            this.points = resample(pointsRaw, SAMPLING_RESOLUTION);
            this.points = scale(points);
            this.points = translateTo(points, centroid(points));
            
            if (computeLUT)
            {
                transformCoordinatesToIntegers();
                constructLUT();
            }
        }

        private Point[] scale(Point[] points)
        {
            float minx = Float.MAX_VALUE, miny = Float.MAX_VALUE, maxx = Float.MIN_VALUE, maxy = Float.MIN_VALUE;
            for (int i = 0; i < points.length; i++)
            {
                if (minx > points[i].X) minx = points[i].X;
                if (miny > points[i].Y) miny = points[i].Y;
                if (maxx < points[i].X) maxx = points[i].X;
                if (maxy < points[i].Y) maxy = points[i].Y;
            }

            Point[] newPoints = new Point[points.length];
            float scale = Math.max(maxx - minx, maxy - miny);
            for (int i = 0; i < points.length; i++) newPoints[i] = new Point((points[i].X - minx) / scale, (points[i].Y - miny) / scale, points[i].strokeID);
            return newPoints;
        }
        
        private Point[] translateTo(Point[] points, Point p)
        {
            Point[] newPoints = new Point[points.length];
            for (int i = 0; i < points.length; i++) newPoints[i] = new Point(points[i].X - p.X, points[i].Y - p.Y, points[i].strokeID);
            return newPoints;
        }

        private Point centroid(Point[] points)
        {
            float cx = 0, cy = 0;
            for (int i = 0; i < points.length; i++)
            {
                cx += points[i].X;
                cy += points[i].Y;
            }
            return new Point(cx / points.length, cy / points.length, 0);
        }
        
        public Point[] resample(Point[] points, int n)
        {
            Point[] newPoints = new Point[n];
            newPoints[0] = new Point(points[0].X, points[0].Y, points[0].strokeID);
            int numPoints = 1;

            float I = pathLength(points) / (n - 1);
            float D = 0;
            for (int i = 1; i < points.length; i++)
            {
                if (points[i].strokeID == points[i - 1].strokeID)
                {
                    float d = Geometry.EuclideanDistance(points[i - 1], points[i]);
                    if (D + d >= I)
                    {
                        Point firstPoint = points[i - 1];
                        while (D + d >= I)
                        {
                            float t = Math.min(Math.max((I - D) / d, 0.0f), 1.0f);
                            if (Float.isNaN(t)) t = 0.5f;
                            newPoints[numPoints++] = new Point(
                                (1.0f - t) * firstPoint.X + t * points[i].X,
                                (1.0f - t) * firstPoint.Y + t * points[i].Y,
                                points[i].strokeID
                            );

                            d = D + d - I;
                            D = 0;
                            firstPoint = newPoints[numPoints - 1];
                        }
                        D = d;
                    }
                    else D += d;
                }
            }

            if (numPoints == n - 1) newPoints[numPoints++] = new Point(points[points.length - 1].X, points[points.length - 1].Y, points[points.length - 1].strokeID);
            return newPoints;
        }

        private float pathLength(Point[] points)
        {
            float length = 0;
            for (int i = 1; i < points.length; i++) if (points[i].strokeID == points[i - 1].strokeID) length += Geometry.EuclideanDistance(points[i - 1], points[i]);
            return length;
        }

        private void transformCoordinatesToIntegers()
        {
            for (int i = 0; i < points.length; i++)
            {
            	points[i].intX = (int)((points[i].X + 1.0f) / 2.0f * (MAX_INT_COORDINATES - 1));
            	points[i].intY = (int)((points[i].Y + 1.0f) / 2.0f * (MAX_INT_COORDINATES - 1));
            }
        }

        private void constructLUT()
        {
            this.LUT = new int[LUT_SIZE][];
            for (int i = 0; i < LUT_SIZE; i++) LUT[i] = new int[LUT_SIZE];

            for (int i = 0; i < LUT_SIZE; i++) for (int j = 0; j < LUT_SIZE; j++)
            {
                int minDistance = Integer.MAX_VALUE;
                int indexMin = -1;
                for (int t = 0; t < points.length; t++)
                {
                    int row = points[t].intY / LUT_SCALE_FACTOR;
                    int col = points[t].intX / LUT_SCALE_FACTOR;
                    int dist = (row - i) * (row - i) + (col - j) * (col - j);
                    if (dist < minDistance)
                    {
                        minDistance = dist;
                        indexMin = t;
                    }
                }
                LUT[i][j] = indexMin;
            }
        }
    }

    public class QPointCloudRecognizer
    {
        public static boolean UseEarlyAbandoning = true;
        public static boolean UseLowerBounding = true;

        public static float greedyCloudMatch(Gesture gesture1, Gesture gesture2, float minSoFar)
        {
            int n = gesture1.points.length;
            float eps = 0.5f;
            int step = (int) Math.floor(Math.pow(n, 1.0f - eps));

            if (UseLowerBounding)
            {
                float[] LB1 = computeLowerBound(gesture1.points, gesture2.points, gesture2.LUT, step);
                float[] LB2 = computeLowerBound(gesture2.points, gesture1.points, gesture1.LUT, step);
                for (int i = 0, indexLB = 0; i < n; i += step, indexLB++)
                {
                    if (LB1[indexLB] < minSoFar) minSoFar = Math.min(minSoFar, cloudDistance(gesture1.points, gesture2.points, i, minSoFar));
                    if (LB2[indexLB] < minSoFar) minSoFar = Math.min(minSoFar, cloudDistance(gesture2.points, gesture1.points, i, minSoFar));  
                }
            }
            else
            {
                for (int i = 0; i < n; i += step)
                {
                    minSoFar = Math.min(minSoFar, cloudDistance(gesture1.points, gesture2.points, i, minSoFar));
                    minSoFar = Math.min(minSoFar, cloudDistance(gesture2.points, gesture1.points, i, minSoFar));   
                }
            }

            return minSoFar;
        }

        private static float[] computeLowerBound(Point[] points1, Point[] points2, int[][] LUT, int step)
        {
            int n = points1.length;
            float[] LB = new float[n / step + 1];
            float[] SAT = new float[n];

            LB[0] = 0;
            for (int i = 0; i < n; i++)
            {
                int index = LUT[points1[i].intY / Gesture.LUT_SCALE_FACTOR][points1[i].intX / Gesture.LUT_SCALE_FACTOR];
                float dist = Geometry.SqrEuclideanDistance(points1[i], points2[index]);
                SAT[i] = (i == 0) ? dist : SAT[i - 1] + dist;
                LB[0] += (n - i) * dist;
            }

            for (int i = step, indexLB = 1; i < n; i += step, indexLB++) LB[indexLB] = LB[0] + i * SAT[n - 1] - n * SAT[i - 1];
            return LB;
        }

        private static float cloudDistance(Point[] points1, Point[] points2, int startIndex, float minSoFar)
        {
            int n = points1.length;
            int[] indexesNotMatched = new int[n];
            for (int j = 0; j < n; j++) indexesNotMatched[j] = j;

            float sum = 0;
            int i = startIndex;
            int weight = n;
            int indexNotMatched = 0;
            do
            {
                int index = -1;
                float minDistance = Float.MAX_VALUE;
                for (int j = indexNotMatched; j < n; j++)
                {
                    float dist = Geometry.SqrEuclideanDistance(points1[i], points2[indexesNotMatched[j]]);
                    if (dist < minDistance)
                    {
                        minDistance = dist;
                        index = j;
                    }
                }
                indexesNotMatched[index] = indexesNotMatched[indexNotMatched];
                sum += (weight--) * minDistance;

                if (UseEarlyAbandoning)
                {
                    if (sum >= minSoFar) return sum;
                }

                i = (i + 1) % n;
                indexNotMatched++;
            } while (i != startIndex);
            return sum;
        }
    }
}
