package slimeknights.tmechworks.integration.waila;

import com.google.common.eventbus.Subscribe;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import slimeknights.mantle.pulsar.pulse.Pulse;

@Pulse(id = "wailaIntegration", modsRequired = Waila.modid, defaultEnable = true)
public class Waila
{
    public static final String modid = "waila";

    @Subscribe
    public void preInit(FMLPreInitializationEvent event) {
        FMLInterModComms.sendMessage("waila", "register", "slimeknights.tmechworks.integration.waila.WailaRegistrar.wailaCallback");
    }
}
