package slimeknights.tmechworks.common.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import slimeknights.tmechworks.common.blocks.entity.DrawbridgeBlockEntity;
import slimeknights.tmechworks.common.items.MechworksBlockItem;
import slimeknights.tmechworks.library.Util;

import javax.annotation.Nonnull;

public class DrawbridgeBlock extends RedstoneMachineBlock implements IBlockItemConstruct
{
    public static final BooleanProperty ADVANCED = BooleanProperty.create("advanced");

    public DrawbridgeBlock()
    {
        super(Material.METAL);

        registerDefaultState(defaultBlockState().setValue(ADVANCED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);

        builder.add(ADVANCED);
    }

    @Override
    public void fillItemCategory(CreativeModeTab pTab, NonNullList<ItemStack> pItems) {
        super.fillItemCategory(pTab, pItems);
    }

    @Override
    public void writeAdditionalItemData(BlockState state, Level worldIn, BlockPos pos, ItemStack stack) {
        super.writeAdditionalItemData(state, worldIn, pos, stack);

        CompoundTag tags = stack.getOrCreateTag();
        tags.putBoolean("drawAdvanced", state.getValue(ADVANCED));
    }

    @Nonnull
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DrawbridgeBlockEntity(pos, state);
    }

    @Override
    public void setDefaultNBT(CompoundTag nbt, CompoundTag blockState) {
        super.setDefaultNBT(nbt, blockState);

        blockState.putInt("PlaceAngle", 1);
        blockState.putInt("PlaceDirectionRaw", 2);
        blockState.putInt("InventorySize", DrawbridgeBlockEntity.UPGRADES_SIZE + 1);
        nbt.putBoolean("drawAdvanced", false);
    }

    @Override
    public void onBlockItemConstruct(MechworksBlockItem item) {
        // register => registerPropertyForItem
        ItemProperties.register(item, Util.getResource("advanced"), (stack, world, entity, seed) -> {
            boolean advanced = false;

            if(stack.hasTag() && stack.getTag().contains("drawAdvanced", CompoundTag.TAG_BYTE))
                advanced = stack.getTag().getBoolean("drawAdvanced");

            return advanced ? 1F : 0F;
        });
    }
}
