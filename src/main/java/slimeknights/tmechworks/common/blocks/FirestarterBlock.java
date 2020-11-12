package slimeknights.tmechworks.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import slimeknights.tmechworks.common.blocks.tileentity.FirestarterTileEntity;
import slimeknights.tmechworks.common.items.MechworksBlockItem;
import slimeknights.tmechworks.library.Util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class FirestarterBlock extends RedstoneMachineBlock implements IBlockItemConstruct
{
    public static final BooleanProperty EXTINGUISH = BooleanProperty.create("extinguish");

    public FirestarterBlock()
    {
        super(Material.IRON);
        setDefaultState(getDefaultState().with(EXTINGUISH, true));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(EXTINGUISH);
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        ItemStack extinguishStack = new ItemStack(this, 1);
        ItemStack keepLitStack = new ItemStack(this, 1);

        CompoundNBT extinguish = extinguishStack.getOrCreateTag();
        extinguish.putBoolean("extinguish", true);
        CompoundNBT keepLit = keepLitStack.getOrCreateTag();
        keepLit.putBoolean("extinguish", false);

        items.add(extinguishStack);
        items.add(keepLitStack);
    }

    @Override
    public void writeAdditionalItemData(BlockState state, World worldIn, BlockPos pos, ItemStack stack) {
        super.writeAdditionalItemData(state, worldIn, pos, stack);

        CompoundNBT tags = stack.getOrCreateTag();
        tags.putBoolean("extinguish", state.get(EXTINGUISH));
    }

    @Nonnull
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new FirestarterTileEntity();
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        boolean shouldExtinguish = true;
        ItemStack stack = context.getItem();

        if(stack.hasTag() && stack.getTag().contains("extinguish", Constants.NBT.TAG_BYTE))
            shouldExtinguish = stack.getTag().getBoolean("extinguish");

        return super.getStateForPlacement(context).with(EXTINGUISH, shouldExtinguish);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if(player.isCrouching())
            return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);

        state = state.func_235896_a_(EXTINGUISH); // func_235896_a_ => cycleValue

        worldIn.setBlockState(pos, state);
        worldIn.playSound(player, pos, SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCKS, 0.3F, 0.55F);

        return ActionResultType.SUCCESS;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);

        boolean shouldExtinguish = true;

        if(stack.hasTag() && stack.getTag().contains("extinguish", Constants.NBT.TAG_BYTE))
            shouldExtinguish = stack.getTag().getBoolean("extinguish");

        tooltip.add(new TranslationTextComponent(Util.prefix("tooltip.behaviour"), I18n.format(Util.prefix("tooltip.behaviour.firestarter." + (shouldExtinguish ? "extinguish" : "keep")))).mergeStyle(TextFormatting.GRAY));
    }

    @Override
    public void setDefaultNBT(CompoundNBT nbt, CompoundNBT blockState) {
        // Firestarter does not have an inventory
        //        super.setDefaultNBT(nbt, blockState);

        if(!nbt.contains("extinguish"))
            nbt.putBoolean("extinguish", true);
    }

    @Override
    public void onBlockItemConstruct(MechworksBlockItem item) {
        ItemModelsProperties.registerProperty(item, new ResourceLocation("extinguish"), (stack, world, entity) -> {
            boolean shouldExtinguish = true;

            if(stack.hasTag() && stack.getTag().contains("extinguish", Constants.NBT.TAG_BYTE))
                shouldExtinguish = stack.getTag().getBoolean("extinguish");

            return shouldExtinguish ? 1F : 0F;
        });
    }
}
