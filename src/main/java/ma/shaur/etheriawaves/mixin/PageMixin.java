package ma.shaur.etheriawaves.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import it.mralxart.etheria.magic.magemicon.Page;

@Mixin(value = Page.class, remap = false)
public class PageMixin
{
	@Shadow private String text;
    @Shadow private Page.Type type;
	
	@Inject(method = "render", cancellable = true, at = @At(value = "INVOKE", target = "Lit/mralxart/etheria/magic/magemicon/Page;renderTitle(Lnet/minecraft/client/gui/GuiGraphics;Lit/mralxart/etheria/magic/magemicon/MageMicon;II)V", shift = At.Shift.AFTER))
	private void cancelText(CallbackInfo ci)
	{
		if(type.equals(Page.Type.TEXT) && text != null && text.startsWith("%") && text.endsWith("%")) ci.cancel();
	}
}
