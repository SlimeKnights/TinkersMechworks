package slimeknights.tmechworks.integration.waila;

import com.google.common.eventbus.Subscribe;
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
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.tmechworks.TMechworks;
import slimeknights.tmechworks.blocks.logic.DrawbridgeLogicBase;
import slimeknights.tmechworks.blocks.logic.RedstoneMachineLogicBase;
import slimeknights.tmechworks.library.Util;

import java.util.List;

@Pulse(id = "wailaIntegration", modsRequired = Waila.modid, defaultEnable = true)
public class Waila
{
    public static final String modid = "waila";

    @Subscribe
    public void preInit(FMLPreInitializationEvent event) {
        FMLInterModComms.sendMessage("waila", "register", "slimeknights.tmechworks.integration.waila.WailaRegistrar.wailaCallback");
    }
}
