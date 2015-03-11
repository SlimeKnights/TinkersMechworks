package tmechworks;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

import org.apache.logging.log4j.Logger;

import tmechworks.client.SignalTetherWorldOverlayRenderer;
import tmechworks.command.BlockInfoCommand;
import tmechworks.common.CommonProxy;
import tmechworks.common.MechContent;
import tmechworks.lib.ConfigCore;
import tmechworks.lib.Repo;
import tmechworks.lib.TMechworksRegistry;
import tmechworks.lib.multiblock.MultiblockEventHandler;
import tmechworks.lib.multiblock.MultiblockServerTickHandler;
import tmechworks.lib.util.TabTools;
import tmechworks.network.packet.PacketPipeline;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = Repo.modId, name = Repo.modName, version = Repo.modVer, dependencies = Repo.modDeps)
public class TMechworks
{

    /* Shared mod logger */
    public static Logger logger;

    /* Mod content holder */
    public static MechContent content;

    /* Instance of this mod, used for grabbing prototype fields */
    @Instance(Repo.modId)
    public static TMechworks instance;
    /* Proxies for sides, used for graphics processing */
    @SidedProxy(clientSide = "tmechworks.client.ClientProxy", serverSide = "tmechworks.common.CommonProxy")
    public static CommonProxy proxy;

    public static final PacketPipeline packetPipeline = new PacketPipeline();

    @EventHandler
    public void preInit (FMLPreInitializationEvent event)
    {
        logger = event.getModLog();

        ConfigCore.loadConfig(new Configuration(event.getSuggestedConfigurationFile()));

        TMechworksRegistry.Mechworks = new TabTools("TMechworks");

        content = new MechContent();

        proxy.registerRenderer();
        proxy.registerTickHandler();

        NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);

        MinecraftForge.EVENT_BUS.register(new MultiblockEventHandler());
        if (event.getSide() == Side.SERVER)
        {
            MinecraftForge.EVENT_BUS.register(new MultiblockServerTickHandler());
        }
    }

    @EventHandler
    public void init (FMLInitializationEvent event)
    {
        packetPipeline.initalise();
        if (event.getSide() == Side.CLIENT)
        {
            MinecraftForge.EVENT_BUS.register(new SignalTetherWorldOverlayRenderer());
        }
        proxy.init();
    }

    @EventHandler
    public void postInit (FMLPostInitializationEvent evt)
    {
        packetPipeline.postInitialise();
        content.postInit();
        proxy.postInit();

    }

    @EventHandler
    public void serverStarting (FMLServerStartingEvent evt)
    {
        evt.registerServerCommand(new BlockInfoCommand());
    }

}
