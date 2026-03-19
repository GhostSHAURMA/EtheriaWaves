package ma.shaur.etheriawaves.network.packets;

import java.util.function.Supplier;

import ma.shaur.etheriawaves.client.ClientSpellHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class SpellNamePacket
{
	private final String name;
	
	public SpellNamePacket(final String name)
	{
		this.name = name;
	}
	
	public static SpellNamePacket decode(FriendlyByteBuf buf)
	{
		return new SpellNamePacket(buf.readUtf());
	}
	
	public void encode(FriendlyByteBuf buf)
	{
		buf.writeUtf(name);
	}
	
	public void handle(Supplier<NetworkEvent.Context> ctx) 
	{
		if(name != null && name.length() > 0) 
		{
			ClientSpellHandler.setCurrentSpell(name);
		}
		else
		{
			
		}
	}
}
