package slimeknights.tmechworks.integration.waila;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import slimeknights.tmechworks.integration.IInformationProvider;

import javax.annotation.Nonnull;
import java.util.List;

public class GenericTileDataProvider implements IWailaDataProvider {
    public final String configName;

    public GenericTileDataProvider(String configName) {
        this.configName = configName;
    }

    @Override
    @Nonnull
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    @Override
    @Nonnull
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        if (config.getConfig(configName) && (accessor.getTileEntity() instanceof IInformationProvider)) {
            IInformationProvider provider = (IInformationProvider) accessor.getTileEntity();
            provider.getInformation(currenttip, IInformationProvider.InformationType.HEAD);
        }

        return currenttip;
    }

    @Override
    @Nonnull
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        if (config.getConfig(configName) && (accessor.getTileEntity() instanceof IInformationProvider)) {
            IInformationProvider provider = (IInformationProvider) accessor.getTileEntity();
            provider.getInformation(currenttip, IInformationProvider.InformationType.BODY);
        }

        return currenttip;
    }

    @Override
    @Nonnull
    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        if (config.getConfig(configName) && (accessor.getTileEntity() instanceof IInformationProvider)) {
            IInformationProvider provider = (IInformationProvider) accessor.getTileEntity();
            provider.getInformation(currenttip, IInformationProvider.InformationType.TAIL);
        }

        return currenttip;
    }

    @Override
    @Nonnull
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
        return tag;
    }
}
