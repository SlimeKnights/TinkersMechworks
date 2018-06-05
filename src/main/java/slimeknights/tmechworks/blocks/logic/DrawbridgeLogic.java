package slimeknights.tmechworks.blocks.logic;

import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.tmechworks.client.gui.GuiDrawbridge;
import slimeknights.tmechworks.inventory.ContainerDrawbridge;
import slimeknights.tmechworks.library.Util;

public class DrawbridgeLogic extends DrawbridgeLogicBase
{

    public DrawbridgeLogic ()
    {
        super(Util.prefix("inventory.drawbridge"), 2);
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

        markDirty();
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
        // Handled by onPlaceItemIntoWorld
        /*decrStackSize(-1, 1);
        ItemStack stack = getStackInSlot(0);
        if (stack != null && stack.stackSize <= 0)
        {
            super.setInventorySlotContents(0, null);
        }*/
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
            world.setBlockState(nextPos, Blocks.AIR.getDefaultState());
            addLastBlock();

            return true;
        }

        setInventorySlotContents(0, getStackInSlot(0)); // Looks weird, but this'll update the ghost item on retract failure
        return false;
    }

    @Override
    public String getVariantName() {
        return "normal";
    }

    public boolean placeBlock (BlockPos position)
    {
        FakePlayer fakePlayer = getFakePlayer(position.getX(), position.getY(), position.getZ());

        if (fakePlayer == null)
            return false;

        ItemStack stack = getNextBlock();

        if (stack == null)
        {
            return false;
        }

        Item item = stack.getItem();

        if (item instanceof ItemBlock)
        {
            if (world.getBlockState(position).getBlock().isReplaceable(world, position))
            {
                fakePlayer.inventory.setInventorySlotContents(0, stack);
                return ForgeHooks.onPlaceItemIntoWorld(stack, fakePlayer, world, position, getPlaceDirection(), 0, 0, 0, EnumHand.MAIN_HAND) == EnumActionResult.SUCCESS;
            }
            else
            {
                return false;
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

            if (ib.getBlock() == world.getBlockState(position).getBlock() && (!ib.getHasSubtypes() || ib.getMetadata(stack) == world.getBlockState(position).getBlock()
                    .getMetaFromState(world.getBlockState(position))))
            {
                return world.setBlockToAir(position);
            }

            return false;
        }

        Block block = Block.getBlockFromItem(item);

        if (block != null)
        {
            if (block == world.getBlockState(position).getBlock() && (!item.getHasSubtypes() || stack.getMetadata() == world.getBlockState(position).getBlock()
                    .getMetaFromState(world.getBlockState(position))))
            {
                return world.setBlockToAir(position);
            }
        }

        return false;
    }

    @Override public Container createContainer (InventoryPlayer inventoryplayer, World world, BlockPos pos)
    {
        return new ContainerDrawbridge(this, inventoryplayer);
    }

    @Override @SideOnly(Side.CLIENT) public GuiContainer createGui (InventoryPlayer inventoryplayer, World world, BlockPos pos)
    {
        return new GuiDrawbridge(new ContainerDrawbridge(this, inventoryplayer));
    }
}
