package ma.shaur.etheriawaves.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import it.mralxart.etheria.magic.magemicon.MageMicon;
import it.mralxart.etheria.magic.magemicon.Page;
import ma.shaur.etheriawaves.EtheriaWaves;
import ma.shaur.etheriawaves.client.gui.widgets.PracticeSymbol;
import ma.shaur.etheriawaves.magic.spells.Recognizer.Gesture;

@Mixin(value = MageMicon.class, remap = false)
public class MageMiconMixin
{
	@Redirect(method = "m_7856_()V", at = @At(value = "INVOKE", target = "Lit/mralxart/etheria/magic/magemicon/Page;init(Lit/mralxart/etheria/magic/magemicon/MageMicon;III)V"))
	private void overrideSymbol(Page page, MageMicon micon, int x, int y, int pageIndex)
	{
		String text = page.getText();
		if(text == null || (!text.startsWith("%") && !text.endsWith("%")))
		{
			page.init(micon, x, y, pageIndex);
			return;
		}
		
		String spell = text.substring(1, text.length() - 1);
		Gesture symbol = EtheriaWaves.getSymbolStorage().getSymbols().get(spell);

		micon.m_142416_(new PracticeSymbol(x + 20, y + 40, 120, 120, symbol));
		return;
	}
}
