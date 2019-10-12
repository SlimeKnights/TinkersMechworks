package slimeknights.tmechworks.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import slimeknights.mantle.client.book.BookLoader;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.repository.FileRepository;
import slimeknights.tmechworks.common.CommonProxy;
import slimeknights.tmechworks.common.MechworksContent;
import slimeknights.tmechworks.common.event.ModelBakeEventListener;

public class ClientProxy extends CommonProxy {
    public static final BookData book = BookLoader.registerBook("tmechworks:book", true, false, new FileRepository("tmechworks:book"));

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

    @Override
    public PlayerEntity getPlayer() {
        return Minecraft.getInstance().player;
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
