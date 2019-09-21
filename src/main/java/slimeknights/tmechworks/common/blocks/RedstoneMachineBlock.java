package slimeknights.tmechworks.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.network.NetworkHooks;
import slimeknights.mantle.tileentity.InventoryTileEntity;
import slimeknights.tmechworks.common.blocks.tileentity.RedstoneMachineTileEntity;
import slimeknights.tmechworks.library.Util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public abstract class RedstoneMachineBlock extends DirectionalBlock {
    public boolean dropState = true;

    private TileEntity cachedTE;

    protected RedstoneMachineBlock(Material material) {
        super(Block.Properties.create(material).hardnessAndResistance(3.5F));
        this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(FACING);
    }

    protected boolean openGui(PlayerEntity player, World world, BlockPos pos) {
        if (player instanceof ServerPlayerEntity && !(player instanceof FakePlayer)) {
            TileEntity te = world.getTileEntity(pos);

            if (!(te instanceof INamedContainerProvider))
                return false;

            NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) te, pos);
        }

        return true;
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);

        RedstoneMachineTileEntity logicBase = (RedstoneMachineTileEntity) worldIn.getTileEntity(pos);

        if (logicBase != null) {
            logicBase.updateRedstone();
        }
    }

    @Override
    public boolean shouldCheckWeakPower(BlockState state, IWorldReader world, BlockPos pos, Direction side) {
        return true;
    }

    public boolean hasFacingDirection() {
        return true;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        if (!hasFacingDirection())
            return super.getStateForPlacement(context);

        return this.getDefaultState().with(FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nonnull LivingEntity placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);

        // set custom name from named stack
        if (stack.hasDisplayName()) {
            TileEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof InventoryTileEntity) {
                ((InventoryTileEntity) tileentity).setCustomName(stack.getDisplayName());
            }
        }
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        TileEntity te = builder.get(LootParameters.BLOCK_ENTITY);

        if(te instanceof RedstoneMachineTileEntity) {
            List<ItemStack> drops = NonNullList.create();

            RedstoneMachineTileEntity machine = (RedstoneMachineTileEntity) te;
            ItemStack item = new ItemStack(this, 1);

            writeAdditionalItemData(state, builder.getWorld(), builder.get(LootParameters.POSITION), item);

            if (dropState)
                machine.storeTileData(item);

            drops.add(item);
            return drops;
        }

        return super.getDrops(state, builder);
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (blockMatches(state, worldIn, pos, newState, isMoving))
            return;

        TileEntity te = worldIn.getTileEntity(pos);

        if(te instanceof IInventory) {
            if(!dropState)
                InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory)te);

            worldIn.updateComparatorOutputLevel(pos, this);
        }

        super.onReplaced(state, worldIn, pos, newState, isMoving);
    }

    public void writeAdditionalItemData(BlockState state, World worldIn, BlockPos pos, ItemStack stack) {
    }

    public boolean blockMatches(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving){
        return state.getBlock() == newState.getBlock();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (!stack.hasTag())
            return;

        CompoundNBT compound = stack.getTag();
        if (compound.contains("BlockEntityTag", Constants.NBT.TAG_COMPOUND)) {
            CompoundNBT tags = compound.getCompound("BlockEntityTag");

            if (tags.contains("Disguise", Constants.NBT.TAG_COMPOUND)) {
                ItemStack disguise = ItemStack.read(tags.getCompound("Disguise"));
                if (disguise != ItemStack.EMPTY) {
                    tooltip.add(new TranslationTextComponent(Util.prefix("hud.disguise")).applyTextStyles(TextFormatting.GRAY, TextFormatting.BOLD));
                    tooltip.add(disguise.getDisplayName());
                }
            }

            if (tags.contains("Items", Constants.NBT.TAG_LIST)) {
                ListNBT items = tags.getList("Items", Constants.NBT.TAG_LIST);

                if (items.size() > 0) {
                    tooltip.add(new TranslationTextComponent(Util.prefix("hud.items")).applyTextStyles(TextFormatting.GRAY, TextFormatting.BOLD));
                }

                for (int i = 0; i < items.size(); ++i) {
                    CompoundNBT itemTag = items.getCompound(i);
                    int slot = itemTag.getByte("Slot") & 255;

                    ItemStack item = ItemStack.read(itemTag);
                    tooltip.add(new TranslationTextComponent(Util.prefix("hud.slot"), slot, item.getDisplayName(), item.getCount()).applyTextStyles(TextFormatting.GRAY, TextFormatting.BOLD));
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
    public boolean eventReceived(BlockState state, World worldIn, BlockPos pos, int id, int param) {
        super.eventReceived(state, worldIn, pos, id, param);
        TileEntity tileentity = worldIn.getTileEntity(pos);
        return tileentity != null && tileentity.receiveClientEvent(id, param);
    }

    /////////////////////////
    // BlockInventory Code //
    /////////////////////////


    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nonnull
    @Override
    public abstract TileEntity createTileEntity(BlockState state, IBlockReader world);

    @Override
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (!worldIn.isRemote)
            return this.openGui(player, worldIn, pos);

        return true;
    }

    @Override
    public boolean canHarvestBlock(BlockState state, IBlockReader world, BlockPos pos, PlayerEntity player) {
        return true;
    }

    public void setDefaultNBT(CompoundNBT nbt, CompoundNBT blockState){
        blockState.put("Items", new ListNBT());
        blockState.putInt("InventorySize", 0);
    }
}
