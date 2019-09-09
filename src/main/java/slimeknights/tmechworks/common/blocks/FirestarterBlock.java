package slimeknights.tmechworks.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
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
import slimeknights.tmechworks.common.TMechContent;
import slimeknights.tmechworks.common.blocks.tileentity.FirestarterTileEntity;
import slimeknights.tmechworks.library.Util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class FirestarterBlock extends RedstoneMachineBlock
{
    public final boolean shouldExtinguish;

    public FirestarterBlock(boolean shouldExtinguish)
    {
        super(Material.IRON);

        this.shouldExtinguish = shouldExtinguish;
    }

    @Nonnull
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new FirestarterTileEntity();
    }

    @Override
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        Block newBlock = shouldExtinguish ? TMechContent.firestarter_keeplit : TMechContent.firestarter;
        BlockState newState = newBlock.getDefaultState();
        newState = newState.with(FACING, state.get(FACING));

        worldIn.setBlockState(pos, newState);
        worldIn.playSound(player, pos, SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCKS, 0.3F, 0.55F);

        return true;
    }

    @Override
    public boolean blockMatches(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        return newState.getBlock() == TMechContent.firestarter || newState.getBlock() == TMechContent.firestarter_keeplit;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);

        tooltip.add(new TranslationTextComponent(Util.prefix("hud.behaviour")).appendText(": ").appendSibling(new TranslationTextComponent(Util.prefix("hud.behaviour.firestarter." + (shouldExtinguish ? "extinguish" : "keep")))).applyTextStyle(TextFormatting.GRAY));
    }

    @Override
    public String getTranslationKey() {
        return "block.tmechworks.firestarter";
    }
}
