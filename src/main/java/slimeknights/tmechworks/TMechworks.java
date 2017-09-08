package slimeknights.tmechworks;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import slimeknights.mantle.common.GuiHandler;
import slimeknights.mantle.network.NetworkWrapper;
import slimeknights.tmechworks.common.*;
import slimeknights.tmechworks.integration.WailaIntegration;
import slimeknights.tmechworks.networking.PacketUpdatePlaceDirection;

@Mod(modid = TMechworks.modID, name = TMechworks.modName, version = TMechworks.modVersion, dependencies = "required-after:forge@[14.21.1.2387,); required-after:mantle@[1.12-1.3.1,); after:jei@[4.2,); after:waila@[1.8,)", acceptedMinecraftVersions = "[1.12, 1.12.1)")
public class TMechworks
{
    public static final String modID = "tmechworks";
    public static final String modVersion = "${version}";
    public static final String modName = "Tinkers' Mechworks";

    public static MechworksContent content;

    public static NetworkWrapper packetPipeline;

    @Mod.Instance(modID) public static TMechworks instance;

    @SidedProxy(modId = modID, clientSide = "slimeknights.tmechworks.client.ClientProxy", serverSide = "slimeknights.tmechworks.common.CommonProxy") public static CommonProxy proxy;

    @Mod.EventHandler public void preInit (FMLPreInitializationEvent event)
    {
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());

        if (Loader.isModLoaded("Waila"))
        {
            new WailaIntegration();
        }

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
    }

    @Mod.EventHandler public void postInit (FMLInitializationEvent event)
    {
        proxy.postInit();
    }
}
