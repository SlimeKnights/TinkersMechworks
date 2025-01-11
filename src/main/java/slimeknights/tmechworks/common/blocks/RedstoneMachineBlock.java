package slimeknights.tmechworks.common.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.network.NetworkHooks;
import slimeknights.mantle.block.entity.InventoryBlockEntity;
import slimeknights.tmechworks.api.disguisestate.DisguiseStates;
import slimeknights.tmechworks.common.blocks.entity.RedstoneMachineBlockEntity;
import slimeknights.tmechworks.library.Util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class RedstoneMachineBlock extends DirectionalBlock implements EntityBlock {
    public static final BooleanProperty HAS_DISGUISE = BooleanProperty.create("has_disguise");
    public static final IntegerProperty LIGHT_VALUE = IntegerProperty.create("light_value", 0, 15);

    public boolean dropState = true;

    protected RedstoneMachineBlock(Material material) {
        super(Block.Properties.of(material).strength(3.5F).noOcclusion());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(HAS_DISGUISE, false)
                .setValue(LIGHT_VALUE, 0));

        if(hasFacingDirection()) {
            this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH));
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        if(hasFacingDirection()) {
            builder.add(FACING);
        }

        // Disguise properties
        builder.add(HAS_DISGUISE);
        builder.add(LIGHT_VALUE);
    }

    public boolean openGui(Player player, Level world, BlockPos pos) {
        if (player instanceof ServerPlayer && !(player instanceof FakePlayer)) {
            BlockEntity te = world.getBlockEntity(pos);

            if (!(te instanceof MenuProvider))
                return false;

            NetworkHooks.openGui((ServerPlayer) player, (MenuProvider) te, pos);
        }

        return true;
    }

    @Override
    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);

        RedstoneMachineBlockEntity logicBase = (RedstoneMachineBlockEntity) worldIn.getBlockEntity(pos);

        if (logicBase != null) {
            logicBase.updateRedstone();
        }
    }

    @Override
    public boolean shouldCheckWeakPower(BlockState state, LevelReader world, BlockPos pos, Direction side) {
        return true;
    }

    public boolean hasFacingDirection() {
        return true;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        if (!hasFacingDirection()) {
            return super.getStateForPlacement(context);
        }

        return super.getStateForPlacement(context).setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nonnull LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, placer, stack);

        // set custom name from named stack
        if (stack.hasCustomHoverName()) {
            BlockEntity be = worldIn.getBlockEntity(pos);

            if (be instanceof InventoryBlockEntity) {
                ((InventoryBlockEntity) be).setCustomName(stack.getHoverName());
            }
        }
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        BlockEntity te = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);

        if (te instanceof RedstoneMachineBlockEntity) {
            List<ItemStack> drops = NonNullList.create();

            RedstoneMachineBlockEntity machine = (RedstoneMachineBlockEntity) te;
            ItemStack item = new ItemStack(this, 1);

            if (dropState)
                machine.storeTileData(item);

            writeAdditionalItemData(state, builder.getLevel(), new BlockPos(builder.getOptionalParameter(LootContextParams.ORIGIN)), item);

            drops.add(item);
            return drops;
        }

        return super.getDrops(state, builder);
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (blockMatches(state, worldIn, pos, newState, isMoving))
            return;

        BlockEntity te = worldIn.getBlockEntity(pos);

        if (te instanceof Container) {
            if (!dropState)
                Containers.dropContents(worldIn, pos, (Container) te);

            worldIn.updateNeighbourForOutputSignal(pos, this);
        }

        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    public void writeAdditionalItemData(BlockState state, Level worldIn, BlockPos pos, ItemStack stack) {
    }

    public boolean blockMatches(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        return state.getBlock() == newState.getBlock();
    }



    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        if (!stack.hasTag())
            return;

        CompoundTag compound = stack.getTag();
        if (compound.contains("BlockEntityTag", CompoundTag.TAG_COMPOUND)) {
            CompoundTag tags = compound.getCompound("BlockEntityTag");

            if (tags.contains("Disguise", CompoundTag.TAG_COMPOUND)) {
                ItemStack disguise = ItemStack.of(tags.getCompound("Disguise"));
                if (disguise != ItemStack.EMPTY) {
                    tooltip.add(new TranslatableComponent(Util.prefix("hud.disguise")).withStyle(ChatFormatting.GRAY, ChatFormatting.BOLD));
                    tooltip.add(disguise.getHoverName());
                }
            }

            if (tags.contains("Items", CompoundTag.TAG_LIST)) {
                ListTag items = tags.getList("Items", CompoundTag.TAG_LIST);

                if (items.size() > 0) {
                    tooltip.add(new TranslatableComponent(Util.prefix("hud.items")).withStyle(ChatFormatting.GRAY, ChatFormatting.BOLD));
                }

                for (int i = 0; i < items.size(); ++i) {
                    CompoundTag itemTag = items.getCompound(i);
                    int slot = itemTag.getByte("Slot") & 255;

                    ItemStack item = ItemStack.of(itemTag);
                    tooltip.add(new TranslatableComponent(Util.prefix("hud.slot"), slot, item.getHoverName(), item.getCount()).withStyle(ChatFormatting.GRAY, ChatFormatting.BOLD));
                }
            }
        }
    }

    /////////////////////////
    // BlockContainer Code //
    /////////////////////////


    /**
     * Called on both Client and Server when World#addBlockEvent is called. On the Server, this may perform additional
     * changes to the world, like pistons replacing the block with an extended base. On the client, the update may
     * involve replacing tile entities, playing sounds, or performing other visual actions to reflect the server side
     * changes.
     */
    @Override
    public boolean triggerEvent(BlockState state, Level worldIn, BlockPos pos, int id, int param) {
        super.triggerEvent(state, worldIn, pos, id, param);
        BlockEntity tileentity = worldIn.getBlockEntity(pos);
        return tileentity != null && tileentity.triggerEvent(id, param);
    }

    /////////////////////////
    // BlockInventory Code //
    /////////////////////////


    @Nonnull
    @Override
    public abstract BlockEntity newBlockEntity(BlockPos pos, BlockState state);

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (!worldIn.isClientSide) {
            this.openGui(player, worldIn, pos);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean canHarvestBlock(BlockState state, BlockGetter world, BlockPos pos, Player player) {
        return true;
    }

    public void setDefaultNBT(CompoundTag nbt, CompoundTag blockState) {
        blockState.putInt("InventorySize", 0);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return RedstoneMachineBlockEntity::ticker;
    }

    ////////////////////////
    // Disguise Overrides //
    ////////////////////////
    public <T> T runOnDisguiseBlock(BlockState state, BlockGetter worldIn, BlockPos pos, Function<BlockState, T> func, Supplier<T> orElse) {
        if (!state.getValue(HAS_DISGUISE))
            return orElse.get();

        BlockEntity te = worldIn.getBlockEntity(pos);

        if (te instanceof RedstoneMachineBlockEntity) {
            RedstoneMachineBlockEntity machine = (RedstoneMachineBlockEntity) te;
            ItemStack disguise = machine.getDisguiseBlock();

            if (disguise.getItem() instanceof BlockItem) {
                BlockState disguiseState = ((BlockItem) disguise.getItem()).getBlock().defaultBlockState();
                disguiseState = DisguiseStates.processDisguiseStates(disguiseState, ((RedstoneMachineBlockEntity) te).getDisguiseState(), state.getValue(BlockStateProperties.FACING));

                return func.apply(disguiseState);
            }
        }

        return orElse.get();
    }

    @Override
    public boolean hasDynamicShape() {
        return true;
    }


//    @Override
//    public boolean isNormalCube(IBlockReader worldIn, BlockPos pos) {
//        BlockState state = worldIn.getBlockState(pos);
//        return runOnDisguiseBlock(state, worldIn, pos, disguise -> disguise.isNormalCube(worldIn, pos), () -> super.isNormalCube(worldIn, pos));
//    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return runOnDisguiseBlock(state, worldIn, pos, disguise -> disguise.getShape(worldIn, pos, context), () -> super.getShape(state, worldIn, pos, context));
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return runOnDisguiseBlock(state, worldIn, pos, disguise -> disguise.getOcclusionShape(worldIn, pos), () -> super.getOcclusionShape(state, worldIn, pos));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return runOnDisguiseBlock(state, worldIn, pos, disguise -> disguise.getCollisionShape(worldIn, pos, context), () -> super.getCollisionShape(state, worldIn, pos, context));
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return runOnDisguiseBlock(state, reader, pos, disguise -> disguise.propagatesSkylightDown(reader, pos), () -> super.propagatesSkylightDown(state, reader, pos));
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return runOnDisguiseBlock(state, worldIn, pos, disguise -> disguise.getLightBlock(worldIn, pos), worldIn::getMaxLightLevel);
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
        return runOnDisguiseBlock(state, world, pos, disguise -> disguise.getLightEmission(world, pos), () -> super.getLightEmission(state, world, pos));
    }

    //TODO seems hardcoded for now
//    @Override
//    public boolean canBeConnectedTo(BlockState state, IBlockReader world, BlockPos pos, Direction facing) {
//        return runOnDisguiseBlock(state, world, pos, disguise -> disguise.canBeConnectedTo(world, pos, facing), () -> super.canBeConnectedTo(state, world, pos, facing));
//    }
}
