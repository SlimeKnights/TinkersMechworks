package slimeknights.tmechworks.common.blocks.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import slimeknights.tmechworks.common.MechworksContent;
import slimeknights.tmechworks.common.MechworksTags;
import slimeknights.tmechworks.common.blocks.FirestarterBlock;
import slimeknights.tmechworks.library.Util;

import javax.annotation.Nonnull;
import java.util.List;

import slimeknights.tmechworks.integration.waila.IInformationProvider.InformationType;

public class FirestarterTileEntity extends RedstoneMachineTileEntity
{
    public FirestarterTileEntity()
    {
        super(MechworksContent.TileEntities.firestarter.get(), new TranslationTextComponent(Util.prefix("inventory.firestarter")), 0);
    }

    @Override public void onRedstoneUpdate()
    {
        super.onRedstoneUpdate();

        setFire();
    }

    public void setFire ()
    {
        if(level.isClientSide)
            return;

        BlockState state = getLevel().getBlockState(getBlockPos());
        Direction facing = state.getValue(FirestarterBlock.FACING);
        boolean shouldExtinguish = state.getValue(FirestarterBlock.EXTINGUISH);

        BlockPos loc = getBlockPos();
        BlockPos position = new BlockPos(loc.getX() + facing.getStepX(), loc.getY() + facing.getStepY(), loc.getZ() + facing.getStepZ());

        BlockState forwardState = level.getBlockState(position);
        if (getRedstoneState() > 0)
        {
            if (forwardState.isAir(getLevel(), position) && Blocks.FIRE.canSurvive(forwardState, level, position))
            {
                level.playSound(null, loc.getX() + 0.5D, loc.getY() + 0.5D, loc.getZ() + 0.5D, SoundEvents.FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, Util.rand.nextFloat() * 0.4F + 0.8F);
                level.setBlock(position, Blocks.FIRE.defaultBlockState(), 11);
            }
        } else if (shouldExtinguish && MechworksTags.Blocks.FIRESTARTER_WHITELIST.contains(forwardState.getBlock()))
        {
            level.playSound(null, loc.getX() + 0.5D, loc.getY() + 0.5D, loc.getZ() + 0.5D, SoundEvents.FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, Util.rand.nextFloat() * 0.4F + 0.8F);
            level.removeBlock(position, false);
        }
    }

    @Override
    public ItemStack storeTileData(ItemStack stack) {
        return stack;
    }

    @Override
    public void getInformation(@Nonnull List<ITextComponent> info, @Nonnull InformationType type, PlayerEntity player) {
        super.getInformation(info, type, player);
        if(type != InformationType.BODY) {
            return;
        }

        BlockState state = getLevel().getBlockState(getBlockPos());
        boolean shouldExtinguish = state.getValue(FirestarterBlock.EXTINGUISH);

        info.add(new TranslationTextComponent(Util.prefix("tooltip.behaviour"), I18n.get(Util.prefix("tooltip.behaviour.firestarter." + (shouldExtinguish ? "extinguish" : "keep")))));
    }
}
