package slimeknights.tmechworks;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import slimeknights.mantle.common.GuiHandler;
import slimeknights.tmechworks.common.CommonProxy;
import slimeknights.tmechworks.common.MechworksContent;
import slimeknights.tmechworks.integration.WailaIntegration;

@Mod(modid = TMechworks.modID, name = TMechworks.modName, version = TMechworks.modVersion, dependencies = "required-after:Forge@[12.18.0.1993,); required-after:mantle@[1.10-0.10.3,)", acceptedMinecraftVersions = "[1.10, 1.11)")
public class TMechworks
{

    public static final String modID = "tmechworks";
    public static final String modVersion = "${version}";
    public static final String modName = "Tinkers' Mechworks";

    public static MechworksContent content;

    @Mod.Instance(modID) public static TMechworks instance;

    @SidedProxy(modId = modID, clientSide = "slimeknights.tmechworks.client.ClientProxy", serverSide = "slimeknights.tmechworks.common.CommonProxy") public static CommonProxy proxy;

    @Mod.EventHandler public void preInit (FMLPreInitializationEvent event)
    {
        proxy.preInit();

        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());

        if (Loader.isModLoaded("Waila"))
        {
            new WailaIntegration();
        }
    }

    @Mod.EventHandler public void init (FMLInitializationEvent event)
    {
        proxy.init();

        content = new MechworksContent();
    }

    @Mod.EventHandler public void postInit (FMLInitializationEvent event)
    {
        proxy.postInit();
    }
}
