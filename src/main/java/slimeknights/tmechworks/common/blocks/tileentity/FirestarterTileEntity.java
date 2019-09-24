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
import slimeknights.tmechworks.common.blocks.FirestarterBlock;
import slimeknights.tmechworks.library.Util;

import javax.annotation.Nonnull;
import java.util.List;

public class FirestarterTileEntity extends RedstoneMachineTileEntity
{
    public FirestarterTileEntity()
    {
        super(MechworksContent.TileEntities.firestarter, new TranslationTextComponent(Util.prefix("inventory.firestarter")), 0);
    }

    @Override public void onRedstoneUpdate()
    {
        super.onRedstoneUpdate();

        setFire();
    }

    public void setFire ()
    {
        if(world.isRemote)
            return;

        BlockState state = getWorld().getBlockState(getPos());
        Direction facing = state.get(FirestarterBlock.FACING);
        boolean shouldExtinguish = state.get(FirestarterBlock.EXTINGUISH);

        BlockPos loc = getPos();
        BlockPos position = new BlockPos(loc.getX() + facing.getXOffset(), loc.getY() + facing.getYOffset(), loc.getZ() + facing.getZOffset());

        BlockState forwardState = world.getBlockState(position);
        if (getRedstoneState() > 0)
        {
            if (forwardState.getBlock() == Blocks.AIR && Blocks.FIRE.isValidPosition(forwardState, world, position))
            {
                world.playSound(null, loc.getX() + 0.5D, loc.getY() + 0.5D, loc.getZ() + 0.5D, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, Util.rand.nextFloat() * 0.4F + 0.8F);
                world.setBlockState(position, Blocks.FIRE.getDefaultState(), 11);
            }
        } else if (shouldExtinguish && (forwardState.getBlock() == Blocks.FIRE || forwardState.getBlock() == Blocks.NETHER_PORTAL))
        {
            world.playSound(null, loc.getX() + 0.5D, loc.getY() + 0.5D, loc.getZ() + 0.5D, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, Util.rand.nextFloat() * 0.4F + 0.8F);
            world.setBlockState(position, Blocks.AIR.getDefaultState());
        }
    }

    @Override
    public ItemStack storeTileData(ItemStack stack) {
        return stack;
    }

    @Override
    public void getInformation(@Nonnull List<ITextComponent> info, InformationType type, PlayerEntity player) {
        super.getInformation(info, type, player);
        if(type != InformationType.BODY) {
            return;
        }

        BlockState state = getWorld().getBlockState(getPos());
        boolean shouldExtinguish = state.get(FirestarterBlock.EXTINGUISH);

        info.add(new TranslationTextComponent(Util.prefix("hud.behaviour"), I18n.format(Util.prefix("hud.behaviour.firestarter." + (shouldExtinguish ? "extinguish" : "keep")))));
    }
}
