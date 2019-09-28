package slimeknights.tmechworks.common.blocks.tileentity;

import com.google.common.collect.Lists;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.ForgeEventFactory;
import slimeknights.tmechworks.common.config.BlacklistConfig;
import slimeknights.tmechworks.common.config.MechworksConfig;
import slimeknights.tmechworks.common.MechworksContent;
import slimeknights.tmechworks.common.blocks.DrawbridgeBlock;
import slimeknights.tmechworks.common.blocks.RedstoneMachineBlock;
import slimeknights.tmechworks.common.inventory.DrawbridgeContainer;
import slimeknights.tmechworks.common.inventory.FragmentedInventory;
import slimeknights.tmechworks.common.items.MachineUpgradeItem;
import slimeknights.tmechworks.library.Util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.stream.Collectors;

public class DrawbridgeTileEntity extends RedstoneMachineTileEntity implements IPlaceDirection {
    private static final float TICK_TIME = 0.05F;

    public DrawbridgeStats stats;

    public static final int UPGRADES_SIZE = 4;

    public final FragmentedInventory upgrades;
    public final FragmentedInventory slots;

    private WeakReference<FakePlayer> fakePlayer;

    private Direction placeDirection;
    private Direction rawPlaceDirection;
    private Angle placeAngle = Angle.NEUTRAL;

    private boolean isMoving;
    private boolean isExtended;
    private int extendedLength;
    private float cooldown;

    private long lastWorldTime;

    public DrawbridgeTileEntity() {
        super(MechworksContent.TileEntities.drawbridge, new TranslationTextComponent(Util.prefix("inventory.drawbridge")), UPGRADES_SIZE + 1);

        upgrades = new FragmentedInventory(this, 0, UPGRADES_SIZE).overrideStackLimit(1).setValidItemsPredicate(stack -> stack.getItem() instanceof MachineUpgradeItem);
        slots = new FragmentedInventory(this, UPGRADES_SIZE, 1).setValidItemsPredicate(stack -> stack.getItem() instanceof BlockItem && !BlacklistConfig.DRAWBRIDGE.isBlacklisted(((BlockItem)stack.getItem()).getBlock().getRegistryName())).overrideStackLimit(64);
    }

    @Override
    public void tick() {
        super.tick();

        float delta = (getWorld().getGameTime() - lastWorldTime) * TICK_TIME;
        lastWorldTime = getWorld().getGameTime();

        if (placeDirection == null)
            setPlaceDirectionRelativeToBlock(Direction.NORTH);

        if (isMoving) {
            if (cooldown > 0F) {
                cooldown -= delta;

                markDirtyFast();
                return;
            }

            if (isExtended) {
                if (extendedLength >= stats.extendLength) {
                    isMoving = false;

                    markDirtyFast();
                    return;
                }

                int extend = extendedLength + 1;

                Direction dir = getWorld().getBlockState(getPos()).get(DrawbridgeBlock.FACING);
                BlockPos pos = new BlockPos(getPos().getX() + dir.getXOffset() * extend, getPos().getY() + dir.getYOffset() * extend, getPos().getZ() + dir.getZOffset() * extend);

                if (placeBlock(pos, slots.getStackInSlot(getSlot()))) {
                    extendedLength++;
                    cooldown = stats.extendDelay;
                    world.playSound(null, pos, SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, 0.25F, Util.rand.nextFloat() * 0.25F + 0.6F);
                } else {
                    isMoving = false;

                    markDirtyFast();
                    return;
                }
            } else {
                if (extendedLength > stats.extendLength)
                    extendedLength = stats.extendLength;

                if (extendedLength <= 0) {
                    isMoving = false;
                    extendedLength = 0;

                    markDirtyFast();
                    return;
                }

                int extend = extendedLength;

                Direction dir = getWorld().getBlockState(getPos()).get(DrawbridgeBlock.FACING);
                BlockPos pos = new BlockPos(getPos().getX() + dir.getXOffset() * extend, getPos().getY() + dir.getYOffset() * extend, getPos().getZ() + dir.getZOffset() * extend);

                if (breakBlock(pos, getSlot())) {
                    extendedLength--;
                    cooldown = stats.extendDelay;
                    world.playSound(null, pos, SoundEvents.BLOCK_PISTON_CONTRACT, SoundCategory.BLOCKS, 0.25F, Util.rand.nextFloat() * 0.15F + 0.6F);
                } else {
                    isMoving = false;
                    extendedLength = 0;

                    markDirtyFast();
                    return;
                }
            }

            markDirtyFast();
        }
    }

    @Override
    public void init() {
        super.init();

        if (rawPlaceDirection == null)
            rawPlaceDirection = Direction.NORTH;
        if (placeDirection == null)
            setPlaceDirectionRelativeToBlock(rawPlaceDirection);
        if (placeAngle == null)
            setPlaceAngle(Angle.NEUTRAL);

        computeStats();

        lastWorldTime = world.getGameTime();
    }

    @Override
    public void onRedstoneUpdate() {
        super.onRedstoneUpdate();

        if (isExtended && getRedstoneState() <= 0) {
            isExtended = false;
            isMoving = true;

            Direction dir = getWorld().getBlockState(getPos()).get(DrawbridgeBlock.FACING);
            World world = getWorld();

            // Clamp extended state to nearest air block
            for (int i = 1; i <= extendedLength; i++) {
                BlockPos pos = new BlockPos(getPos().getX() + dir.getXOffset() * i, getPos().getY() + dir.getYOffset() * i, getPos().getZ() + dir.getZOffset() * i);

                if (world.isAirBlock(pos)) {
                    extendedLength = i - 1;
                    break;
                }
            }
        } else if (!isExtended && getRedstoneState() > 0) {
            isExtended = true;
            isMoving = true;
        }
    }

    public int getSlot() {
        if (!stats.isAdvanced)
            return 0;
        else
            return isExtended ? extendedLength : extendedLength - 1;
    }

    public boolean placeBlock(BlockPos pos, ItemStack stack) {
        if (getWorld().isRemote)
            return false;
        if (stack.isEmpty() || !(stack.getItem() instanceof BlockItem))
            return false;
        if(BlacklistConfig.DRAWBRIDGE.isBlacklisted(((BlockItem)stack.getItem()).getBlock().getRegistryName()))
            return false;


        FakePlayer player = getFakePlayer(pos);

        if (player == null)
            return false;

        double yOffset = 0D;
        switch (getPlaceAngle()) {
            case LOW:
                yOffset = 0.1D;
                break;
            case NEUTRAL:
                yOffset = 0.5D;
                break;
            case HIGH:
                yOffset = 0.9D;
                break;
        }

        player.setItemStackToSlot(EquipmentSlotType.MAINHAND, stack);
        ItemUseContext ctx = new ItemUseContext(player, Hand.MAIN_HAND, new BlockRayTraceResult(new Vec3d(pos.getX() + 0.5D, pos.getY() + yOffset, pos.getZ() + 0.5D), getPlaceDirection(), pos, false));
        return doPlaceBlock(new DrawbridgeItemUseContext(ctx)) == ActionResultType.SUCCESS;
    }

    public boolean breakBlock(BlockPos pos, int targetSlot) {
        World world = getWorld();

        if (world.isRemote)
            return false;

        BlockState state = world.getBlockState(pos);
        if (state.isAir(world, pos)) {
            return false;
        }

        ItemStack tool = DrawbridgeTools.getByType(state.getHarvestTool());

        LootContext.Builder context = new LootContext.Builder((ServerWorld) world);

        context.withNullableParameter(LootParameters.BLOCK_ENTITY, world.getTileEntity(pos))
                .withRandom(world.rand)
                .withParameter(LootParameters.POSITION, pos)
                .withParameter(LootParameters.TOOL, tool);

        ItemStack stack = slots.getStackInSlot(targetSlot);
        List<ItemStack> drops = state.getDrops(context).stream().filter(x -> !x.isEmpty()).collect(Collectors.toList());

        if (stack.isEmpty()) {
            ItemStack target = drops.stream().filter(x -> slots.isItemValidForSlot(targetSlot, x)).findFirst().orElse(ItemStack.EMPTY);

            if (!target.isEmpty() && drops.remove(target))
                slots.setInventorySlotContents(targetSlot, target);
        } else {
            ItemStack target = drops.stream().filter(x -> stack.isItemEqual(x) && ItemStack.areItemStackTagsEqual(stack, x)).findFirst().orElse(ItemStack.EMPTY);

            if (!target.isEmpty() && drops.remove(target)) {
                int remainder = Math.max(stack.getCount() + target.getCount() - Math.min(stack.getMaxStackSize(), slots.getInventoryStackLimit()), 0);
                int targetCount = target.getCount() - remainder;

                stack.setCount(stack.getCount() + targetCount);

                if (remainder > 0) {
                    target.setCount(remainder);
                    drops.add(target);
                }
            }
        }

        drops.forEach(x -> {
            InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), x);
        });

        world.removeBlock(pos, false);
        state.getBlock().onPlayerDestroy(world, pos, state);
        return true;
    }

    public void computeStats() {
        DrawbridgeStats stats = new DrawbridgeStats();

        for (int i = 0; i < upgrades.getSizeInventory(); i++) {
            Item item = upgrades.getStackInSlot(i).getItem();

            if (item instanceof MachineUpgradeItem)
                ((MachineUpgradeItem) item).effect.accept(stats);
        }

        this.stats = stats;
        onStatsUpdated();
    }

    public void onStatsUpdated() {
        int blockSlots = stats.isAdvanced ? stats.extendLength : 1;

        World world = getWorld();
        BlockPos pos = getPos();

        // Drop items in removed slots
        for (int i = slots.getSizeInventory() - 1; i >= blockSlots; i--) {
            ItemStack stack = slots.getStackInSlot(i);

            if (!stack.isEmpty()) {
                InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
                slots.setInventorySlotContents(i, ItemStack.EMPTY);
            }
        }

        resize(UPGRADES_SIZE + blockSlots);
        slots.resize(blockSlots);
        slots.overrideStackLimit(stats.isAdvanced ? 1 : 64);

        BlockState state = getWorld().getBlockState(getPos());
        getWorld().setBlockState(getPos(), state.with(DrawbridgeBlock.ADVANCED, stats.isAdvanced));
    }

    @Override
    public void read(CompoundNBT tags) {
        super.read(tags);

        CompoundNBT state = tags.getCompound("DrawbridgeState");

        extendedLength = state.getInt("ExtendLength");
        isExtended = state.getBoolean("Extended");
        isMoving = state.getBoolean("Moving");
        cooldown = state.getFloat("Cooldown");
    }

    @Nonnull
    @Override
    public CompoundNBT write(CompoundNBT tags) {
        tags = super.write(tags);

        CompoundNBT state = new CompoundNBT();

        state.putInt("ExtendLength", extendedLength);
        state.putBoolean("Extended", isExtended);
        state.putBoolean("Moving", isMoving);
        state.putFloat("Cooldown", cooldown);

        tags.put("DrawbridgeState", state);

        return tags;
    }

    @Override
    public void readItemData(CompoundNBT tags) {
        super.readItemData(tags);

        rawPlaceDirection = Direction.values()[tags.getInt("PlaceDirectionRaw")];
        placeAngle = Angle.values()[tags.getInt("PlaceAngle")];
    }

    @Override
    public CompoundNBT writeItemData(CompoundNBT tags) {
        if (rawPlaceDirection == null)
            rawPlaceDirection = Direction.NORTH;

        tags.putInt("PlaceDirectionRaw", rawPlaceDirection.ordinal());
        tags.putInt("PlaceAngle", placeAngle.ordinal());

        return super.writeItemData(tags);
    }

    @Nullable
    @Override
    public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new DrawbridgeContainer(id, playerInventory, this);
    }

    @Override
    public void setPlaceDirection(int direction) {
        if (direction < Direction.values().length)
            setPlaceDirectionRelativeToBlock(Direction.values()[direction]);
        else if (direction - Direction.values().length < Angle.values().length)
            setPlaceAngle(Angle.values()[direction - Direction.values().length]);
        else
            throw new IllegalArgumentException("Direction " + direction + " cannot be mapped to any direction or angle.");
    }

    public Direction getPlaceDirection() {
        return placeDirection;
    }

    public Direction getRawPlaceDirection() {
        return rawPlaceDirection;
    }

    public Angle getPlaceAngle() {
        return placeAngle;
    }

    public void setPlaceAngle(Angle angle) {
        placeAngle = angle;
        markDirty();
    }

    public void setPlaceDirection(Direction direction) {
        placeDirection = direction;
        markDirty();
    }

    public void setPlaceDirectionRelativeToBlock(Direction direction) {
        rawPlaceDirection = direction;

        Direction facing = getWorld().getBlockState(getPos()).get(RedstoneMachineBlock.FACING);

        switch (direction) {
            case UP:
                switch (facing) {
                    case UP:
                        setPlaceDirection(Direction.SOUTH);
                        break;
                    case DOWN:
                        setPlaceDirection(Direction.NORTH);
                        break;
                    default:
                        setPlaceDirection(Direction.UP);
                        break;
                }
                break;
            case DOWN:
                switch (facing) {
                    case UP:
                        setPlaceDirection(Direction.NORTH);
                        break;
                    case DOWN:
                        setPlaceDirection(Direction.SOUTH);
                        break;
                    default:
                        setPlaceDirection(Direction.DOWN);
                        break;
                }
                break;
            case NORTH:
                setPlaceDirection(facing);
                break;
            case SOUTH:
                setPlaceDirection(facing.getOpposite());
                break;
            case EAST:
                switch (facing) {
                    case UP:
                    case DOWN:
                        setPlaceDirection(direction);
                        break;
                    default:
                        setPlaceDirection(facing.rotateY());
                        break;
                }
                break;
            case WEST:
                switch (facing) {
                    case UP:
                    case DOWN:
                        setPlaceDirection(direction);
                        break;
                    default:
                        setPlaceDirection(facing.rotateYCCW());
                        break;
                }
                break;
        }
    }

    public void updateFakePlayer(BlockPos pos) {
        if (fakePlayer == null || fakePlayer.get() == null) {
            fakePlayer = Util.createFakePlayer(world);
        }

        if (fakePlayer == null) {
            return;
        }

        FakePlayer player = fakePlayer.get();

        player.rotationYaw = 0;
        player.rotationPitch = 0;
        player.posX = pos.getX();
        player.posY = pos.getY();
        player.posZ = pos.getZ();

        switch (placeDirection) {
            case NORTH:
                player.rotationYaw = 0;
                player.posZ += 2;
                break;
            case SOUTH:
                player.rotationYaw = 180;
                player.posZ -= 2;
                break;
            case UP:
                player.rotationPitch = 90;
                player.posY += 2;
                break;
            case DOWN:
                player.rotationPitch = -90;
                player.posY -= 2;
                break;
            case EAST:
                player.rotationYaw = 90;
                player.posX -= 2;
                break;
            case WEST:
                player.rotationYaw = -90;
                player.posX += 2;
                break;
        }

        switch (placeAngle) {
            case HIGH:
                player.rotationPitch -= 45;
                break;
            case LOW:
                player.rotationPitch += 45;
                break;
        }

        player.prevRotationPitch = player.rotationPitch;
        player.prevRotationYaw = player.rotationYaw;
        player.rotationYawHead = player.rotationYaw;
        player.prevRotationYawHead = player.rotationYawHead;
    }

    @Override
    public void setInventorySlotContents(int slot, @Nonnull ItemStack itemstack) {
        super.setInventorySlotContents(slot, itemstack);

        if (upgrades.isSlotInInventory(slot))
            computeStats();
    }

    public FakePlayer getFakePlayer(BlockPos pos) {
        updateFakePlayer(pos);

        return fakePlayer.get();
    }

    @Override
    public void getInformation(@Nonnull List<ITextComponent> info, InformationType type, CompoundNBT serverData, PlayerEntity player) {
        super.getInformation(info, type, serverData, player);

        if (type != InformationType.BODY) {
            return;
        }

        info.add(new TranslationTextComponent(Util.prefix("machine.stats")));
        info.add(new TranslationTextComponent(Util.prefix("drawbridge.stats.advanced"), stats.isAdvanced));
        info.add(new TranslationTextComponent(Util.prefix("drawbridge.stats.length"), stats.extendLength));
        info.add(new TranslationTextComponent(Util.prefix("drawbridge.stats.delay"), stats.extendDelay));
        info.add(new StringTextComponent(""));

        requireSneak(info, player, () -> {
            info.add(new TranslationTextComponent(Util.prefix("machine.state")));
            info.add(new TranslationTextComponent(Util.prefix("drawbridge.state.moving"), serverData.getBoolean("moving")));
            info.add(new TranslationTextComponent(Util.prefix("drawbridge.state.extended"), serverData.getBoolean("extended")));
            info.add(new TranslationTextComponent(Util.prefix("drawbridge.state.extendedcount"), serverData.getInt("extendedCount")));
        });
    }

    @Override
    public void syncInformation(CompoundNBT nbt, ServerPlayerEntity player) {
        nbt.putBoolean("extended", isExtended);
        nbt.putBoolean("moving", isMoving);
        nbt.putInt("extendedCount", extendedLength);
    }

    /**
     * Adaptation of ForgeHooks.onPlaceItemIntoWorld that passes the context straight into tryPlace
     */
    public ActionResultType doPlaceBlock(DrawbridgeItemUseContext context) {
        ItemStack itemstack = context.getItem();
        World world = context.getWorld();

        PlayerEntity player = context.getPlayer();
        if (player != null && !player.abilities.allowEdit && !itemstack.canPlaceOn(world.getTags(), new CachedBlockInfo(world, context.getPos(), false)))
            return ActionResultType.PASS;

        if (!(itemstack.getItem() instanceof BlockItem))
            return ActionResultType.FAIL;

        // handle all placement events here
        BlockItem item = (BlockItem) itemstack.getItem();
        int size = itemstack.getCount();
        CompoundNBT nbt = null;
        if (itemstack.getTag() != null)
            nbt = itemstack.getTag().copy();

        if (!(itemstack.getItem() instanceof BucketItem)) // if not bucket
            world.captureBlockSnapshots = true;

        ItemStack copy = itemstack.isDamageable() ? itemstack.copy() : null;
        ActionResultType ret = item.tryPlace(context);
        if (itemstack.isEmpty())
            ForgeEventFactory.onPlayerDestroyItem(player, copy, context.getHand());

        world.captureBlockSnapshots = false;

        if (ret == ActionResultType.SUCCESS) {
            // save new item data
            int newSize = itemstack.getCount();
            CompoundNBT newNBT = null;
            if (itemstack.getTag() != null) {
                newNBT = itemstack.getTag().copy();
            }
            @SuppressWarnings("unchecked")
            List<BlockSnapshot> blockSnapshots = (List<BlockSnapshot>) world.capturedBlockSnapshots.clone();
            world.capturedBlockSnapshots.clear();

            // make sure to set pre-placement item data for event
            itemstack.setCount(size);
            itemstack.setTag(nbt);

            Direction side = context.getFace();

            boolean eventResult = false;
            if (blockSnapshots.size() > 1) {
                eventResult = ForgeEventFactory.onMultiBlockPlace(player, blockSnapshots, side);
            } else if (blockSnapshots.size() == 1) {
                eventResult = ForgeEventFactory.onBlockPlace(player, blockSnapshots.get(0), side);
            }

            if (eventResult) {
                ret = ActionResultType.FAIL; // cancel placement
                // revert back all captured blocks
                for (BlockSnapshot blocksnapshot : Lists.reverse(blockSnapshots)) {
                    world.restoringBlockSnapshots = true;
                    blocksnapshot.restore(true, false);
                    world.restoringBlockSnapshots = false;
                }
            } else {
                // Change the stack to its new content
                itemstack.setCount(newSize);
                itemstack.setTag(newNBT);

                for (BlockSnapshot snap : blockSnapshots) {
                    int updateFlag = snap.getFlag();
                    BlockState oldBlock = snap.getReplacedBlock();
                    BlockState newBlock = world.getBlockState(snap.getPos());
                    if (!newBlock.getBlock().hasTileEntity(newBlock)) // Containers get placed automatically
                    {
                        newBlock.onBlockAdded(world, snap.getPos(), oldBlock, false);
                    }

                    world.markAndNotifyBlock(snap.getPos(), null, oldBlock, newBlock, updateFlag);
                }
                player.addStat(Stats.ITEM_USED.get(item));
            }
        }
        world.capturedBlockSnapshots.clear();

        return ret;
    }

    public static class DrawbridgeStats {
        public int extendLength = MechworksConfig.DRAWBRIDGE.extendLength.get();
        public float extendDelay = MechworksConfig.DRAWBRIDGE.delay.get().floatValue();
        public boolean isAdvanced = false;
    }

    public enum Angle {
        HIGH,
        NEUTRAL,
        LOW
    }

    public static class DrawbridgeItemUseContext extends BlockItemUseContext {
        public DrawbridgeItemUseContext(ItemUseContext p_i47813_1_) {
            super(p_i47813_1_);
        }

        @Override
        public BlockPos getPos() {
            return rayTraceResult.getPos();
        }
    }

    private static class DrawbridgeTools {
        public static final ItemStack PICKAXE;
        public static final ItemStack AXE;
        public static final ItemStack SHOVEL;

        static {
            PICKAXE = new ItemStack(Items.DIAMOND_PICKAXE);
            AXE = new ItemStack(Items.DIAMOND_AXE);
            SHOVEL = new ItemStack(Items.DIAMOND_SHOVEL);

            PICKAXE.addEnchantment(Enchantments.SILK_TOUCH, 1);
            AXE.addEnchantment(Enchantments.SILK_TOUCH, 1);
            SHOVEL.addEnchantment(Enchantments.SILK_TOUCH, 1);
        }

        public static ItemStack getByType(ToolType type) {
            if (type == null)
                return PICKAXE;

            switch (type.getName()) {
                case "pickaxe":
                    return PICKAXE;
                case "axe":
                    return AXE;
                case "shovel":
                    return SHOVEL;
            }

            return PICKAXE;
        }
    }
}
