package slimeknights.tmechworks.blocks.logic;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import slimeknights.tmechworks.library.Util;

import javax.annotation.Nonnull;
import java.util.List;

public class FirestarterLogic extends RedstoneMachineLogicBase
{

    private boolean shouldExtinguish = true;

    public FirestarterLogic ()
    {
        super(Util.prefix("inventory.firestarter"), 0);
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

        EnumFacing facing = getFacingDirection();

        BlockPos loc = getPos();
        BlockPos position = new BlockPos(loc.getX() + facing.getFrontOffsetX(), loc.getY() + facing.getFrontOffsetY(), loc.getZ() + facing.getFrontOffsetZ());

        IBlockState state = world.getBlockState(position);
        if (getRedstoneState() > 0)
        {
            if (state.getBlock() == Blocks.AIR && Blocks.FIRE.canPlaceBlockAt(world, position))
            {
                world.playSound(null, loc.getX() + 0.5D, loc.getY() + 0.5D, loc.getZ() + 0.5D, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, Util.rand.nextFloat() * 0.4F + 0.8F);
                world.setBlockState(position, Blocks.FIRE.getDefaultState(), 11);
            }
        } else if (shouldExtinguish && (state.getBlock() == Blocks.FIRE || state.getBlock() == Blocks.PORTAL))
        {
            world.playSound(null, loc.getX() + 0.5D, loc.getY() + 0.5D, loc.getZ() + 0.5D, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, Util.rand.nextFloat() * 0.4F + 0.8F);
            world.setBlockToAir(position);
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

    @Override
    public void getInformation(@Nonnull List<String> info, InformationType type) {
        super.getInformation(info, type);
        if(type != InformationType.BODY) {
            return;
        }

        info.add(I18n.format("tmechworks.hud.behaviour") + ": " + I18n.format("tmechworks.hud.behaviour.firestarter." + (getShouldExtinguish() ? "extinguish" : "keep")));
    }
}
