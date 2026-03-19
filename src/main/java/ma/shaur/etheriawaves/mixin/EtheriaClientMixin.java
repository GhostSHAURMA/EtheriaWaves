package ma.shaur.etheriawaves.mixin;

import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.mojang.logging.LogUtils;

import it.mralxart.etheria.EtheriaClient;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

@Mixin(value = EtheriaClient.class, remap = false)
public class EtheriaClientMixin
{
    private static final Logger LOGGER = LogUtils.getLogger();
    
	@ModifyArg(method = "onOverlayRegistry(Lnet/minecraftforge/client/event/RegisterGuiOverlaysEvent;)V", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/event/RegisterGuiOverlaysEvent;registerBelowAll(Ljava/lang/String;Lnet/minecraftforge/client/gui/overlay/IGuiOverlay;)V"), index = 1)
	private static IGuiOverlay replaceSpellsHud(IGuiOverlay guiOverlay)
	{
    	LOGGER.info("I AM EtheriaClientMixin AND I RAN");
		
		return (ForgeGui, guiGraphics, partialTick, screenWidth, screenHeight) -> {};
	}
}
