package ma.shaur.etheriawaves.client;

import com.mojang.blaze3d.platform.InputConstants;

import it.mralxart.etheria.items.CatalystItem;
import it.mralxart.etheria.magic.spells.TickingSpells;
import it.mralxart.etheria.network.packets.CastSpellPacket;
import it.mralxart.etheria.registry.ItemRegistry;
import it.mralxart.etheria.registry.KeybindsRegistry;
import it.mralxart.etheria.utils.ItemUtils;
import ma.shaur.etheriawaves.EtheriaWaves;
import ma.shaur.etheriawaves.client.gui.CastScreen;
import ma.shaur.etheriawaves.network.Networking;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = EtheriaWaves.MODID, value = Dist.CLIENT)
public class ClientSpellHandler
{
	private static volatile String CURRENT_SPELL = null;

	@SubscribeEvent
	public static void keyPressed(InputEvent.Key event)
	{
		Minecraft minecraft = Minecraft.getInstance();

		if(minecraft.level == null || minecraft.player == null ||  event.getKey() != KeybindsRegistry.SPELLS.getKey().getValue()) return;
		if(!ItemUtils.onHand(minecraft.player, ItemRegistry.MAGIC_STICK.get()) && !ItemUtils.onHand(minecraft.player, item -> item instanceof CatalystItem)) return;
		
		if(event.getAction() == InputConstants.PRESS && minecraft.screen == null)
		{
			minecraft.setScreen(new CastScreen());
		} 
		else if(event.getAction() == InputConstants.RELEASE && minecraft.screen != null && minecraft.screen instanceof CastScreen castScreen)
		{
			castScreen.getPoints();
			minecraft.setScreen(null);
		}
	}

	@SubscribeEvent
	public static void mousePressed(InputEvent.MouseButton.Pre event)
	{
		Minecraft minecraft = Minecraft.getInstance();

		if(minecraft.level == null || minecraft.screen != null || minecraft.player == null || CURRENT_SPELL == null) return;
		if(!ItemUtils.onHand(minecraft.player, ItemRegistry.MAGIC_STICK.get()) && !ItemUtils.onHand(minecraft.player, item -> item instanceof CatalystItem)) return;
		
		event.setCanceled(true);

		Networking.sendToServer(new CastSpellPacket(CURRENT_SPELL));

		CURRENT_SPELL = null;
	}

	@SubscribeEvent
	public static void playerTick(TickEvent.PlayerTickEvent event)
	{
		if(event.side != LogicalSide.CLIENT || event.phase != Phase.END || CURRENT_SPELL == null) return;

		TickingSpells.tick(event.player, CURRENT_SPELL);
	}
	
	@SubscribeEvent
	public static void swapSlot(InputEvent.MouseScrollingEvent event)
	{
		Minecraft minecraft = Minecraft.getInstance();
		if(minecraft.level == null || minecraft.screen != null || minecraft.player == null || CURRENT_SPELL == null) return;
		if(!ItemUtils.onHand(minecraft.player, ItemRegistry.MAGIC_STICK.get()) && !ItemUtils.onHand(minecraft.player, item -> item instanceof CatalystItem)) return;
		
		CURRENT_SPELL = null;
	}

	public static void setCurrentSpell(String name)
	{
		CURRENT_SPELL = name;
	}
}
