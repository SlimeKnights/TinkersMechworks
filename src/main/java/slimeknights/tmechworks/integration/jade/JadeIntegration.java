package slimeknights.tmechworks.integration.jade;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import slimeknights.tmechworks.common.blocks.RedstoneMachineBlock;
import slimeknights.tmechworks.library.Util;
import snownee.jade.api.*;

@WailaPlugin
public class JadeIntegration implements IWailaPlugin {
    public static final ResourceLocation CONFIG_REDSTONE_MACHINE = Util.getResource("redstone_machine");

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(GenericTileDataProvider.INSTANCE, BlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.addConfig(CONFIG_REDSTONE_MACHINE, true);
        registration.registerBlockComponent(GenericTileDataProvider.INSTANCE, RedstoneMachineBlock.class);
    }
}