package slimeknights.tmechworks.client;

import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import slimeknights.tmechworks.TMechworks;
import slimeknights.tmechworks.common.CommonProxy;
import slimeknights.tmechworks.common.MechworksContent;
import slimeknights.tmechworks.common.event.ModelBakeEventListener;

import java.util.List;

public class ClientProxy extends CommonProxy {
    @Override
    public void preInit() {
        super.preInit();
    }

    @Override
    public void init() {
        super.init();

        ModelBakeEventListener.registerDisguiseBlock(MechworksContent.Blocks.drawbridge.getRegistryName());
        ModelBakeEventListener.registerDisguiseBlock(MechworksContent.Blocks.firestarter.getRegistryName());
    }

    @Override
    public void setupClient() {
        // TODO mod config gui
//        ModList.get().getModContainerById(TMechworks.modId).ifPresent(c -> c.registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> (mc, scr) -> scr));
    }

    static {
        List<ModInfo> mods = ModList.get().getMods();
        ModInfo info = mods.stream().filter(x -> x.getModId().equals(TMechworks.modId)).findFirst().orElse(null);

        if (info != null) {
            ModInfo newInfo = new ModInfo(info.getOwningFile(), info.getModConfig()) {
                public boolean hasConfigUI() {
                    return true;
                }
            };

            mods.set(mods.indexOf(info), newInfo);
        }
    }
}
