package slimeknights.tmechworks.integration.waila;

import mcp.mobius.waila.api.BlockAccessor;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IServerDataProvider;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.config.IPluginConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import slimeknights.tmechworks.common.blocks.entity.RedstoneMachineBlockEntity;

import java.util.ArrayList;
import java.util.List;

public class GenericTileDataProvider implements IServerDataProvider<BlockEntity>, IComponentProvider {
    public static final GenericTileDataProvider INSTANCE = new GenericTileDataProvider();

    @Override
    public void appendServerData(CompoundTag nbt, ServerPlayer player, Level world, BlockEntity tile, boolean showDetails) {
        if(tile instanceof IInformationProvider)
            ((IInformationProvider)tile).syncInformation(nbt, player, showDetails);
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        BlockEntity te = accessor.getBlockEntity();
        if (te instanceof IInformationProvider) {
            if(te instanceof RedstoneMachineBlockEntity && !config.get(WailaIntegration.CONFIG_REDSTONE_MACHINE)) {
                return;
            }

            IInformationProvider provider = (IInformationProvider) te;

            IInformationProvider.InformationType type;
            switch(accessor.getTooltipPosition()) {
                case BODY -> type = IInformationProvider.InformationType.BODY;
                case TAIL -> type = IInformationProvider.InformationType.TAIL;
                case HEAD -> type = IInformationProvider.InformationType.HEAD;
                default -> type = IInformationProvider.InformationType.BODY;
            }

            List<Component> info = new ArrayList<>();
            provider.getInformation(info, type, accessor.getServerData(), accessor.getPlayer());

            if(!info.isEmpty()) {
                for(Component s : info) {
                    tooltip.add(s);
                }
            }
        }
    }
}
