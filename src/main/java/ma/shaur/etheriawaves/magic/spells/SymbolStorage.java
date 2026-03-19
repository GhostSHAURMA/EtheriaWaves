package ma.shaur.etheriawaves.magic.spells;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;

import ma.shaur.etheriawaves.magic.spells.Recognizer.Gesture;
import ma.shaur.etheriawaves.magic.spells.Recognizer.Point;
import ma.shaur.etheriawaves.magic.spells.Recognizer.QPointCloudRecognizer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.storage.loot.Deserializers;

public class SymbolStorage extends SimpleJsonResourceReloadListener
{
	private static final Logger LOGGER = LogUtils.getLogger();
    public static final Gson GSON_INSTANCE = Deserializers.createFunctionSerializer().create();
    
	private HashMap<String, Gesture> symbols = null;
	
	public SymbolStorage() 
	{
		super(GSON_INSTANCE, "spells");
	}
	
	public void setStorage(HashMap<String, Gesture> symbols) 
	{
		this.symbols = symbols;
	}
	
	public HashMap<String, Gesture> getSymbols() 
	{
		return new HashMap<String, Gesture>(symbols);
	}
	
	public String recognizeSpell(final Point[] points)
	{
    	Pair<String, Float> result = new Pair<>(null, Float.MAX_VALUE);
    	
    	Gesture candidate = new Gesture(points);
    	
        for (String spell : symbols.keySet())
        {
            float dist = QPointCloudRecognizer.greedyCloudMatch(candidate, symbols.get(spell), result.getSecond());
            
            if(dist < result.getSecond()) result = new Pair<>(spell, dist);
			LOGGER.info("Dist to {} is {}", spell, dist);
        }
        
		return result.getSecond() > 16 ? "" : result.getFirst();
	}

	public float distance(Gesture gesture, Gesture symbol) 
	{
		return QPointCloudRecognizer.greedyCloudMatch(gesture, symbol, Float.MAX_VALUE);
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> jsonFiles, ResourceManager resourceManager, ProfilerFiller profilerFiller)
	{
		symbols = new HashMap<>();
		for(Entry<ResourceLocation, JsonElement> entry : jsonFiles.entrySet())
		{
			try
			{
				readJSON(entry, symbols);
			} 
			catch (Exception exception)
			{
				LOGGER.error("Parsing error loading spell symbol {}: {}", entry.getKey(), exception.getMessage());
				LOGGER.debug("Full stack: ", exception);
			}
		}
	}
	
	private static void readJSON(Entry<ResourceLocation, JsonElement> entry, HashMap<String, Gesture> symbols)
	{
		JsonObject jsonObject = GsonHelper.convertToJsonObject(entry.getValue(), "symbol");
		
		JsonArray strokes = GsonHelper.getAsJsonArray(jsonObject, "strokes");
		ArrayList<Point> points = new ArrayList<>();
		
		for(int i = 0; i < strokes.size(); i++)
		{
			JsonArray stroke = GsonHelper.convertToJsonArray(strokes.get(i), "stroke");
			if(stroke.size() % 2 != 0) throw new JsonSyntaxException("Each stroke should have an even ammount of number to represent x and y coordinates");
			for(int j = 0; (j + 1) < stroke.size(); j += 2)
			{
				points.add(new Point(GsonHelper.convertToFloat(stroke.get(j), "X" + j / 2), GsonHelper.convertToFloat(stroke.get(j + 1), "Y" + j / 2), i));
			}
		}
		
		symbols.put(GsonHelper.getAsString(jsonObject, "spell"), new Gesture(points.toArray(Point[]::new)));
	}
}
