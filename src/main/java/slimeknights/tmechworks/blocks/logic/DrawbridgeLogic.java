package slimeknights.tmechworks.blocks.logic;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.tmechworks.client.gui.GuiDrawbridge;
import slimeknights.tmechworks.inventory.ContainerDrawbridge;
import slimeknights.tmechworks.library.Util;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class DrawbridgeLogic extends DrawbridgeLogicBase {
    protected int[] metaMemory;

    public DrawbridgeLogic() {
        super(Util.prefix("inventory.drawbridge"), 2);
        metaMemory = new int[getStats().extendLength];
    }

    @Override
    public void setupStatistics(DrawbridgeStats ds) {
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return super.getStackInSlot(slot == -1 ? 0 : slot);
    }

    @Override
    public void setInventorySlotContents(int slot, @Nonnull ItemStack item) {
        super.setInventorySlotContents(slot == -1 ? 0 : slot, item);

        if (slot == 0) {
            if (item.isEmpty()) {
                setInventorySlotContents(1, ItemStack.EMPTY);
            } else {
                setInventorySlotContents(1, item.copy());
            }
        }

        markDirty();
    }

    public ItemStack getNextBlock() {
        return getStackInSlot(0);
    }

    public ItemStack getLastBlock() {
        return getStackInSlot(1);
    }

    public void subtractNextBlock() {
        decrStackSize(-1, 1);
        ItemStack stack = getStackInSlot(0);
        if (!stack.isEmpty() && stack.getCount() <= 0) {
            super.setInventorySlotContents(0, ItemStack.EMPTY);
        }
    }

    public void addLastBlock() {
        if (!getStackInSlot(0).isEmpty()) {
            decrStackSize(-1, -1);
        } else {
            super.setInventorySlotContents(0, getStackInSlot(1).copy());
        }
    }

    @Override
    public boolean extendNext() {
        EnumFacing face = getFacingDirection();

        int extend = getExtendState() + 1;
        BlockPos nextPos = new BlockPos(pos.getX() + face.getFrontOffsetX() * extend, pos.getY() + face.getFrontOffsetY() * extend, pos.getZ() + face.getFrontOffsetZ() * extend);

        if (placeBlock(nextPos)) {
            metaMemory[extend - 1] = world.getBlockState(nextPos).getBlock().getMetaFromState(world.getBlockState(nextPos));
            subtractNextBlock();
            return true;
        }

        return false;
    }

    @Override
    public boolean retractNext() {
        EnumFacing face = getFacingDirection();

        int extend = getExtendState();
        BlockPos nextPos = new BlockPos(pos.getX() + face.getFrontOffsetX() * extend, pos.getY() + face.getFrontOffsetY() * extend, pos.getZ() + face.getFrontOffsetZ() * extend);

        if (breakBlock(nextPos)) {
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

    public boolean placeBlock(BlockPos position) {
        if(world.isRemote)
            return false;

        FakePlayer fakePlayer = getFakePlayer(position.getX(), position.getY(), position.getZ());

        ItemStack stack = getNextBlock();

        if (stack.isEmpty()) {
            return false;
        }

        Item item = stack.getItem();

        if (item instanceof ItemBlock) {
            ItemBlock itemBlock = (ItemBlock) item;
            Block block = itemBlock.getBlock();

            EnumFacing hitFace = getPlaceDirection().getOpposite();

            if (world.getBlockState(position).getBlock().isReplaceable(world, position) && fakePlayer.canPlayerEdit(position, hitFace, stack) && world.mayPlace(block, position, false, hitFace, null)) {
                world.captureBlockSnapshots = true;

                fakePlayer.inventory.setInventorySlotContents(0, stack);

                float hitY = getPlaceAngle() == Angle.HIGH ? 1F : getPlaceAngle() == Angle.LOW ? 0F : 0.5F;
                IBlockState state = itemBlock.getBlock().getStateForPlacement(world, position, hitFace, .5F, hitY, .5F, itemBlock.getMetadata(stack), fakePlayer, EnumHand.MAIN_HAND);
                boolean placed = itemBlock.placeBlockAt(stack, fakePlayer, world, position, hitFace, .5F, hitY, .5F, state);
                if (placed) {
                    state.getBlock().onBlockPlacedBy(world, position, state, fakePlayer, stack);
                }

                world.captureBlockSnapshots = false;

                @SuppressWarnings("unchecked")
                List<BlockSnapshot> blockSnapshots = (List<BlockSnapshot>) world.capturedBlockSnapshots.clone();
                world.capturedBlockSnapshots.clear();

                BlockEvent.PlaceEvent placeEvent = null;
                if (blockSnapshots.size() == 1) {
                    placeEvent = ForgeEventFactory.onPlayerBlockPlace(fakePlayer, blockSnapshots.get(0), hitFace, EnumHand.MAIN_HAND);
                }

                if (placeEvent != null && placeEvent.isCanceled()) {
                    placed = false;

                    for (BlockSnapshot blocksnapshot : Lists.reverse(blockSnapshots)) {
                        world.restoringBlockSnapshots = true;
                        blocksnapshot.restore(true, false);
                        world.restoringBlockSnapshots = false;
                    }
                } else {
                    for (BlockSnapshot snap : blockSnapshots) {
                        int updateFlag = snap.getFlag();
                        IBlockState oldBlock = snap.getReplacedBlock();
                        IBlockState newBlock = world.getBlockState(snap.getPos());
                        if (!newBlock.getBlock().hasTileEntity(newBlock)) // Containers get placed automatically
                        {
                            newBlock.getBlock().onBlockAdded(world, snap.getPos(), newBlock);
                        }

                        world.markAndNotifyBlock(snap.getPos(), null, oldBlock, newBlock, updateFlag);
                    }
                }

                return placed;
            }
        }

        return false;
    }

    public boolean breakBlock(BlockPos position) {
        if(world.isRemote)
            return false;

        ItemStack stack = getLastBlock();

        if (stack.isEmpty()) {
            return false;
        }

        Item item = stack.getItem();

        if (item instanceof ItemBlock) {
            ItemBlock ib = (ItemBlock) item;

            IBlockState state = world.getBlockState(position);
            Block block = state.getBlock();

            if (ib.getBlock() == block && (!ib.getHasSubtypes() || metaMemory[getExtendState() - 1] == block.getMetaFromState(state))) {
                return world.setBlockToAir(position);
            }

            return false;
        }

        Block block = Block.getBlockFromItem(item);

        if (block != Blocks.AIR) {
            if (block == world.getBlockState(position).getBlock() && (!item.getHasSubtypes() || stack.getMetadata() == world.getBlockState(position).getBlock()
                    .getMetaFromState(world.getBlockState(position)))) {
                return world.setBlockToAir(position);
            }
        }

        return false;
    }

    @Override
    public Container createContainer(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
        return new ContainerDrawbridge(this, inventoryplayer);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiContainer createGui(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
        return new GuiDrawbridge(new ContainerDrawbridge(this, inventoryplayer));
    }

    @Override
    public void readItemData(NBTTagCompound tags) {
        super.readItemData(tags);

        metaMemory = tags.getIntArray("metaMemory");
        if(metaMemory.length != getStats().extendLength){
            metaMemory = Arrays.copyOf(metaMemory, getStats().extendLength);
        }
    }

    @Override
    public NBTTagCompound writeItemData(NBTTagCompound tags) {
        tags = super.writeItemData(tags);

        tags.setIntArray("metaMemory", metaMemory);

        return tags;
    }
}
