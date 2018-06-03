package slimeknights.tmechworks.common;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import slimeknights.mantle.pulsar.config.ForgeCFG;
import slimeknights.tmechworks.TMechworks;

public final class Config {
    public static ForgeCFG pulseConfig = new ForgeCFG("tmechworksModules", "Modules");
    public static Config instance = new Config();

    private Config(){}

    private static Configuration configFile;

    public static void load(FMLPreInitializationEvent event){
        configFile = new Configuration(event.getSuggestedConfigurationFile(), "2.0", false);
        MinecraftForge.EVENT_BUS.register(instance);
        updateConfig();
    }

    @SubscribeEvent
    public void update(ConfigChangedEvent.OnConfigChangedEvent event){
        if(event.getModID().equals(TMechworks.modID)) {
            updateConfig();
        }
    }

    public static void updateConfig(){
        if(configFile.hasChanged()) {
            configFile.save();
        }
        if(pulseConfig.getConfig().hasChanged()) {
            pulseConfig.flush();
        }
    }
}
