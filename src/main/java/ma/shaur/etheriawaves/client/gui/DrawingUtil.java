package ma.shaur.etheriawaves.client.gui;

import org.joml.Matrix4f;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;

public class DrawingUtil 
{
	public static boolean line(NativeImage pixels, int x0, int y0, int x1, int y1, int color) 
	{
		return line(pixels, x0, y0, x1, y1, color, false);
	}
	
	public static boolean line(NativeImage pixels, int x0, int y0, int x1, int y1, int color, boolean force) 
	{
	    boolean steep = Math.abs(y1 - y0) > Math.abs(x1 - x0);

	    if (steep) 
	    {
	    	x0 = x0 ^ y0;
	    	y0 = x0 ^ y0;
	    	x0 = x0 ^ y0;
	        
	    	x1 = x1 ^ y1;
	    	y1 = x1 ^ y1;
	    	x1 = x1 ^ y1;
	    }

	    if (x0 > x1) 
	    {
	    	x0 = x0 ^ x1;
	    	x1 = x0 ^ x1;
	    	x0 = x0 ^ x1;
	        
	    	y0 = y0 ^ y1;
	    	y1 = y0 ^ y1;
	    	y0 = y0 ^ y1;
	    }

	    int height = steep ? pixels.getWidth() : pixels.getHeight();
	    int width = steep ? pixels.getHeight() : pixels.getWidth();
	    int dx = x1 - x0;
	    int dy = Math.abs(y1 - y0);
	    int error = dx / 2;
	    int ystep = (y0 < y1) ? 1 : -1;
	    int y = y0;

	    for (int x = x0; x <= x1; ++x) 
	    {
        	if(x >= 0 && x < width && y >= 0 && y < height)
        	{
		        if (steep) 
		        {
		        	if(!force && pixels.getPixelRGBA(y, x) == 0x000000FF) return false;
		        	pixels.setPixelRGBA(y, x, color);
		        } 
		        else 
		        {
		        	if(!force && pixels.getPixelRGBA(x, y) == 0x000000FF) return false;
		        	pixels.setPixelRGBA(x, y, color);
		        }
        	}

	        error -= dy;

	        if (error < 0) 
	        {
	            y += ystep;
	            error += dx;
	        }
	    }
	    
	    return true;
	}

	public static void drawDynamicTexture(GuiGraphics guiGraphics, int x, int y, int z, int xMax, int yMax, DynamicTexture texture)
	{
		RenderSystem.setShaderTexture(0, texture.getId());
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		Matrix4f matrix4f = guiGraphics.pose().last().pose();
		BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
		bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		int x1 = x + 1, y1 = y + 1, x2 = xMax - 1, y2 = yMax - 1;
		bufferbuilder.vertex(matrix4f, (float) x1, (float) y1, (float) z).uv(0, 0).endVertex();
		bufferbuilder.vertex(matrix4f, (float) x1, (float) y2, (float) z).uv(0, 1).endVertex();
		bufferbuilder.vertex(matrix4f, (float) x2, (float) y2, (float) z).uv(1, 1).endVertex();
		bufferbuilder.vertex(matrix4f, (float) x2, (float) y1, (float) z).uv(1, 0).endVertex();
		BufferUploader.drawWithShader(bufferbuilder.end());
	}

}
