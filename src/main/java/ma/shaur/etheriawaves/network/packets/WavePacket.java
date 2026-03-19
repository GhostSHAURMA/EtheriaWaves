package ma.shaur.etheriawaves.network.packets;

import java.util.Set;
import java.util.function.Supplier;

import it.mralxart.etheria.registry.CapabilityRegistry;
import ma.shaur.etheriawaves.EtheriaWaves;
import ma.shaur.etheriawaves.magic.spells.Recognizer.Point;
import ma.shaur.etheriawaves.network.Networking;
import ma.shaur.etheriawaves.world.level.MagicGameRules;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraftforge.network.NetworkEvent;

public class WavePacket
{
	private final Point[] points;
	
	public WavePacket(final Point[] points)
	{
		this.points = points;
	}
	
	public static WavePacket decode(FriendlyByteBuf buf)
	{
		final Point[] points = new Point[buf.readInt()];
		
		for(int i = 0; i < points.length; i++)
		{
			points[i] = new Point(buf.readFloat(), buf.readFloat(), buf.readInt());
		}
		
		return new WavePacket(points);
	}
	
	public void encode(FriendlyByteBuf buf)
	{
		buf.writeInt(points.length);
		
		for(int i = 0; i < points.length; i++)
		{
			buf.writeFloat(points[i].getX());
			buf.writeFloat(points[i].getY());
			buf.writeInt(points[i].getStrokeID());
		}
	}
	
	public void handle(Supplier<NetworkEvent.Context> ctx) 
	{
		ServerPlayer player = ctx.get().getSender();
		Set<String> spells = CapabilityRegistry.getCap(player).getActiveSpells().keySet();
		String name = EtheriaWaves.getSymbolStorage().recognizeSpell(points);
		if(player.gameMode.getGameModeForPlayer() != GameType.CREATIVE && !spells.contains(name) && !player.level().getGameRules().getBoolean(MagicGameRules.RULE_UNLIMITED_SPELLS)) name = "";
		Networking.sendTo(ctx.get().getSender(), new SpellNamePacket(name));
	}
}
