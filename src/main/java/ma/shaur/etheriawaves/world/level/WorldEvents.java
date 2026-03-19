package ma.shaur.etheriawaves.world.level;

import ma.shaur.etheriawaves.EtheriaWaves;
import ma.shaur.etheriawaves.network.Networking;
import ma.shaur.etheriawaves.network.packets.SymbolListPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = EtheriaWaves.MODID, value = Dist.DEDICATED_SERVER)
public class WorldEvents
{
	@SubscribeEvent
	public void onJoin(PlayerLoggedInEvent event)
	{
		Networking.sendTo(((ServerPlayer) event.getEntity()), new SymbolListPacket(EtheriaWaves.getSymbolStorage().getSymbols()));
	}
}
