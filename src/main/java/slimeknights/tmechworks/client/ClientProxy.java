package slimeknights.tmechworks.client;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import slimeknights.mantle.client.book.BookLoader;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.repository.FileRepository;
import slimeknights.tmechworks.TMechworks;
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

        registerDisguiseBlock(MechworksContent.Blocks.drawbridge.get());
        registerDisguiseBlock(MechworksContent.Blocks.firestarter.get());
    }

    @Override
    public void setupClient() {
        DistExecutor.runWhenOn(Dist.CLIENT, () -> TMechworks.content::registerScreenFactories);

        // TODO mod config gui
    }

    private void registerDisguiseBlock(Block block) {
        ModelBakeEventListener.registerDisguiseBlock(block.getRegistryName());
        RenderTypeLookup.setRenderLayer(block, rt -> true);
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
