package slimeknights.tmechworks.common.blocks.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import slimeknights.mantle.block.entity.InventoryBlockEntity;
import slimeknights.tmechworks.client.model.DisguiseBakedModel;
import slimeknights.tmechworks.common.blocks.RedstoneMachineBlock;
import slimeknights.tmechworks.common.inventory.DisguiseContainerMenu;
import slimeknights.tmechworks.integration.jade.IInformationProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public abstract class RedstoneMachineBlockEntity extends InventoryBlockEntity implements IInformationProvider {
    private SimpleContainer disguiseInventory;
    private String disguiseState;

    private int redstoneState;
    private boolean isFirstTick = true;

    public RedstoneMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, Component name, int inventorySize) {
        this(type, pos, state, name, inventorySize, 64);
    }

    public RedstoneMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, Component name, int inventorySize, int maxStackSize) {
        this(type, pos, state, name, inventorySize, maxStackSize, false);
    }

    public RedstoneMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, Component name, int inventorySize, int maxStackSize, boolean saveSizeToNBT) {
        super(type, pos, state, name, saveSizeToNBT, inventorySize, maxStackSize);

        disguiseInventory = new SimpleContainer(1) {
            @Override
            public boolean canPlaceItem(int index, ItemStack stack) {
                return stack.getItem() instanceof BlockItem;
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        };
    }

    /**
     * Updates redstone state
     */
    public void updateRedstone() {
        if (isFirstTick)
            return;

        Direction facing = Direction.NORTH;

        if (hasFacingDirection()) {
            facing = getLevel().getBlockState(getBlockPos()).getValue(RedstoneMachineBlock.FACING);
        }

        int oldPow = redstoneState;

        Direction[] directions = Direction.values();
        int maxPow = 0;

        for (Direction dir : directions) {
            if (!hasFacingDirection() || dir != facing) {
                int pow = level.getSignal(worldPosition.relative(dir), dir);
                if (pow > maxPow)
                    maxPow = pow;
            }
        }

        int downPow = level.getSignal(worldPosition, Direction.DOWN);
        if (downPow > maxPow)
            maxPow = downPow;

        redstoneState = maxPow;

        if (maxPow != oldPow) {
            onRedstoneUpdate();
        }
        onBlockUpdate();

        if (oldPow != redstoneState) {
            sync();
        }
    }

    /**
     * Called when the redstone state changes
     */
    public void onRedstoneUpdate() {
    }

    /**
     * Called when redstone state is updated, but the redstone state remains unchanged
     */
    public void onBlockUpdate() {
    }

    /**
     * @return The redstone power level
     */
    public int getRedstoneState() {
        return redstoneState;
    }

    public void tick() {
        if (isFirstTick) {
            isFirstTick = false;
            updateRedstone();
            init();
            sync();
        }
    }

    /**
     * Gets called the first tick this tile exists
     */
    public void init() {
        disguiseInventory.addListener(inv -> {
            refreshDisguise();
        });
    }

    public ItemStack getDisguiseBlock() {
        return disguiseInventory.getItem(0);
    }

    public void setDisguiseBlock(ItemStack disguise) {
        disguiseInventory.setItem(0, disguise);
    }

    public String getDisguiseState() {
        return disguiseState;
    }

    public void setDisguiseState(String state) {
        disguiseState = state;
        refreshDisguise();
    }

    public SimpleContainer getDisguiseInventory() {
        return disguiseInventory;
    }

    public void refreshDisguise() {
        requestModelDataUpdate();
        setChangedFast();

        BlockState state = getBlockState();
        BlockState from = state;
        ItemStack item = getDisguiseBlock();
        boolean hasDisguise = !item.isEmpty() && item.getItem() instanceof BlockItem;
        state = state.setValue(RedstoneMachineBlock.HAS_DISGUISE, hasDisguise);

        getLevel().setBlockAndUpdate(getBlockPos(), state);

        getLevel().sendBlockUpdated(getBlockPos(), from, state, 3);
        getLevel().getLightEngine().checkBlock(getBlockPos());
    }

    /**
     * Writes inventory information
     */
    public CompoundTag writeItemData(CompoundTag tags) {
        tags.putInt("InventorySize", getContainerSize());
        writeInventoryToNBT(tags);

        if (this.hasCustomName()) {
            tags.putString("CustomName", Component.Serializer.toJson(this.getCustomName()));
        }

        ItemStack disguise = getDisguiseBlock();

        if (!disguise.isEmpty()) {
            CompoundTag itemNBT = new CompoundTag();

            itemNBT = disguise.save(itemNBT);

            tags.put("Disguise", itemNBT);
            if (disguiseState != null)
                tags.putString("DisguiseState", disguiseState);
        }

        return tags;
    }

    /**
     * Reads inventory information
     */
    public void readItemData(CompoundTag tags) {
        super.load(tags);

        if (tags.contains("Disguise")) {
            CompoundTag itemNBT = tags.getCompound("Disguise");

            ItemStack disguise = ItemStack.of(itemNBT);

            if (tags.contains("DisguiseState", CompoundTag.TAG_STRING)) {
                disguiseState = tags.getString("DisguiseState");
            }

            setDisguiseBlock(disguise);
        }
    }

    @Override
    public void saveSynced(CompoundTag tags) {
        super.saveSynced(tags);
        tags = writeItemData(tags);

        tags.putInt("Redstone", redstoneState);
    }

    @Override
    public void load(CompoundTag tags) {
        readItemData(tags);

        redstoneState = tags.getInt("Redstone");
    }

    @Override
    public void writeInventoryToNBT(CompoundTag tag) {
        if (!isEmpty())
            super.writeInventoryToNBT(tag);
    }

    @Override
    protected boolean shouldSyncOnUpdate() {
        return true;
    }

    public void sync() {
        setChanged();

        if (EffectiveSide.get() == LogicalSide.SERVER) {
            ClientboundBlockEntityDataPacket packetUpdateTileEntity = getUpdatePacket();

            if (packetUpdateTileEntity == null) {
                return;
            }

            for (Player player : level.players()) {
                ((ServerPlayer) player).connection.send(packetUpdateTileEntity);
            }
        }
    }

    /**
     * Stores information in an itemstack
     */
    public ItemStack storeTileData(ItemStack stack) {
        CompoundTag tags = writeItemData(new CompoundTag());

        stack.addTagElement("BlockEntityTag", tags);

        if (this.hasCustomName()) {
            CompoundTag name = new CompoundTag();
            name.putString("Name", Component.Serializer.toJson(this.getCustomName()));

            stack.addTagElement("display", name);
        }

        return stack;
    }

    @Nonnull
    @Override
    public ModelData getModelData() {
        return ModelData.builder()
                .with(DisguiseBakedModel.DISGUISE, getDisguiseBlock())
                .with(DisguiseBakedModel.DISGUISE_STATE, getDisguiseState())
                .build();
    }

    @Override
    public void syncInformation(CompoundTag nbt, ServerPlayer player) {
        nbt.putInt("power", getRedstoneState());
    }

    @Override
    public void getInformation(@Nonnull List<Component> info, Player player) {
    }

    @Override
    public void getInformation(@Nonnull List<Component> info, CompoundTag serverData, Player player) {
        if (!serverData.isEmpty())
            info.add(Component.translatable("tooltip.jade.power", serverData.getInt("power")));

        getInformation(info, player);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player playerEntity) {
        return new DisguiseContainerMenu(id, playerInventory, this);
    }

    public final boolean hasFacingDirection() {
        BlockState state = getBlockState();

        if (state.getBlock() instanceof RedstoneMachineBlock) {
            return ((RedstoneMachineBlock) state.getBlock()).hasFacingDirection();
        }

        return state.hasProperty(DirectionalBlock.FACING);
    }

    public static void ticker(Level level, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        if (blockEntity instanceof RedstoneMachineBlockEntity) {
            ((RedstoneMachineBlockEntity) blockEntity).tick();
        }
    }
}
