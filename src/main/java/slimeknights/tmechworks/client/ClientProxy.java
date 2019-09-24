package slimeknights.tmechworks.client;

import slimeknights.tmechworks.common.CommonProxy;
import slimeknights.tmechworks.common.MechworksContent;
import slimeknights.tmechworks.common.event.ModelBakeEventListener;

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
    }

    // Hack to enable config gui
//    static {
//        List<ModInfo> mods = ModList.get().getMods();
//        ModInfo info = mods.stream().filter(x -> x.getModId().equals(TMechworks.modId)).findFirst().orElse(null);
//
//        if (info != null) {
//            ModInfo newInfo = new ModInfo(info.getOwningFile(), info.getModConfig()) {
//                public boolean hasConfigUI() {
//                    return true;
//                }
//            };
//
//            mods.set(mods.indexOf(info), newInfo);
//        }
//    }
}
