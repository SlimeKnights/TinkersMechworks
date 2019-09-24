package slimeknights.tmechworks;


import net.minecraft.data.DataGenerator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import slimeknights.mantle.util.ModelJsonGenerator;
import slimeknights.tmechworks.client.ClientProxy;
import slimeknights.tmechworks.common.CommonProxy;
import slimeknights.tmechworks.common.MechworksConfig;
import slimeknights.tmechworks.common.MechworksContent;
import slimeknights.tmechworks.common.network.PacketHandler;

import java.io.File;
import java.nio.file.Paths;

@Mod(TMechworks.modId)
public class TMechworks {
    public static final String modId = "tmechworks";
    public static final String modName = "Tinkers' Mechworks";

    public static final Logger log = LogManager.getLogger(modId);

    public static TMechworks instance;
    public static CommonProxy proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);
    public static MechworksContent content;

    public TMechworks() {
        instance = this;

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        bus.addListener(this::preInit);
        bus.addListener(this::init);
        bus.addListener(this::postInit);
        bus.addListener(this::gatherData);

        content = new MechworksContent();
        bus.register(content);
    }

    private void preInit(final FMLCommonSetupEvent event) {
        File configPath = new File("config");
        if(!configPath.exists())
            configPath.mkdirs();
        MechworksConfig.load();

        proxy.preInit();

        content.preInit(event);
        DistExecutor.runWhenOn(Dist.CLIENT, () -> content::registerScreenFactories);

        PacketHandler.register();
    }

    private void init(final InterModEnqueueEvent event) {
        proxy.init();
        content.init(event);
    }

    private void postInit(final InterModProcessEvent event) {
        proxy.postInit();
        content.postInit(event);
    }

    private void setupClient(final FMLClientSetupEvent event) {
        proxy.setupClient();
    }

    private void gatherData(final GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        generator.addProvider(new ModelJsonGenerator(generator, modId));
    }
}
