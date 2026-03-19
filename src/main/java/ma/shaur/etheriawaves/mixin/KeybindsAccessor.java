package ma.shaur.etheriawaves.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import it.mralxart.etheria.client.Keybinds;
import net.minecraft.client.KeyMapping;

@Mixin(value = Keybinds.class, remap = false)
public interface KeybindsAccessor
{
	@Accessor("SKILLS_OPEN_KEY")
	static KeyMapping getSKILLS_OPEN_KEY()
	{
		throw new AssertionError();
	}
}
