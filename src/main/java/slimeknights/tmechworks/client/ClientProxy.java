package slimeknights.tmechworks.client;

import net.minecraft.world.level.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import slimeknights.mantle.client.book.BookLoader;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.repository.FileRepository;
import slimeknights.tmechworks.TMechworks;
import slimeknights.tmechworks.common.CommonProxy;
import slimeknights.tmechworks.common.MechworksContent;
import slimeknights.tmechworks.common.event.ModelBakeEventListener;
import slimeknights.tmechworks.library.Util;

public class ClientProxy extends CommonProxy {
    public static final BookData book = BookLoader.registerBook(Util.getResource("book"), true, false, new FileRepository(Util.getResource("book")));

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
    }

    private void registerDisguiseBlock(Block block) {
        ModelBakeEventListener.registerDisguiseBlock(block.getRegistryName());
        ItemBlockRenderTypes.setRenderLayer(block, rt -> true);
    }

    @Override
    public Player getPlayer() {
        return Minecraft.getInstance().player;
    }
}
