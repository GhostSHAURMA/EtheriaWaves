package ma.shaur.etheriawaves.network.packets;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.function.Supplier;

import ma.shaur.etheriawaves.EtheriaWaves;
import ma.shaur.etheriawaves.magic.spells.Recognizer.Gesture;
import ma.shaur.etheriawaves.magic.spells.Recognizer.Point;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class SymbolListPacket
{
	private final HashMap<String, Gesture> symbols;
	
	public SymbolListPacket(final HashMap<String, Gesture> symbols)
	{
		this.symbols = symbols;
	}
	
	public static SymbolListPacket decode(FriendlyByteBuf buf)
	{
		final HashMap<String, Gesture> symbols = new HashMap<String, Gesture>();
		
		int size = buf.readInt();
		for(int i = 0; i < size; i++)
		{
			final byte[] spell = new byte[buf.readInt()];

			for(int j = 0; j < spell.length; j++)
			{
				spell[j] = buf.readByte();
			}
			
			final Point[] points = new Point[buf.readInt()];
			
			for(int j = 0; j < points.length; j++)
			{
				points[j] = new Point(buf.readFloat(), buf.readFloat(), buf.readInt());
			}
			
			symbols.put(new String(spell, StandardCharsets.UTF_8), new Gesture(points));
		}
		
		return new SymbolListPacket(symbols);
	}
	
	public void encode(FriendlyByteBuf buf)
	{
		buf.writeInt(symbols.size());
		
		for(String name : symbols.keySet())
		{
			final byte[] spell = name.getBytes(StandardCharsets.UTF_8);
			
			buf.writeInt(spell.length);
			buf.writeByteArray(spell);
			
			final Point[] points = symbols.get(name).getPoints();
			
			for(int j = 0; j < points.length; j++)
			{
				buf.writeFloat(points[j].getX());
				buf.writeFloat(points[j].getY());
				buf.writeInt(points[j].getStrokeID());
			}
		}
	}
	
	public void handle(Supplier<NetworkEvent.Context> ctx) 
	{
		EtheriaWaves.getSymbolStorage().setStorage(symbols);
	}
}
