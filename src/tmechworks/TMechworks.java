package tmechworks;

import java.util.logging.Logger;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import tmechworks.client.SignalTetherWorldOverlayRenderer;
import tmechworks.common.CommonProxy;
import tmechworks.common.MechContent;
import tmechworks.lib.ConfigCore;
import tmechworks.lib.Repo;
import tmechworks.lib.TMechworksRegistry;
import tmechworks.lib.multiblock.MultiblockEventHandler;
import tmechworks.lib.util.TabTools;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = Repo.modId, name = Repo.modName, version = Repo.modVer, dependencies = "required-after:TConstruct")
@NetworkMod(serverSideRequired = false, clientSideRequired = true, channels = { "TMechworks" }, packetHandler = tmechworks.network.PacketHandler.class)
public class TMechworks {

    // Shared mod logger
    public static final Logger logger = Logger.getLogger("TMechworks");

    /* Instance of this mod, used for grabbing prototype fields */
    @Instance("TMechworks")
    public static TMechworks instance;
    /* Proxies for sides, used for graphics processing */
    @SidedProxy(clientSide = "tmechworks.client.ClientProxy", serverSide = "tmechworks.common.CommonProxy")
    public static CommonProxy proxy;

    public TMechworks ()
    {
        logger.setParent(FMLCommonHandler.instance().getFMLLogger());
    }

    @EventHandler
    public void preInit (FMLPreInitializationEvent event)
    {
        ConfigCore.loadConfig(new Configuration(event.getSuggestedConfigurationFile()));

        
        TMechworksRegistry.Mechworks = new TabTools("TMechworks");

        content = new MechContent();

        proxy.registerRenderer();
        proxy.registerTickHandler();
        
        MinecraftForge.EVENT_BUS.register(new MultiblockEventHandler());
    }

    @EventHandler
    public void init (FMLInitializationEvent event)
    {
        if (event.getSide() == Side.CLIENT)
        {
        	MinecraftForge.EVENT_BUS.register(new SignalTetherWorldOverlayRenderer());
        }
    }

    @EventHandler
    public void postInit (FMLPostInitializationEvent evt)
    {
        proxy.postInit();

    }

    public static MechContent content;
}
