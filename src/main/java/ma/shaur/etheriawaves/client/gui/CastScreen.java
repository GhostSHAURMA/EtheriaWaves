package ma.shaur.etheriawaves.client.gui;

import java.util.ArrayList;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.NativeImage;

import ma.shaur.etheriawaves.magic.spells.Recognizer.Point;
import ma.shaur.etheriawaves.network.Networking;
import ma.shaur.etheriawaves.network.packets.WavePacket;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.DynamicTexture;

public class CastScreen extends Screen 
{
	private final static int SPELL_HEIGHT = 120;

	private final int backgroundColor = 0x2F1A1A1A;
	private final int outlineColor = 0x600A0A0A;
	private int y = 0;
	private int x = 0;
	private int yMax = 0;
	private int xMax = 0;
	private DynamicTexture spell;
	private ArrayList<Point> points = new ArrayList<>();
	private int stroke = 0;
	
	public CastScreen() 
	{
		super(GameNarrator.NO_TITLE);
	}
	
	@Override
	protected void init() 
	{
		y = height / 20;
		x = width / 20;
		yMax = y * 19;
		xMax = x * 19;
		
		spell = new DynamicTexture((int) (SPELL_HEIGHT * ((float) ((xMax - x)) / (yMax - y))), SPELL_HEIGHT, true);
		
		spell.upload();
	}
	
	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) 
	{
		guiGraphics.fill(x, y, xMax, yMax, backgroundColor);
		guiGraphics.renderOutline(x, y, xMax - x, yMax - y, outlineColor);
		DrawingUtil.drawDynamicTexture(guiGraphics, x, y, 0, xMax, yMax, spell);
	}
	
	@Override
	public void renderBackground(GuiGraphics guiGraphics) 
	{
		
	}
	
	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY)
	{
		if(button == InputConstants.MOUSE_BUTTON_LEFT && mouseX - deltaX >= x && mouseX - deltaX < xMax && mouseY - deltaY >= y && mouseY - deltaY < yMax)
		{
			float width = xMax - x - 2, height = yMax - y - 2;
			float xRel = (float) (mouseX - x - 1) / width, yRel = (float) (mouseY - y - 1) / height;
			float xPrevRel = (float) (mouseX - deltaX - x - 1) / width, yPrevRel = (float) (mouseY - deltaY - y - 1) / height;
			int tWidth = spell.getPixels().getWidth(), tHeight = spell.getPixels().getHeight();
			
			int x0 = (int) (xPrevRel * tWidth), x1 = (int) (xRel * tWidth), y0 = (int) (yPrevRel * tHeight), y1 = (int) (yRel * tHeight);

			points.add(new Point(x0, y0, stroke));
			points.add(new Point(x1, y1, stroke));
			
			DrawingUtil.line(spell.getPixels(), x0, y0, x1, y1, 0xFF0000FF, true);
			
			spell.upload();
			
			return true;
		}
		return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}
	
	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button)
	{
		if(button == InputConstants.MOUSE_BUTTON_RIGHT)
		{
			clear();
			return true;
		}
		if(button == InputConstants.MOUSE_BUTTON_LEFT)
		{
			stroke++;
		}
		
		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean isPauseScreen() 
	{
		return false;
	}
	
	@Override
	public boolean isMouseOver(double mouseX, double mouseY) 
	{
		return true;
	}
	
	@Override
	public boolean keyPressed(int p_96552_, int p_96553_, int p_96554_)
	{
		return super.keyPressed(p_96552_, p_96553_, p_96554_);
	}
	
	@Override
	public boolean shouldCloseOnEsc() 
	{
		return false;
	}

	@Override
	public void removed() 
	{
		super.removed();
		
		if(spell != null) spell.close();
		
		Networking.sendToServer(new WavePacket(points.toArray(new Point[0])));
	}
	
	private void clear()
	{
		points = new ArrayList<>();
		NativeImage pixels = spell.getPixels();
		
		for(int x = 0; x < pixels.getWidth(); x++)
		{
			for(int y = 0; y < pixels.getHeight(); y++)
			{
				if(pixels.getPixelRGBA(x, y) == 0xFF0000FF) 
				{
					pixels.setPixelRGBA(x, y, 0);
				}
			}
		}

		spell.upload();
	}
	
	@Override
	public GuiEventListener getFocused()
	{
		return null;
	}
	
	public ArrayList<Point> getPoints()
	{
		return points;
	}
}
