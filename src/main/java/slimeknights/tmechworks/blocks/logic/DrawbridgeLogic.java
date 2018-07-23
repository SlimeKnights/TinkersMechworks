package slimeknights.tmechworks.blocks.logic;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.tmechworks.blocks.Drawbridge;
import slimeknights.tmechworks.client.gui.GuiDrawbridge;
import slimeknights.tmechworks.inventory.ContainerDrawbridge;
import slimeknights.tmechworks.library.Util;

import java.util.List;

//TODO: lock the gui down whilst the drawbridge is running and/or extended
public class DrawbridgeLogic extends DrawbridgeLogicBase {
    protected static final ItemStack SILKTOUCH_PICKAXE;
    protected IBlockState placedState;
    protected ItemStack setStack = ItemStack.EMPTY;

    public DrawbridgeLogic() {
        super(Util.prefix("inventory.drawbridge"), 1);
    }

    @Override
    public void setupStatistics(DrawbridgeStats ds) {
    }

    public ItemStack getNextBlock() {
        return getStackInSlot(getNextIndex());
    }

    public ItemStack getLastBlock() {
        return getStackInSlot(getLastIndex());
    }

    public void subtractNextBlock() {
        decrStackSize(getNextIndex(), 1);
        ItemStack stack = getStackInSlot(getNextIndex());
        if (!stack.isEmpty() && stack.getCount() <= 0) {
            super.setInventorySlotContents(getNextIndex(), ItemStack.EMPTY);
        }
    }

    public void addLastBlock() {
        if (!getStackInSlot(getLastIndex()).isEmpty()) {
            decrStackSize(getLastIndex(), -1);
        } else {
            setInventorySlotContents(getLastIndex(), setStack);
            setStack = ItemStack.EMPTY;
        }
    }

    public int getNextIndex() {
        return 0;
    }

    public int getLastIndex() {
        return 0;
    }

    @Override
    public boolean extendNext() {
        EnumFacing face = getFacingDirection();

        int extend = getExtendState() + 1;
        BlockPos nextPos = new BlockPos(pos.getX() + face.getXOffset() * extend, pos.getY() + face.getYOffset() * extend, pos.getZ() + face.getZOffset() * extend);

        if (placeBlock(nextPos)) {
            subtractNextBlock();
            return true;
        }

        return false;
    }

    @Override
    public boolean retractNext() {
        EnumFacing face = getFacingDirection();

        int extend = getExtendState();
        BlockPos nextPos = new BlockPos(pos.getX() + face.getXOffset() * extend, pos.getY() + face.getYOffset() * extend, pos.getZ() + face.getZOffset() * extend);

        if (breakBlock(nextPos)) {
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

                    placedState = state;
                }

                return placed;
            }
        }

        return false;
    }

    public boolean breakBlock(BlockPos position) {
        if(world.isRemote)
            return false;

        setStack = ItemStack.EMPTY;

        ItemStack stack = getLastBlock();

        NonNullList<ItemStack> drops = getBlockDrops(position);

        if(stack.isEmpty() && placedState != null){
            ItemStack foundDrop = ItemStack.EMPTY;

            for(ItemStack drop : drops){
                if(drop.getCount() == 1 && drop.getItem() == Item.getItemFromBlock(placedState.getBlock())){
                    foundDrop = drop;
                    break;
                }
            }

            if(foundDrop.isEmpty())
                return false;

            drops.remove(foundDrop);

            setStack = foundDrop;
        } else if (!stack.isEmpty() && !drops.removeIf(drop -> drop.getCount() == 1 && ItemStack.areItemStackTagsEqual(drop, stack) && ItemStack.areItemsEqual(drop, stack))){
            return false;
        }

        drops.forEach(drop -> Block.spawnAsEntity(world, position, drop));

        return world.setBlockToAir(position);
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
    public void readFromNBT(NBTTagCompound tags) {
        super.readFromNBT(tags);

        if(tags.hasKey("placedState", Constants.NBT.TAG_COMPOUND))
            placedState = NBTUtil.readBlockState(tags.getCompoundTag("placedState"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tags) {
        tags = super.writeToNBT(tags);

        if(placedState != null) {
            NBTTagCompound stateCompound = new NBTTagCompound();
            NBTUtil.writeBlockState(stateCompound, placedState);
            tags.setTag("placedState", stateCompound);
        }

        return tags;
    }

    public NonNullList<ItemStack> getBlockDrops(BlockPos position){
        Drawbridge.dropCapture(true);

        IBlockState state = world.getBlockState(position);
        state.getBlock().harvestBlock(world, getFakePlayer(), position, state, world.getTileEntity(position), SILKTOUCH_PICKAXE);

        return Drawbridge.dropCapture(false);
    }

    static {
        SILKTOUCH_PICKAXE = new ItemStack(Items.DIAMOND_PICKAXE, 1, 0);
        EnchantmentHelper.setEnchantments(ImmutableMap.of(Enchantments.SILK_TOUCH, 1), SILKTOUCH_PICKAXE);
    }
}
