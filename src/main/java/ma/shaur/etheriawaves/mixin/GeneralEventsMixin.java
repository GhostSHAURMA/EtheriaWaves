package ma.shaur.etheriawaves.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import it.mralxart.etheria.client.gui.SpellsHud.GeneralEvents;
import net.minecraft.world.entity.player.Player;

@Mixin(value = GeneralEvents.class, remap = false)
public class GeneralEventsMixin
{
	@Redirect(method = "onPlayerTick", at = @At(value = "INVOKE", target = "handleSpellSelection(Lnet/minecraft/world/entity/player/Player;)V"))
	private static void handleSpellSelection(Player player)
	{
		
	}
	
	@Redirect(method = "onPlayerTick", at = @At(value = "INVOKE", target = "handleSpellDeselection()V"))
	private static void handleSpellDeselection()
	{
		
	}
}
