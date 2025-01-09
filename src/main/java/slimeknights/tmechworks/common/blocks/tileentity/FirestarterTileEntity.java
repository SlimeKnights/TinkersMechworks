package slimeknights.tmechworks.common.blocks.tileentity;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import slimeknights.tmechworks.common.MechworksContent;
import slimeknights.tmechworks.common.MechworksTags;
import slimeknights.tmechworks.common.blocks.FirestarterBlock;
import slimeknights.tmechworks.library.Util;

import javax.annotation.Nonnull;
import java.util.List;

import slimeknights.tmechworks.integration.waila.IInformationProvider.InformationType;

public class FirestarterTileEntity extends RedstoneMachineTileEntity
{
    public FirestarterTileEntity(BlockPos pos, BlockState state)
    {
        super(MechworksContent.TileEntities.firestarter.get(), pos, state, new TranslatableComponent(Util.prefix("inventory.firestarter")), 0);
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
            if (forwardState.isAir() && Blocks.FIRE.canSurvive(forwardState, level, position))
            {
                level.playSound(null, loc.getX() + 0.5D, loc.getY() + 0.5D, loc.getZ() + 0.5D, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, Util.rand.nextFloat() * 0.4F + 0.8F);
                level.setBlock(position, Blocks.FIRE.defaultBlockState(), 11);
            }
        } else if (shouldExtinguish && forwardState.is(MechworksTags.Blocks.FIRESTARTER_WHITELIST))
        {
            level.playSound(null, loc.getX() + 0.5D, loc.getY() + 0.5D, loc.getZ() + 0.5D, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, Util.rand.nextFloat() * 0.4F + 0.8F);
            level.removeBlock(position, false);
        }
    }

    @Override
    public ItemStack storeTileData(ItemStack stack) {
        return stack;
    }

    @Override
    public void getInformation(@Nonnull List<Component> info, @Nonnull InformationType type, Player player) {
        super.getInformation(info, type, player);
        if(type != InformationType.BODY) {
            return;
        }

        BlockState state = getLevel().getBlockState(getBlockPos());
        boolean shouldExtinguish = state.getValue(FirestarterBlock.EXTINGUISH);

        info.add(new TranslatableComponent(Util.prefix("tooltip.behaviour"), I18n.get(Util.prefix("tooltip.behaviour.firestarter." + (shouldExtinguish ? "extinguish" : "keep")))));
    }
}
