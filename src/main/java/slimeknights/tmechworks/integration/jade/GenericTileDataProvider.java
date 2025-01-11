package slimeknights.tmechworks.integration.jade;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import slimeknights.tmechworks.common.blocks.entity.RedstoneMachineBlockEntity;
import slimeknights.tmechworks.library.Util;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

import java.util.ArrayList;
import java.util.List;

public class GenericTileDataProvider implements IServerDataProvider<BlockEntity>, IBlockComponentProvider {
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
            if(te instanceof RedstoneMachineBlockEntity && !config.get(JadeIntegration.CONFIG_REDSTONE_MACHINE)) {
                return;
            }

            IInformationProvider provider = (IInformationProvider) te;

            List<Component> info = new ArrayList<>();
            provider.getInformation(info, accessor.getServerData(), accessor.getPlayer());

            if(!info.isEmpty()) {
                for(Component s : info) {
                    tooltip.add(s);
                }
            }
        }
    }

    @Override
    public ResourceLocation getUid() {
        return Util.getResource("generic");
    }
}
