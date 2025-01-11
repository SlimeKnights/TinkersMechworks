package slimeknights.tmechworks.common.blocks.entity;

import com.google.common.collect.Lists;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.Containers;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.stats.Stats;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.items.wrapper.InvWrapper;
import slimeknights.tmechworks.common.MechworksContent;
import slimeknights.tmechworks.common.MechworksTags;
import slimeknights.tmechworks.common.blocks.DrawbridgeBlock;
import slimeknights.tmechworks.common.blocks.RedstoneMachineBlock;
import slimeknights.tmechworks.common.config.MechworksConfig;
import slimeknights.tmechworks.common.inventory.DrawbridgeContainerMenu;
import slimeknights.tmechworks.common.inventory.FragmentedContainer;
import slimeknights.tmechworks.common.items.MachineUpgradeItem;
import slimeknights.tmechworks.library.Util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

public class DrawbridgeBlockEntity extends RedstoneMachineBlockEntity implements IPlaceDirection {
    private static final float TICK_TIME = 0.05F;

    public DrawbridgeStats stats;

    public static final int UPGRADES_SIZE = 4;

    public final FragmentedContainer upgrades;
    public final FragmentedContainer slots;

    private WeakReference<FakePlayer> fakePlayer;

    private Direction placeDirection;
    private Direction rawPlaceDirection;
    private Angle placeAngle = Angle.NEUTRAL;

    private boolean isMoving;
    private boolean isExtended;
    private int extendedLength;
    private float cooldown;

    private static boolean isCapAccess;

    private long lastWorldTime;

    public DrawbridgeBlockEntity(BlockPos pos, BlockState state) {
        super(MechworksContent.TileEntities.drawbridge.get(), pos, state, new TranslatableComponent(Util.prefix("inventory.drawbridge")), UPGRADES_SIZE + 1, 64, true);

        upgrades = new FragmentedContainer(this, 0, UPGRADES_SIZE).overrideStackLimit(1).setValidItemsPredicate(stack -> stack.getItem() instanceof MachineUpgradeItem);
        slots = new FragmentedContainer(this, UPGRADES_SIZE, 1).setValidItemsPredicate(stack -> stack.getItem() instanceof BlockItem && !Block.byItem(stack.getItem()).defaultBlockState().is(MechworksTags.Blocks.DRAWBRIDGE_BLACKLIST)).overrideStackLimit(64);

        itemHandlerCap.invalidate();
        itemHandler = new DrawbridgeItemHandler(this);
        itemHandlerCap = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void tick() {
        super.tick();

        float delta = (getLevel().getGameTime() - lastWorldTime) * TICK_TIME;
        lastWorldTime = getLevel().getGameTime();

        if (placeDirection == null)
            setPlaceDirectionRelativeToBlock(Direction.NORTH);

        if (isMoving) {
            if (cooldown > 0F) {
                cooldown -= delta;

                setChangedFast();
                return;
            }

            if (isExtended) {
                if (extendedLength >= stats.extendLength) {
                    isMoving = false;

                    setChangedFast();
                    return;
                }

                int extend = extendedLength + 1;

                Direction dir = getLevel().getBlockState(getBlockPos()).getValue(DrawbridgeBlock.FACING);
                BlockPos pos = new BlockPos(getBlockPos().getX() + dir.getStepX() * extend, getBlockPos().getY() + dir.getStepY() * extend, getBlockPos().getZ() + dir.getStepZ() * extend);

                if (placeBlock(pos, slots.getItem(getSlot()))) {
                    extendedLength++;
                    cooldown = stats.extendDelay;
                    level.playSound(null, pos, SoundEvents.PISTON_EXTEND, SoundSource.BLOCKS, 0.25F, Util.rand.nextFloat() * 0.25F + 0.6F);
                } else {
                    isMoving = false;

                    setChangedFast();
                    return;
                }
            } else {
                if (extendedLength > stats.extendLength)
                    extendedLength = stats.extendLength;

                if (extendedLength <= 0) {
                    isMoving = false;
                    extendedLength = 0;

                    setChangedFast();
                    return;
                }

                int extend = extendedLength;

                Direction dir = getLevel().getBlockState(getBlockPos()).getValue(DrawbridgeBlock.FACING);
                BlockPos pos = new BlockPos(getBlockPos().getX() + dir.getStepX() * extend, getBlockPos().getY() + dir.getStepY() * extend, getBlockPos().getZ() + dir.getStepZ() * extend);

                if (breakBlock(pos, getSlot())) {
                    extendedLength--;
                    cooldown = stats.extendDelay;
                    level.playSound(null, pos, SoundEvents.PISTON_CONTRACT, SoundSource.BLOCKS, 0.25F, Util.rand.nextFloat() * 0.15F + 0.6F);
                } else {
                    isMoving = false;
                    extendedLength = 0;

                    setChangedFast();
                    return;
                }
            }

            setChangedFast();
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

        lastWorldTime = level.getGameTime();
    }

    @Override
    public void onRedstoneUpdate() {
        super.onRedstoneUpdate();

        if (isExtended && getRedstoneState() <= 0) {
            isExtended = false;
            isMoving = true;

            Direction dir = getLevel().getBlockState(getBlockPos()).getValue(DrawbridgeBlock.FACING);
            Level world = getLevel();

            // Clamp extended state to nearest air block
            for (int i = 1; i <= extendedLength; i++) {
                BlockPos pos = new BlockPos(getBlockPos().getX() + dir.getStepX() * i, getBlockPos().getY() + dir.getStepY() * i, getBlockPos().getZ() + dir.getStepZ() * i);

                if (world.isEmptyBlock(pos)) {
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
        if (getLevel().isClientSide)
            return false;
        if (stack.isEmpty() || !(stack.getItem() instanceof BlockItem))
            return false;
        if(Block.byItem(stack.getItem()).defaultBlockState().is(MechworksTags.Blocks.DRAWBRIDGE_BLACKLIST))
            return false;
        if(Block.byItem(stack.getItem()).defaultBlockState().getDestroySpeed(getLevel(), getBlockPos()) < 0)
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

        player.setItemSlot(EquipmentSlot.MAINHAND, stack);
        UseOnContext ctx = new UseOnContext(player, InteractionHand.MAIN_HAND, new BlockHitResult(new Vec3(pos.getX() + 0.5D, pos.getY() + yOffset, pos.getZ() + 0.5D), getPlaceDirection(), pos, false));
        return doPlaceBlock(new DrawbridgeItemUseContext(ctx)).consumesAction();
    }

    public boolean breakBlock(BlockPos pos, int targetSlot) {
        Level world = getLevel();

        if (world.isClientSide)
            return false;

        BlockState state = world.getBlockState(pos);
        if (state.isAir() || state.getDestroySpeed(getLevel(), getBlockPos()) < 0 || state.is(MechworksTags.Blocks.DRAWBRIDGE_BLACKLIST)) {
            return false;
        }

        ItemStack tool = DrawbridgeTools.getByBlock(state);

        LootContext.Builder context = new LootContext.Builder((ServerLevel) world);

        context.withOptionalParameter(LootContextParams.BLOCK_ENTITY, world.getBlockEntity(pos))
                .withRandom(world.random)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                .withParameter(LootContextParams.TOOL, tool);

        ItemStack stack = slots.getItem(targetSlot);
        List<ItemStack> drops = state.getDrops(context).stream().filter(x -> !x.isEmpty()).collect(Collectors.toList());

        if (stack.isEmpty()) {
            ItemStack target = drops.stream().filter(x -> slots.canPlaceItem(targetSlot, x)).findFirst().orElse(ItemStack.EMPTY);

            if (!target.isEmpty() && drops.remove(target))
                slots.setItem(targetSlot, target);
        } else {
            ItemStack target = drops.stream().filter(x -> stack.sameItem(x) && ItemStack.tagMatches(stack, x)).findFirst().orElse(ItemStack.EMPTY);

            if (!target.isEmpty() && drops.remove(target)) {
                int remainder = Math.max(stack.getCount() + target.getCount() - Math.min(stack.getMaxStackSize(), slots.getMaxStackSize()), 0);
                int targetCount = target.getCount() - remainder;

                stack.setCount(stack.getCount() + targetCount);

                if (remainder > 0) {
                    target.setCount(remainder);
                    drops.add(target);
                }
            }
        }

        drops.forEach(x -> {
            Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), x);
        });

        world.removeBlock(pos, false);
        state.getBlock().destroy(world, pos, state);
        return true;
    }

    public void computeStats() {
        DrawbridgeStats stats = new DrawbridgeStats();

        for (int i = 0; i < upgrades.getContainerSize(); i++) {
            Item item = upgrades.getItem(i).getItem();

            if (item instanceof MachineUpgradeItem)
                ((MachineUpgradeItem) item).effect.accept(stats);
        }

        this.stats = stats;
        onStatsUpdated();
    }

    public void onStatsUpdated() {
        int blockSlots = stats.isAdvanced ? stats.extendLength : 1;

        Level world = getLevel();
        BlockPos pos = getBlockPos();

        // Drop items in removed slots
        for (int i = slots.getContainerSize() - 1; i >= blockSlots; i--) {
            ItemStack stack = slots.getItem(i);

            if (!stack.isEmpty()) {
                Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
                slots.setItem(i, ItemStack.EMPTY);
            }
        }

        resize(UPGRADES_SIZE + blockSlots);
        slots.resize(blockSlots);
        slots.overrideStackLimit(stats.isAdvanced ? 1 : 64);

        BlockState state = getBlockState();
        getLevel().setBlockAndUpdate(getBlockPos(), state.setValue(DrawbridgeBlock.ADVANCED, stats.isAdvanced));
    }

    @Override
    public void load(CompoundTag tags) {
        super.load(tags);

        CompoundTag stats = tags.getCompound("DrawbridgeState");

        extendedLength = stats.getInt("ExtendLength");
        isExtended = stats.getBoolean("Extended");
        isMoving = stats.getBoolean("Moving");
        cooldown = stats.getFloat("Cooldown");

        if(tags.contains("DrawbridgeStats")) {
            CompoundTag statsTag = tags.getCompound("DrawbridgeStats");

            this.stats = new DrawbridgeStats();
            this.stats.extendLength = statsTag.getInt("ExtendLength");
            this.stats.extendDelay = statsTag.getFloat("ExtendDelay");
            this.stats.isAdvanced = statsTag.getBoolean("IsAdvanced");

            onStatsUpdated();
        }
    }

    @Nonnull
    @Override
    public void saveSynced(CompoundTag tags) {
        super.saveSynced(tags);

        CompoundTag state = new CompoundTag();

        state.putInt("ExtendLength", extendedLength);
        state.putBoolean("Extended", isExtended);
        state.putBoolean("Moving", isMoving);
        state.putFloat("Cooldown", cooldown);

        tags.put("DrawbridgeState", state);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag base = super.getUpdateTag();

        if(stats != null) {
            CompoundTag stats = new CompoundTag();

            stats.putInt("ExtendLength", this.stats.extendLength);
            stats.putFloat("ExtendDelay", this.stats.extendDelay);
            stats.putBoolean("IsAdvanced", this.stats.isAdvanced);

            base.put("DrawbridgeStats", stats);
        }

        return base;
    }

    @Override
    public void readItemData(CompoundTag tags) {
        super.readItemData(tags);

        rawPlaceDirection = Direction.values()[tags.getInt("PlaceDirectionRaw")];
        placeAngle = Angle.values()[tags.getInt("PlaceAngle")];
    }

    @Override
    public CompoundTag writeItemData(CompoundTag tags) {
        if (rawPlaceDirection == null)
            rawPlaceDirection = Direction.NORTH;

        tags.putInt("PlaceDirectionRaw", rawPlaceDirection.ordinal());
        tags.putInt("PlaceAngle", placeAngle.ordinal());

        return super.writeItemData(tags);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player playerEntity) {
        return new DrawbridgeContainerMenu(id, playerInventory, this);
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
        setChanged();
    }

    public void setPlaceDirection(Direction direction) {
        placeDirection = direction;
        setChanged();
    }

    public void setPlaceDirectionRelativeToBlock(Direction direction) {
        rawPlaceDirection = direction;

        Direction facing = getLevel().getBlockState(getBlockPos()).getValue(RedstoneMachineBlock.FACING);

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
                        setPlaceDirection(facing.getClockWise());
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
                        setPlaceDirection(facing.getCounterClockWise());
                        break;
                }
                break;
        }
    }

    public void updateFakePlayer(BlockPos pos) {
        fakePlayer = Util.getFakePlayer(level);

        if (fakePlayer == null) {
            return;
        }

        FakePlayer player = fakePlayer.get();

        float xRot = 0;
        float yRot = 0;

        float posX = pos.getX();
        float posY = pos.getY();
        float posZ = pos.getZ();

        switch (placeDirection) {
            case NORTH:
                yRot = 0;
                posZ += 2;
                break;
            case SOUTH:
                yRot = 180;
                posZ -= 2;
                break;
            case UP:
                xRot = 90;
                posY += 2;
                break;
            case DOWN:
                xRot = -90;
                posY -= 2;
                break;
            case EAST:
                yRot = 90;
                posX -= 2;
                break;
            case WEST:
                yRot = -90;
                posX += 2;
                break;
        }

        switch (placeAngle) {
            case HIGH:
                xRot -= 45;
                break;
            case LOW:
                xRot += 45;
                break;
        }

        player.setXRot(xRot);
        player.setYRot(yRot);

        player.xRotO = xRot;
        player.yRotO = yRot;
        player.yHeadRot = yRot;
        player.yHeadRotO = player.yHeadRot;

        player.setPos(posX, posY, posZ);
    }

    @Override
    public void setItem(int slot, @Nonnull ItemStack itemstack) {
        super.setItem(slot, itemstack);

        if (upgrades.isSlotInInventory(slot))
            computeStats();
    }

    @Override
    public boolean canPlaceItem(int slot, @Nonnull ItemStack stack) {
        if(!isCapAccess)
            return false;

        if(!slots.isSlotInInventory(slot - slots.getStartSlot()))
            return false;

        return super.canPlaceItem(slot, stack) && slots.canPlaceItem(slot - slots.getStartSlot(), stack);
    }

    public FakePlayer getFakePlayer(BlockPos pos) {
        updateFakePlayer(pos);

        return fakePlayer.get();
    }

    @Override
    public void getInformation(@Nonnull List<Component> info, @Nonnull InformationType type, CompoundTag serverData, Player player) {
        super.getInformation(info, type, serverData, player);

        if (type != InformationType.BODY) {
            return;
        }

        info.add(new TranslatableComponent(Util.prefix("machine.stats")));
        info.add(new TranslatableComponent(Util.prefix("drawbridge.stats.advanced"), stats.isAdvanced));
        info.add(new TranslatableComponent(Util.prefix("drawbridge.stats.length"), stats.extendLength));
        info.add(new TranslatableComponent(Util.prefix("drawbridge.stats.delay"), stats.extendDelay));

        if(serverData.contains("showDetails") && serverData.getBoolean("showDetails")) {
            info.add(new TranslatableComponent(Util.prefix("machine.state")));
            info.add(new TranslatableComponent(Util.prefix("drawbridge.state.moving"), serverData.getBoolean("moving")));
            info.add(new TranslatableComponent(Util.prefix("drawbridge.state.extended"), serverData.getBoolean("extended")));
            info.add(new TranslatableComponent(Util.prefix("drawbridge.state.extendedcount"), serverData.getInt("extendedCount")));
        } else {
            showDetailsText(info, serverData);
        }
    }

    @Override
    public void syncInformation(CompoundTag nbt, ServerPlayer player, boolean showDetails) {
        super.syncInformation(nbt, player, showDetails);

        if (showDetails) {
            nbt.putBoolean("extended", isExtended);
            nbt.putBoolean("moving", isMoving);
            nbt.putInt("extendedCount", extendedLength);
        }
    }

    /**
     * Adaptation of ForgeHooks.onPlaceItemIntoWorld that passes the context straight into tryPlace
     */
    public InteractionResult doPlaceBlock(DrawbridgeItemUseContext context) {
        ItemStack itemstack = context.getItemInHand();
        Level world = context.getLevel();

        Player player = context.getPlayer();
        if (player != null && !player.getAbilities().mayBuild && !itemstack.hasAdventureModePlaceTagForBlock(world.registryAccess().registryOrThrow(Registry.BLOCK_REGISTRY), new BlockInWorld(world, context.getClickedPos(), false)))
            return InteractionResult.PASS;

        if (!(itemstack.getItem() instanceof BlockItem))
            return InteractionResult.FAIL;

        // handle all placement events here
        BlockItem item = (BlockItem) itemstack.getItem();
        int size = itemstack.getCount();
        CompoundTag nbt = null;
        if (itemstack.getTag() != null)
            nbt = itemstack.getTag().copy();

        if (!(itemstack.getItem() instanceof BucketItem)) // if not bucket
            world.captureBlockSnapshots = true;

        ItemStack copy = itemstack.copy();
        InteractionResult ret = item.place(context);
        if (itemstack.isEmpty())
            ForgeEventFactory.onPlayerDestroyItem(player, copy, context.getHand());

        world.captureBlockSnapshots = false;

        if (ret.consumesAction()) {
            // save new item data
            int newSize = itemstack.getCount();
            CompoundTag newNBT = null;
            if (itemstack.getTag() != null) {
                newNBT = itemstack.getTag().copy();
            }
            @SuppressWarnings("unchecked")
            List<BlockSnapshot> blockSnapshots = (List<BlockSnapshot>) world.capturedBlockSnapshots.clone();
            world.capturedBlockSnapshots.clear();

            // make sure to set pre-placement item data for event
            itemstack.setCount(size);
            itemstack.setTag(nbt);

            Direction side = context.getClickedFace();

            boolean eventResult = false;
            if (blockSnapshots.size() > 1) {
                eventResult = ForgeEventFactory.onMultiBlockPlace(player, blockSnapshots, side);
            } else if (blockSnapshots.size() == 1) {
                eventResult = ForgeEventFactory.onBlockPlace(player, blockSnapshots.get(0), side);
            }

            if (eventResult) {
                ret = InteractionResult.FAIL; // cancel placement
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
                    if (!newBlock.hasBlockEntity()) // Containers get placed automatically
                    {
                        newBlock.onPlace(world, snap.getPos(), oldBlock, false);
                    }

                    world.markAndNotifyBlock(snap.getPos(), world.getChunkAt(snap.getPos()), oldBlock, newBlock, updateFlag, 512);
                }
                player.awardStat(Stats.ITEM_USED.get(item));
            }
        }
        world.capturedBlockSnapshots.clear();

        return ret;
    }

    public static class DrawbridgeStats {
        public int extendLength = MechworksConfig.COMMON_CONFIG.drawbridge.extendLength.get();
        public float extendDelay = MechworksConfig.COMMON_CONFIG.drawbridge.delay.get().floatValue();
        public boolean isAdvanced = false;
    }

    public enum Angle {
        HIGH,
        NEUTRAL,
        LOW
    }

    public static class DrawbridgeItemUseContext extends BlockPlaceContext {
        public DrawbridgeItemUseContext(UseOnContext pContext) {
            super(pContext);
        }

        @Override
        public BlockPos getClickedPos() {
            return getHitResult().getBlockPos();
        }
    }

    private static class DrawbridgeItemHandler extends InvWrapper {
        private final DrawbridgeBlockEntity te;

        public DrawbridgeItemHandler(DrawbridgeBlockEntity inv) {
            super(inv);

            te = inv;
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slotAbs, @Nonnull ItemStack stack, boolean simulate) {
            int slot = slotAbs - te.slots.getStartSlot();

            // Disallow inserting anywhere but in main inventory slots
            if(slot < 0 || slot >= te.slots.getContainerSize())
                return stack;

            isCapAccess = true;
            ItemStack out = super.insertItem(slotAbs, stack, simulate);
            isCapAccess = false;
            return out;
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int slotAbs, int amount, boolean simulate) {
            int slot = slotAbs - te.slots.getStartSlot();

            // Disallow inserting anywhere but in main inventory slots
            if(slot < 0 || slot >= te.slots.getContainerSize())
                return ItemStack.EMPTY;

            isCapAccess = true;
            ItemStack out = super.extractItem(slotAbs, amount, simulate);
            isCapAccess = false;
            return out;
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            isCapAccess = true;
            boolean out = super.isItemValid(slot, stack);
            isCapAccess = false;
            return out;
        }

        @Override
        public int getSlotLimit(int slot) {
            return te.slots.getMaxStackSize();
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

            PICKAXE.enchant(Enchantments.SILK_TOUCH, 1);
            AXE.enchant(Enchantments.SILK_TOUCH, 1);
            SHOVEL.enchant(Enchantments.SILK_TOUCH, 1);
        }

        public static ItemStack getByBlock(BlockState state) {
            if(PICKAXE.isCorrectToolForDrops(state))
                return PICKAXE;
            if(AXE.isCorrectToolForDrops(state))
                return AXE;
            if(SHOVEL.isCorrectToolForDrops(state))
                return SHOVEL;

            return PICKAXE;
        }
    }
}
