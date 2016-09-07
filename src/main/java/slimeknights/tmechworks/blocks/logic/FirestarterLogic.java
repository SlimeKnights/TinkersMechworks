package slimeknights.tmechworks.blocks.logic;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import slimeknights.tmechworks.library.Util;

public class FirestarterLogic extends RedstoneMachineLogicBase
{

    private boolean shouldExtinguish = true;

    public FirestarterLogic ()
    {
        super(Util.prefix("inventory.firestarter"), 0);
    }

    @Override public void onBlockUpdate ()
    {
        super.onBlockUpdate();

        setFire();
    }

    public void setFire ()
    {
        EnumFacing facing = getFacingDirection();

        BlockPos loc = getPos();
        BlockPos position = new BlockPos(loc.getX() + facing.getFrontOffsetX(), loc.getY() + facing.getFrontOffsetY(), loc.getZ() + facing.getFrontOffsetZ());

        IBlockState state = worldObj.getBlockState(position);
        if (getRedstoneState() > 0)
        {
            if (state.getBlock() == Blocks.AIR && Blocks.FIRE.canPlaceBlockAt(worldObj, position))
            {
                worldObj.playSound(null, loc.getX() + 0.5D, loc.getY() + 0.5D, loc.getZ() + 0.5D, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, Util.rand.nextFloat() * 0.4F + 0.8F);
                worldObj.setBlockState(position, Blocks.FIRE.getDefaultState(), 11);
            }
        } else if (shouldExtinguish && state.getBlock() == Blocks.FIRE)
        {
            worldObj.playSound(null, loc.getX() + 0.5D, loc.getY() + 0.5D, loc.getZ() + 0.5D, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, Util.rand.nextFloat() * 0.4F + 0.8F);
            worldObj.setBlockToAir(position);
        }
    }

    public void setShouldExtinguish(boolean shouldExtinguish) {
        this.shouldExtinguish = shouldExtinguish;
        markDirty();
    }

    public boolean getShouldExtinguish() {
        return shouldExtinguish;
    }

    @Override public NBTTagCompound writeToNBT (NBTTagCompound tags)
    {
        tags = super.writeToNBT(tags);

        tags.setBoolean("ShouldExtinguish", shouldExtinguish);

        return tags;
    }

    @Override public void readFromNBT (NBTTagCompound tags)
    {
        super.readFromNBT(tags);

        shouldExtinguish = tags.getBoolean("ShouldExtinguish");
    }
}
