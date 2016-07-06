package slimeknights.tmechworks.blocks.logic;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import slimeknights.tmechworks.TMechworks;

public class DrawbridgeLogic extends DrawbridgeLogicBase
{

    public DrawbridgeLogic ()
    {
        super(TMechworks.modID + ".inventory.drawbridge", 2);
    }

    @Override public void setupStatistics (DrawbridgeStats ds)
    {
    }

    @Override public void setInventorySlotContents (int slot, ItemStack item)
    {
        super.setInventorySlotContents(slot, item);

        if (slot == 0)
        {
            setInventorySlotContents(1, new ItemStack(item.getItem(), 1, item.getItemDamage()));
        }
    }

    @Override public boolean extendNext ()
    {
        Block b = Blocks.STONE;
        EnumFacing side = EnumFacing.NORTH;

        int extend = getExtendState() + 1;
        EnumFacing face = getFacingDirection();
        BlockPos nextPos = new BlockPos(pos.getX() + face.getFrontOffsetX() * extend, pos.getY() + face.getFrontOffsetY() * extend, pos.getZ() + face.getFrontOffsetZ() * extend);

        if (!worldObj.canBlockBePlaced(b, nextPos, true, side, null, null))
        {
            return false;
        }

        worldObj.setBlockState(nextPos, Blocks.STONE.getDefaultState());

        return true;
    }

    @Override public boolean retractNext ()
    {
        int extend = getExtendState();
        EnumFacing face = getFacingDirection();
        BlockPos nextPos = new BlockPos(pos.getX() + face.getFrontOffsetX() * extend, pos.getY() + face.getFrontOffsetY() * extend, pos.getZ() + face.getFrontOffsetZ() * extend);

        worldObj.setBlockState(nextPos, Blocks.AIR.getDefaultState());

        return true;
    }
}
