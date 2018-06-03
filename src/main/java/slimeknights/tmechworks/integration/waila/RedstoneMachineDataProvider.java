package slimeknights.tmechworks.integration.waila;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import slimeknights.tmechworks.blocks.logic.DrawbridgeLogic;
import slimeknights.tmechworks.blocks.logic.DrawbridgeLogicBase;
import slimeknights.tmechworks.blocks.logic.FirestarterLogic;
import slimeknights.tmechworks.blocks.logic.RedstoneMachineLogicBase;

import java.util.List;

public class RedstoneMachineDataProvider implements IWailaDataProvider
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
        if (config.getConfig(WailaRegistrar.CONFIG_REDSTONE_MACHINE) && accessor.getTileEntity() instanceof RedstoneMachineLogicBase)
        {
            RedstoneMachineLogicBase logicBase = (RedstoneMachineLogicBase) accessor.getTileEntity();

            currenttip.add(I18n.format("hud.msg.power") + ": " + logicBase.getRedstoneState());

            if(logicBase instanceof DrawbridgeLogicBase){
                DrawbridgeLogicBase logic = (DrawbridgeLogicBase) logicBase;
                DrawbridgeLogicBase.DrawbridgeStats stats = logic.getStats();

                currenttip.add(I18n.format("hud.msg.state") + ": " + I18n.format("tmechworks.hud.state.drawbridge." + (logic.getExtending() ? "extending" : logic.getExtended() ? "extended" : "retracted")) + " " + I18n.format("tmechworks.hud.state.drawbridge.length", logic.getExtendState()));
                currenttip.add(I18n.format("tmechworks.machine.stats"));
                currenttip.add(I18n.format("tmechworks.drawbridge.stats.length", stats.extendLength));
                currenttip.add(I18n.format("tmechworks.drawbridge.stats.delay", stats.extendDelay));
            } else if(logicBase instanceof FirestarterLogic){
                FirestarterLogic logic = (FirestarterLogic) logicBase;
                currenttip.add(I18n.format("tmechworks.hud.behaviour") + ": " + I18n.format("tmechworks.hud.behaviour.firestarter." + (logic.getShouldExtinguish() ? "extinguish" : "keep")));
            }
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
