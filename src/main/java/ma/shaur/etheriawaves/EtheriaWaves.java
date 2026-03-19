package ma.shaur.etheriawaves;

import ma.shaur.etheriawaves.magic.spells.SymbolStorage;
import ma.shaur.etheriawaves.network.Networking;
import ma.shaur.etheriawaves.world.level.MagicGameRules;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(EtheriaWaves.MODID)
public class EtheriaWaves
{
    public static final String MODID = "etheriawaves";
    private static SymbolStorage SYMBOL_STORAGE = new SymbolStorage();
    
    public EtheriaWaves(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        Networking.init();
        modEventBus.addListener(this::commonSetup);
        
        MinecraftForge.EVENT_BUS.addListener(this::addReloadListenerEvent);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
    	MagicGameRules.init();
    }

    private void addReloadListenerEvent(final AddReloadListenerEvent event)
    {
    	event.addListener(SYMBOL_STORAGE);
    }
    
    public static SymbolStorage getSymbolStorage()
	{
		return SYMBOL_STORAGE;
	}
}
