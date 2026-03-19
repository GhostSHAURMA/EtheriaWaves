package ma.shaur.etheriawaves.network;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import it.mralxart.etheria.network.packets.CastSpellPacket;
import ma.shaur.etheriawaves.EtheriaWaves;
import ma.shaur.etheriawaves.network.packets.SpellNamePacket;
import ma.shaur.etheriawaves.network.packets.SymbolListPacket;
import ma.shaur.etheriawaves.network.packets.WavePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class Networking
{
    private static final Logger LOGGER = LogUtils.getLogger();
	private static SimpleChannel INSTANCE;
	private static int ID = 0;

	public static int nextID()
	{
		return ID++;
	}

	public static void init()
	{
		INSTANCE = NetworkRegistry.newSimpleChannel(ResourceLocation.fromNamespaceAndPath(EtheriaWaves.MODID, "networking"), () ->
		{
			return "1.0";
		}, (s) ->
		{
			return true;
		}, (s) ->
		{
			return true;
		});
		INSTANCE.messageBuilder(WavePacket.class, nextID(), NetworkDirection.PLAY_TO_SERVER).encoder(WavePacket::encode).decoder(WavePacket::decode).consumerMainThread(WavePacket::handle).add();
		INSTANCE.messageBuilder(SymbolListPacket.class, nextID(), NetworkDirection.PLAY_TO_SERVER).encoder(SymbolListPacket::encode).decoder(SymbolListPacket::decode).consumerMainThread(SymbolListPacket::handle).add();
		INSTANCE.messageBuilder(SpellNamePacket.class, nextID(), NetworkDirection.PLAY_TO_CLIENT).encoder(SpellNamePacket::encode).decoder(SpellNamePacket::decode).consumerMainThread(SpellNamePacket::handle).add();
		INSTANCE.messageBuilder(CastSpellPacket.class, nextID()).encoder(CastSpellPacket::encode).decoder(CastSpellPacket::new).consumerMainThread(CastSpellPacket::handle).add();
	}
	
	public static void sendToServer(Object object)
	{
		LOGGER.debug("Sent {} to server", object.getClass().getName());
		INSTANCE.sendToServer(object);
	}
	
	public static void sendTo(ServerPlayer player, Object object)
	{
		LOGGER.debug("Sent {} to {}", object.getClass().getName(), player.getName().getString());
		INSTANCE.sendTo(object, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
	}

	public static void sendToAll(Object packet) 
	{	
      if (EffectiveSide.get() == LogicalSide.SERVER) 
      {
         INSTANCE.send(PacketDistributor.ALL.noArg(), packet);
      }
   }
}
