package slimeknights.tmechworks;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import slimeknights.mantle.common.GuiHandler;
import slimeknights.mantle.network.NetworkWrapper;
import slimeknights.tmechworks.common.*;
import slimeknights.tmechworks.library.JsonConfig;
import slimeknights.mantle.pulsar.config.ForgeCFG;
import slimeknights.mantle.pulsar.control.PulseManager;
import slimeknights.tmechworks.common.CommonProxy;
import slimeknights.tmechworks.common.Config;
import slimeknights.tmechworks.common.MechworksContent;
import slimeknights.tmechworks.integration.waila.Waila;
import slimeknights.tmechworks.networking.PacketUpdatePlaceDirection;

import java.io.File;


@Mod(modid = TMechworks.modID, name = TMechworks.modName, version = TMechworks.modVersion, dependencies = "required-after:forge@[14.23.4.2705,); required-after:mantle@[1.12-1.3.1,); after:jei@[4.2,); after:waila@[1.8,)", acceptedMinecraftVersions = "[1.12.2, 1.13)")
public class TMechworks
{
    public static final String modID = "tmechworks";
    public static final String modVersion = "${version}";
    public static final String modName = "Tinkers' Mechworks";

    public static PulseManager pulseManager = new PulseManager(Config.pulseConfig);

    public static MechworksContent content;

    public static NetworkWrapper packetPipeline;

    @Mod.Instance(modID) public static TMechworks instance;

    @SidedProxy(modId = modID, clientSide = "slimeknights.tmechworks.client.ClientProxy", serverSide = "slimeknights.tmechworks.common.CommonProxy") public static CommonProxy proxy;

    static {
        pulseManager.registerPulse(new Waila());
    }

    @Mod.EventHandler public void preInit (FMLPreInitializationEvent event)
    {
        File modConfigDir = new File(event.getModConfigurationDirectory() + File.separator + modID +"-blacklist.json");
        JsonConfig.createJsonDefault(modConfigDir);
        JsonConfig.readJson(modConfigDir);
        Config.load(event);

        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());

        content = new MechworksContent();
        MinecraftForge.EVENT_BUS.register(content);

        proxy.preInit();

        packetPipeline = new NetworkWrapper(modID);

        // Register packets
        packetPipeline.registerPacket(PacketUpdatePlaceDirection.class);
    }

    @Mod.EventHandler public void init (FMLInitializationEvent event)
    {
        proxy.init();
        JsonConfig.validateBlacklist();
    }

    @Mod.EventHandler public void postInit (FMLInitializationEvent event)
    {
        proxy.postInit();
    }
}
