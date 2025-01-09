package slimeknights.tmechworks.integration.waila;

import mcp.mobius.waila.api.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import slimeknights.tmechworks.common.blocks.RedstoneMachineBlock;
import slimeknights.tmechworks.library.Util;

@WailaPlugin
public class WailaIntegration implements IWailaPlugin {
    private static final ResourceLocation CONFIG_REDSTONE_MACHINE = Util.getResource("redstone_machine");

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.addConfig(CONFIG_REDSTONE_MACHINE, true);

        registration.registerBlockDataProvider(GenericTileDataProvider.INSTANCE, BlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerComponentProvider(GenericTileDataProvider.INSTANCE, TooltipPosition.HEAD, RedstoneMachineBlock.class);
        registration.registerComponentProvider(GenericTileDataProvider.INSTANCE, TooltipPosition.BODY, RedstoneMachineBlock.class);
        registration.registerComponentProvider(GenericTileDataProvider.INSTANCE, TooltipPosition.TAIL, RedstoneMachineBlock.class);
    }
}