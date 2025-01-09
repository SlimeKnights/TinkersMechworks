package slimeknights.tmechworks.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import slimeknights.tmechworks.common.blocks.tileentity.DrawbridgeTileEntity;
import slimeknights.tmechworks.common.items.MechworksBlockItem;

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
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);

        builder.add(ADVANCED);
    }

    @Override
    public void fillItemCategory(ItemGroup pTab, NonNullList<ItemStack> pItems) {
        super.fillItemCategory(pTab, pItems);
    }

    @Override
    public void writeAdditionalItemData(BlockState state, World worldIn, BlockPos pos, ItemStack stack) {
        super.writeAdditionalItemData(state, worldIn, pos, stack);

        CompoundNBT tags = stack.getOrCreateTag();
        tags.putBoolean("drawAdvanced", state.getValue(ADVANCED));
    }

    @Nonnull
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new DrawbridgeTileEntity();
    }

    @Override
    public void setDefaultNBT(CompoundNBT nbt, CompoundNBT blockState) {
        super.setDefaultNBT(nbt, blockState);

        blockState.putInt("PlaceAngle", 1);
        blockState.putInt("PlaceDirectionRaw", 2);
        blockState.putInt("InventorySize", DrawbridgeTileEntity.UPGRADES_SIZE + 1);
        nbt.putBoolean("drawAdvanced", false);
    }

    @Override
    public void onBlockItemConstruct(MechworksBlockItem item) {
        // register => registerPropertyForItem
        ItemModelsProperties.register(item, new ResourceLocation("advanced"), (stack, world, entity) -> {
            boolean advanced = false;

            if(stack.hasTag() && stack.getTag().contains("drawAdvanced", Constants.NBT.TAG_BYTE))
                advanced = stack.getTag().getBoolean("drawAdvanced");

            return advanced ? 1F : 0F;
        });
    }
}
