package ma.shaur.etheriawaves.world.level;

import net.minecraft.world.level.GameRules;

public class MagicGameRules
{
	public static final GameRules.Key<GameRules.BooleanValue> RULE_UNLIMITED_SPELLS = GameRules.register("unlimitedSpells", GameRules.Category.PLAYER, GameRules.BooleanValue.create(false));

	public static void init() { }
}
