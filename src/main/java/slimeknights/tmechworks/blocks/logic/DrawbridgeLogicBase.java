package slimeknights.tmechworks.blocks.logic;

import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.common.util.FakePlayer;
import slimeknights.mantle.common.IInventoryGui;
import slimeknights.tmechworks.library.Util;

import javax.annotation.Nonnull;

public abstract class DrawbridgeLogicBase extends RedstoneMachineLogicBase implements ITickable, IInventoryGui
{
    private static final float TICK_TIME = 0.05F;

    private FakePlayer fakePlayer;
    private DrawbridgeStats statistics = new DrawbridgeStats();

    private int extendState;
    private boolean isExtended;
    private boolean isExtending;
    private float cooldown;

    private EnumFacing placeDirection = null;

    private long lastWorldTime;

    public DrawbridgeLogicBase (String name, int inventorySize)
    {
        super(name, inventorySize);
    }

    @Override public void onBlockUpdate ()
    {
        if (isExtended && getRedstoneState() <= 0)
        {
            isExtended = false;
            isExtending = true;
        }
        else if (!isExtended && getRedstoneState() > 0)
        {
            isExtended = true;
            isExtending = true;
        }
    }

    public void playExtendSound ()
    {
        worldObj.playSound(null, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, 0.25F,
                Util.rand.nextFloat() * 0.25F + 0.6F);
    }

    public void playRetractSound ()
    {
        worldObj.playSound(null, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, SoundEvents.BLOCK_PISTON_CONTRACT, SoundCategory.BLOCKS, 0.25F,
                Util.rand.nextFloat() * 0.15F + 0.6F);
    }

    public int getExtendState ()
    {
        return extendState;
    }

    public boolean getExtended ()
    {
        return isExtended;
    }

    public boolean getExtending ()
    {
        return isExtending;
    }

    public EnumFacing getPlaceDirection ()
    {
        return placeDirection;
    }

    public void setPlaceDirection (EnumFacing direction)
    {
        placeDirection = direction;
    }

    public void setPlaceDirectionRelativeToBlock (EnumFacing direction)
    {
        switch (direction)
        {
        case UP:
        case DOWN:
            setPlaceDirection(direction);
            break;
        case NORTH:
            setPlaceDirection(getFacingDirection());
            break;
        case SOUTH:
            setPlaceDirection(getFacingDirection().getOpposite());
            break;
        case EAST:
            setPlaceDirection(getFacingDirection().rotateY());
            break;
        case WEST:
            setPlaceDirection(getFacingDirection().rotateY().getOpposite());
            break;
        }
    }

    public DrawbridgeStats getStats ()
    {
        return statistics;
    }

    @Override public void loadData ()
    {
        super.loadData();

        if (placeDirection == null)
        {
            setPlaceDirectionRelativeToBlock(EnumFacing.NORTH);
        }

        setupStatistics(statistics);

        lastWorldTime = worldObj.getWorldTime();
    }

    public void updateFakePlayer ()
    {
        if (fakePlayer == null)
        {
            fakePlayer = Util.createFakePlayer(worldObj);
        }
        if (fakePlayer == null)
        {
            return;
        }

        fakePlayer.rotationYaw = 0;
        fakePlayer.rotationPitch = 0;
        fakePlayer.posX = getPos().getX();
        fakePlayer.posY = getPos().getY();
        fakePlayer.posZ = getPos().getZ();

        switch (placeDirection)
        {
        case NORTH:
            fakePlayer.rotationYaw = 0;
            fakePlayer.posZ += 2;
            break;
        case SOUTH:
            fakePlayer.rotationYaw = 180;
            fakePlayer.posZ -= 2;
        case UP:
            fakePlayer.rotationPitch = 90;
            fakePlayer.posY += 2;
            break;
        case DOWN:
            fakePlayer.rotationPitch = -90;
            fakePlayer.posY -= 2;
            break;
        case EAST:
            fakePlayer.rotationYaw = 90;
            fakePlayer.posX -= 2;
            break;
        case WEST:
            fakePlayer.rotationYaw = -90;
            fakePlayer.posX += 2;
            break;
        }
    }

    public FakePlayer getFakePlayer ()
    {
        return fakePlayer;
    }

    @Override public void update ()
    {
        super.update();

        if (placeDirection == null)
        {
            setPlaceDirectionRelativeToBlock(EnumFacing.NORTH);
        }

        if (isExtending)
        {
            if (cooldown > 0)
            {
                cooldown -= (worldObj.getTotalWorldTime() - lastWorldTime) * TICK_TIME;
            }
            else if (isExtended)
            {
                updateFakePlayer();
                if (extendState == statistics.extendLength)
                {
                    isExtending = false;
                }
                else if (extendNext())
                {
                    extendState++;
                    cooldown = statistics.extendDelay;
                    playExtendSound();
                }
                else
                {
                    isExtending = false;
                }
            }
            else
            {
                updateFakePlayer();
                if (extendState <= 0)
                {
                    isExtending = false;
                }
                else if (retractNext())
                {
                    extendState--;
                    cooldown = statistics.extendDelay;
                    playRetractSound();
                }
                else
                {
                    isExtending = false;
                    extendState = 0;
                }
            }
        }

        lastWorldTime = worldObj.getTotalWorldTime();
    }

    @Override public void invalidate ()
    {
        super.invalidate();

        if (fakePlayer != null)
        {
            worldObj.removeEntity(fakePlayer);
            fakePlayer = null;
        }
    }

    @Override public void validate ()
    {
        super.validate();
    }

    /* NBT */
    @Override public void readFromNBT (NBTTagCompound tags)
    {
        super.readFromNBT(tags);

        extendState = tags.getInteger("ExtendState");
        isExtended = tags.getBoolean("IsExtended");
        isExtending = tags.getBoolean("IsExtending");
        cooldown = tags.getFloat("Cooldown");
        placeDirection = EnumFacing.values()[tags.getInteger("PlaceDirection")];
    }

    @Nonnull @Override public NBTTagCompound writeToNBT (NBTTagCompound tags)
    {
        NBTTagCompound data = super.writeToNBT(tags);

        data.setInteger("ExtendState", extendState);
        data.setBoolean("IsExtended", isExtended);
        data.setBoolean("IsExtending", isExtending);
        data.setFloat("Cooldown", cooldown);

        if (placeDirection == null)
        {
            setPlaceDirectionRelativeToBlock(EnumFacing.NORTH);
        }

        data.setInteger("PlaceDirection", placeDirection.ordinal());

        return data;
    }

    public abstract void setupStatistics (DrawbridgeStats ds);

    public abstract boolean extendNext ();

    public abstract boolean retractNext ();

    final class DrawbridgeStats
    {

        public int extendLength = 16;
        public float extendDelay = 0.5F;
    }
}
