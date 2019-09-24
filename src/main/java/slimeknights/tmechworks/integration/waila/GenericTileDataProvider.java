package slimeknights.tmechworks.integration.waila;

import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IDataAccessor;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerDataProvider;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import java.util.List;

public class GenericTileDataProvider implements IServerDataProvider<TileEntity>, IComponentProvider {
    public static final GenericTileDataProvider INSTANCE = new GenericTileDataProvider();

    @Override
    public void appendServerData(CompoundNBT nbt, ServerPlayerEntity player, World world, TileEntity tile) {
        if(tile instanceof IInformationProvider)
            ((IInformationProvider)tile).syncInformation(nbt, player);
    }

    @Override
    public void appendHead(List<ITextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
        call(tooltip, accessor, IInformationProvider.InformationType.HEAD);
    }

    @Override
    public void appendBody(List<ITextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
        call(tooltip, accessor, IInformationProvider.InformationType.BODY);
    }

    @Override
    public void appendTail(List<ITextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
        call(tooltip, accessor, IInformationProvider.InformationType.TAIL);
    }

    private void call(List<ITextComponent> tooltip, IDataAccessor accessor, IInformationProvider.InformationType type) {
        TileEntity te = accessor.getTileEntity();

        if(te instanceof IInformationProvider)
            ((IInformationProvider)te).getInformation(tooltip, type, accessor.getServerData(), accessor.getPlayer());
    }
}
