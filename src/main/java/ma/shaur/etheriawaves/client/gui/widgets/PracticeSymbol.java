package ma.shaur.etheriawaves.client.gui.widgets;

import java.util.ArrayList;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.NativeImage;

import it.mralxart.etheria.client.gui.base.ICustomRenderWidget;
import it.mralxart.etheria.client.gui.base.WidgetBase;
import ma.shaur.etheriawaves.EtheriaWaves;
import ma.shaur.etheriawaves.client.gui.DrawingUtil;
import ma.shaur.etheriawaves.magic.spells.Recognizer.Gesture;
import ma.shaur.etheriawaves.magic.spells.Recognizer.Point;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;

public class PracticeSymbol extends WidgetBase implements ICustomRenderWidget
{	
	private final Gesture gesture;
	private DynamicTexture background;
	private DynamicTexture drawn;
	private int stroke = 0;
	private ArrayList<Point> points = new ArrayList<>();
	private float distance = 176;
	
	public PracticeSymbol(int x, int y, int width, int height, Gesture gesture) 
	{
		super(x, y, width, height);
		this.gesture = gesture;

		drawn = new DynamicTexture(width, height, true);
		drawn.upload();

		background = new DynamicTexture(width, height, true);

		NativeImage pixels = background.getPixels();
		//pixels.fillRect(0, 0, width, height, 0x2F1A1A1A);
		Point[] points = gesture.getPoints();
		for(int i = 0; i < points.length; i += 2) // I spent way too much time trying to make this look ok-enough
		{
			DrawingUtil.line(pixels, 1 + (int) ((points[i].getX() + 0.5f) * (width - 2)), 1 + (int) ((points[i].getY() + 0.5f) * (height - 2)), 1 + (int) ((points[i + 1].getX() + 0.5f) * (width - 2)), 1 + (int) ((points[i + 1].getY() + 0.5f) * (height - 2)), 0x900A0A0A, true);
		}
		
		background.upload();
	}
	
	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY)
	{
		if(button == InputConstants.MOUSE_BUTTON_LEFT && mouseX - deltaX >= getX() && mouseX - deltaX < getX() + width && mouseY - deltaY >= getY() && mouseY - deltaY < getY() + height)
		{
			float xRel = (float) (mouseX - getX() - 1) / width, yRel = (float) (mouseY - getY() - 1) / height;
			float xPrevRel = (float) (mouseX - deltaX - getX() - 1) / width, yPrevRel = (float) (mouseY - deltaY - getY() - 1) / height;
			int tWidth = drawn.getPixels().getWidth(), tHeight = drawn.getPixels().getHeight();
			
			int x0 = (int) (xPrevRel * tWidth), x1 = (int) (xRel * tWidth), y0 = (int) (yPrevRel * tHeight), y1 = (int) (yRel * tHeight);

			points.add(new Point(x0, y0, stroke));
			points.add(new Point(x1, y1, stroke));
			
			DrawingUtil.line(drawn.getPixels(), x0, y0, x1, y1, 0xFF0000FF, true);
			
			drawn.upload();
			
			return true;
		}
		return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}

	@Override
	protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) 
	{
		guiGraphics.enableScissor(getX(), getY(), getX() + width, getY() + height);
		DrawingUtil.drawDynamicTexture(guiGraphics, getX(), getY(), 0, getX() + width, getY() + height, background);
		DrawingUtil.drawDynamicTexture(guiGraphics, getX(), getY(), 0, getX() + width, getY() + height, drawn);
		guiGraphics.disableScissor();
		
		Font font = Minecraft.getInstance().font;
		Component line = Component.literal("Accuracy " + (int) (((16. - distance) / 16) * 100) + "%");
		guiGraphics.drawString(font, line, getX() + width / 2 - font.width(line) / 2, getY() + height, 0x2F1A1A1A);
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) 
	{
		
	}
	
	private void recongised(Point[] points) 
	{
		this.points = new ArrayList<>();
		
		NativeImage pixels = drawn.getPixels();
		
		for(int i = 0; i < points.length; i += 2)
		{
			DrawingUtil.line(pixels, (int) points[i].getX(), (int) points[i].getY(), (int) points[i + 1].getX(), (int) points[i + 1].getY(), 0x900A0A0A, true);
		}

		 drawn.upload();
	}
	
	private void clear()
	{
		points = new ArrayList<>();
		
		NativeImage pixels = drawn.getPixels();
		pixels.fillRect(0, 0, pixels.getWidth(), pixels.getHeight(), 0x0);
		drawn.upload();
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
			
			if(points.size() > 0)
			{
				Point[] pointArr = points.toArray(Point[]::new);
				distance = Math.min(EtheriaWaves.getSymbolStorage().distance(new Gesture(pointArr), gesture), 176);
				if(distance < 16) recongised(pointArr);
			}
		}
		
		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean isHover()
	{
		return false;
	}

	@Override
	public void render(GuiGraphics var1, float var2, int var3, int var4)
	{
		renderWidget(var1, var3, var4, var2);
	}

	@Override
	public void onPress()
	{
		
	}
}
