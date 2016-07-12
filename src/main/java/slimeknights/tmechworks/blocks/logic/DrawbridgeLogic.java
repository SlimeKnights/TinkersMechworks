package slimeknights.tmechworks.blocks.logic;

import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import slimeknights.tmechworks.TMechworks;
import slimeknights.tmechworks.client.gui.GuiDrawbridge;
import slimeknights.tmechworks.inventory.ContainerDrawbridge;

public class DrawbridgeLogic extends DrawbridgeLogicBase
{

    public DrawbridgeLogic ()
    {
        super(TMechworks.modID + ".inventory.drawbridge", 2);
    }

    @Override public void setupStatistics (DrawbridgeStats ds)
    {
    }

    @Override public ItemStack getStackInSlot (int slot)
    {
        return super.getStackInSlot(slot == -1 ? 0 : slot);
    }

    @Override public void setInventorySlotContents (int slot, ItemStack item)
    {
        super.setInventorySlotContents(slot == -1 ? 0 : slot, item);

        if (slot == 0)
        {
            if (item == null)
            {
                setInventorySlotContents(1, null);
            }
            else
            {
                setInventorySlotContents(1, new ItemStack(item.getItem(), 1, item.getItemDamage()));
            }
        }
    }

    public ItemStack getNextBlock ()
    {
        return getStackInSlot(0);
    }

    public ItemStack getLastBlock ()
    {
        return getStackInSlot(1);
    }

    public void subtractNextBlock ()
    {
        decrStackSize(-1, 1);
        ItemStack stack = getStackInSlot(0);
        if (stack != null && stack.stackSize <= 0)
        {
            super.setInventorySlotContents(0, null);
        }
    }

    public void addLastBlock ()
    {
        if (getStackInSlot(0) != null)
        {
            decrStackSize(-1, -1);
        }
        else
        {
            super.setInventorySlotContents(0, getStackInSlot(1));
        }
    }

    @Override public boolean extendNext ()
    {
        EnumFacing face = getFacingDirection();

        int extend = getExtendState() + 1;
        BlockPos nextPos = new BlockPos(pos.getX() + face.getFrontOffsetX() * extend, pos.getY() + face.getFrontOffsetY() * extend, pos.getZ() + face.getFrontOffsetZ() * extend);

        if (placeBlock(nextPos))
        {
            subtractNextBlock();
            return true;
        }

        return false;
    }

    @Override public boolean retractNext ()
    {
        EnumFacing face = getFacingDirection();

        int extend = getExtendState();
        BlockPos nextPos = new BlockPos(pos.getX() + face.getFrontOffsetX() * extend, pos.getY() + face.getFrontOffsetY() * extend, pos.getZ() + face.getFrontOffsetZ() * extend);

        if (breakBlock(nextPos))
        {
            worldObj.setBlockState(nextPos, Blocks.AIR.getDefaultState());
            addLastBlock();

            return true;
        }

        setInventorySlotContents(0, getStackInSlot(0)); // Looks weird, but this'll update the ghost item on retract failure
        return false;
    }

    public boolean placeBlock (BlockPos position)
    {
        ItemStack stack = getNextBlock();

        if (stack == null)
        {
            return false;
        }

        Item item = stack.getItem();

        if (item instanceof ItemBlock)
        {
            ItemBlock ib = (ItemBlock) item;

            if (worldObj.getBlockState(position).getBlock().isReplaceable(worldObj, position))
            {
                return ib.placeBlockAt(stack, getFakePlayer(), worldObj, position, getPlaceDirection(), 0, 0, 0, ib.getBlock().getStateFromMeta(ib.getMetadata(stack)));
            }
            else
            {
                return false;
            }
        }

        Block block = Block.getBlockFromItem(item);

        if (block != null)
        {
            if (worldObj.getBlockState(position).getBlock().isReplaceable(worldObj, position) && block.canPlaceBlockAt(worldObj, position))
            {
                if (worldObj.setBlockState(position, block.getStateFromMeta(stack.getMetadata())))
                {
                    block.onBlockPlacedBy(worldObj, position, worldObj.getBlockState(position), getFakePlayer(), stack);
                    return true;
                }
            }
        }

        return false;
    }

    public boolean breakBlock (BlockPos position)
    {
        ItemStack stack = getLastBlock();

        if (stack == null)
        {
            return false;
        }

        Item item = stack.getItem();

        if (item instanceof ItemBlock)
        {
            ItemBlock ib = (ItemBlock) item;

            if (ib.getBlock() == worldObj.getBlockState(position).getBlock() && (!ib.getHasSubtypes() || ib.getMetadata(stack) == worldObj.getBlockState(position).getBlock()
                    .getMetaFromState(worldObj.getBlockState(position))))
            {
                return worldObj.setBlockToAir(position);
            }

            return false;
        }

        Block block = Block.getBlockFromItem(item);

        if (block != null)
        {
            if (block == worldObj.getBlockState(position).getBlock() && (!item.getHasSubtypes() || stack.getMetadata() == worldObj.getBlockState(position).getBlock()
                    .getMetaFromState(worldObj.getBlockState(position))))
            {
                return worldObj.setBlockToAir(position);
            }
        }

        return false;
    }

    @Override public Container createContainer (InventoryPlayer inventoryplayer, World world, BlockPos pos)
    {
        return new ContainerDrawbridge(this, inventoryplayer);
    }

    @Override public GuiContainer createGui (InventoryPlayer inventoryplayer, World world, BlockPos pos)
    {
        return new GuiDrawbridge(new ContainerDrawbridge(this, inventoryplayer));
    }
}
