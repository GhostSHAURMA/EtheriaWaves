package ma.shaur.etheriawaves.client.gui;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.gui.GuiGraphics;

@Mixin(GuiGraphics.class)
public interface GuiGraphicsInvoker 
{	
	//@Invoker("submitBlit")
	//public void invokeSubmitBlit(RenderPipeline pipeline, GpuTextureView atlasTexture, int x0, int y0, int x1, int y1, float u0, float u1, float v0, float v1, int color);
}
