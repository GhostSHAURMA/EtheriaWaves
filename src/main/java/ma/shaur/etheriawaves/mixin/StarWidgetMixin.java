package ma.shaur.etheriawaves.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import it.mralxart.etheria.client.gui.widgets.StarType;
import it.mralxart.etheria.client.gui.widgets.StarWidget;
import it.mralxart.etheria.magemicon.data.StarInfo;
import it.mralxart.etheria.magic.magemicon.MageMicon;
import it.mralxart.etheria.magic.magemicon.Page;

@Mixin(value = StarWidget.class, remap = false)
public class StarWidgetMixin
{
    @Shadow public StarInfo info;
    @Shadow private StarType type;
	
	@Inject(method = "m_5691_()V", at = @At(value = "INVOKE", target = "Lit/mralxart/etheria/magic/magemicon/MageMiconUtils;getEmptyChapter(Ljava/lang/String;)Lit/mralxart/etheria/magic/magemicon/Chapter;", shift = At.Shift.BY, by = 3), locals = LocalCapture.CAPTURE_FAILHARD)
	public void addPage(CallbackInfo ci, MageMicon micon)
	{
		if(type != StarType.SPELL) return;

		Page page3 = new Page(this.info.getId(), Page.Type.TEXT);
		page3.setText("%" + this.info.getId() + "%");
		Page page4 = new Page(this.info.getId(), Page.Type.TEXT);
		page4.setText("<--------\nTry out drawing the spell right here!\n(get at least 0% to cast)");
		micon.chapter.addPage(micon.page1);
		micon.chapter.addPage(micon.page2);
		micon.chapter.addPage(page3);
		micon.chapter.addPage(page4);
	}
}
