package slimeknights.tmechworks.integration;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import slimeknights.tmechworks.TMechworks;
import slimeknights.tmechworks.blocks.logic.DrawbridgeLogicBase;
import slimeknights.tmechworks.blocks.logic.RedstoneMachineLogicBase;
import slimeknights.tmechworks.library.Util;

import java.util.List;

public class WailaIntegration
{

    private static final String CONFIG_COMMONS = Util.prefix("commons");

    public WailaIntegration ()
    {
        FMLInterModComms.sendMessage("Waila", "register", "slimeknights.tmechworks.integration.WailaIntegration.integrationCallback");
    }

    public static void integrationCallback (IWailaRegistrar registrar)
    {
        registrar.addConfig(TMechworks.modName, CONFIG_COMMONS, true);

        registrar.registerBodyProvider(new RedstoneMachineDataProvider(), RedstoneMachineLogicBase.class);
    }

    private static class RedstoneMachineDataProvider implements IWailaDataProvider
    {

        @Override public ItemStack getWailaStack (IWailaDataAccessor accessor, IWailaConfigHandler config)
        {
            return null;
        }

        @Override public List<String> getWailaHead (ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
        {
            return currenttip;
        }

        @Override public List<String> getWailaBody (ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
        {
            if (config.getConfig(CONFIG_COMMONS) && accessor.getTileEntity() instanceof DrawbridgeLogicBase)
            {
                RedstoneMachineLogicBase logicBase = (RedstoneMachineLogicBase) accessor.getTileEntity();

                currenttip.add(I18n.format("hud.msg.power") + ": " + logicBase.getRedstoneState());
            }

            return currenttip;
        }

        @Override public List<String> getWailaTail (ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
        {
            return currenttip;
        }

        @Override public NBTTagCompound getNBTData (EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos)
        {
            return tag;
        }
    }

}
