package slimeknights.tmechworks.blocks.logic;

import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.FakePlayer;
import slimeknights.mantle.common.IInventoryGui;
import slimeknights.tmechworks.library.Util;

import javax.annotation.Nonnull;

public abstract class DrawbridgeLogicBase extends RedstoneMachineLogicBase implements ITickable, IInventoryGui, IPlaceDirection {
    private static final float TICK_TIME = 0.05F;

    private FakePlayer fakePlayer;
    private DrawbridgeStats statistics = new DrawbridgeStats();

    private int extendState;
    private boolean isExtended;
    private boolean isExtending;
    private float cooldown;

    private EnumFacing rawPlaceDirection = null;
    private EnumFacing placeDirection = null;
    private Angle placeAngle = Angle.NEUTRAL;

    private long lastWorldTime;

    public DrawbridgeLogicBase(String name, int inventorySize) {
        super(name, inventorySize);
    }

    @Override
    public void onBlockUpdate() {
        if (isExtended && getRedstoneState() <= 0) {
            isExtended = false;
            isExtending = true;
        } else if (!isExtended && getRedstoneState() > 0) {
            isExtended = true;
            isExtending = true;
        }

        markDirty();
    }

    public void playExtendSound() {
        world.playSound(null, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, 0.25F,
                Util.rand.nextFloat() * 0.25F + 0.6F);
    }

    public void playRetractSound() {
        world.playSound(null, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, SoundEvents.BLOCK_PISTON_CONTRACT, SoundCategory.BLOCKS, 0.25F,
                Util.rand.nextFloat() * 0.15F + 0.6F);
    }

    public int getExtendState() {
        return extendState;
    }

    public boolean getExtended() {
        return isExtended;
    }

    public boolean getExtending() {
        return isExtending;
    }

    public Angle getPlaceAngle() {
        return placeAngle;
    }

    public void setPlaceAngle(Angle angle) {
        placeAngle = angle;
        markDirty();
    }

    public EnumFacing getRawPlaceDirection() {
        return rawPlaceDirection;
    }

    public EnumFacing getPlaceDirection() {
        return placeDirection;
    }

    public void setPlaceDirection(EnumFacing direction) {
        placeDirection = direction;
        markDirty();
    }

    public void setPlaceDirectionRelativeToBlock(EnumFacing direction) {
        rawPlaceDirection = direction;

        switch (direction) {
            case UP:
                switch (getFacingDirection()) {
                    case UP:
                        setPlaceDirection(EnumFacing.SOUTH);
                        break;
                    case DOWN:
                        setPlaceDirection(EnumFacing.NORTH);
                        break;
                    default:
                        setPlaceDirection(EnumFacing.UP);
                        break;
                }
                break;
            case DOWN:
                switch (getFacingDirection()) {
                    case UP:
                        setPlaceDirection(EnumFacing.NORTH);
                        break;
                    case DOWN:
                        setPlaceDirection(EnumFacing.SOUTH);
                        break;
                    default:
                        setPlaceDirection(EnumFacing.DOWN);
                        break;
                }
                break;
            case NORTH:
                setPlaceDirection(getFacingDirection());
                break;
            case SOUTH:
                setPlaceDirection(getFacingDirection().getOpposite());
                break;
            case EAST:
                switch (getFacingDirection()) {
                    case UP:
                    case DOWN:
                        setPlaceDirection(direction);
                        break;
                    default:
                        setPlaceDirection(getFacingDirection().rotateY());
                        break;
                }
                break;
            case WEST:
                switch (getFacingDirection()) {
                    case UP:
                    case DOWN:
                        setPlaceDirection(direction);
                        break;
                    default:
                        setPlaceDirection(getFacingDirection().rotateYCCW());
                        break;
                }
                break;
        }
    }

    @Override
    public void setPlaceDirectioni(int direction) {
        if (direction < EnumFacing.values().length)
            setPlaceDirectionRelativeToBlock(EnumFacing.values()[direction]);
        else if (direction - EnumFacing.values().length < Angle.values().length)
            setPlaceAngle(Angle.values()[direction - EnumFacing.values().length]);
        else
            throw new IllegalArgumentException("Direction " + direction + " cannot be mapped to any direction or angle.");
    }

    public DrawbridgeStats getStats() {
        return statistics;
    }

    @Override
    public void loadData() {
        super.loadData();

        if (rawPlaceDirection == null)
            rawPlaceDirection = EnumFacing.NORTH;
        if (placeDirection == null)
            setPlaceDirectionRelativeToBlock(rawPlaceDirection);
        if (placeAngle == null)
            setPlaceAngle(Angle.NEUTRAL);

        setupStatistics(statistics);

        lastWorldTime = world.getWorldTime();
    }

    public void updateFakePlayer(int x, int y, int z) {
        if (fakePlayer == null) {
            fakePlayer = Util.createFakePlayer(world);
        }
        if (fakePlayer == null) {
            return;
        }

        fakePlayer.rotationYaw = 0;
        fakePlayer.rotationPitch = 0;
        fakePlayer.posX = x;
        fakePlayer.posY = y;
        fakePlayer.posZ = z;

        switch (placeDirection) {
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

        switch (placeAngle) {
            case HIGH:
                fakePlayer.rotationPitch -= 45;
                break;
            case LOW:
                fakePlayer.rotationPitch += 45;
                break;
        }
    }

    public FakePlayer getFakePlayer() {
        BlockPos pos = getPos();
        return getFakePlayer(pos.getX(), pos.getY(), pos.getZ());
    }

    public FakePlayer getFakePlayer(int x, int y, int z) {
        updateFakePlayer(x, y, z);

        return fakePlayer;
    }

    @Override
    public void update() {
        super.update();

        if (placeDirection == null) {
            setPlaceDirectionRelativeToBlock(EnumFacing.NORTH);
        }

        if (isExtending) {
            if (cooldown > 0) {
                cooldown -= (world.getTotalWorldTime() - lastWorldTime) * TICK_TIME;
            } else if (isExtended) {
                if (extendState == statistics.extendLength) {
                    isExtending = false;
                } else if (extendNext()) {
                    extendState++;
                    cooldown = statistics.extendDelay;
                    playExtendSound();
                } else {
                    isExtending = false;
                }
            } else {
                if (extendState <= 0) {
                    isExtending = false;
                } else if (retractNext()) {
                    extendState--;
                    cooldown = statistics.extendDelay;
                    playRetractSound();
                } else {
                    isExtending = false;
                    extendState = 0;
                }
            }

            markDirty();
        }

        lastWorldTime = world.getTotalWorldTime();
    }

    @Override
    public void invalidate() {
        super.invalidate();

        if (fakePlayer != null) {
            world.removeEntity(fakePlayer);
            fakePlayer = null;
        }
    }

    @Override
    public void validate() {
        super.validate();
    }

    /* NBT */
    @Override
    public void readItemData(NBTTagCompound tags) {
        super.readItemData(tags);

        rawPlaceDirection = EnumFacing.values()[tags.getInteger("PlaceDirectionRaw")];
        placeAngle = Angle.values()[tags.getInteger("PlaceAngle")];
    }

    @Override
    public NBTTagCompound writeItemData(NBTTagCompound tags) {
        tags = super.writeItemData(tags);

        if (rawPlaceDirection == null)
            rawPlaceDirection = EnumFacing.NORTH;

        tags.setInteger("PlaceDirectionRaw", rawPlaceDirection.ordinal());
        tags.setInteger("PlaceAngle", placeAngle.ordinal());

        return tags;
    }

    @Override
    public void readFromNBT(NBTTagCompound tags) {
        super.readFromNBT(tags);

        extendState = tags.getInteger("ExtendState");
        isExtended = tags.getBoolean("IsExtended");
        isExtending = tags.getBoolean("IsExtending");
        cooldown = tags.getFloat("Cooldown");
        placeDirection = EnumFacing.values()[tags.getInteger("PlaceDirection")];
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tags) {
        tags = super.writeToNBT(tags);

        tags.setInteger("ExtendState", extendState);
        tags.setBoolean("IsExtended", isExtended);
        tags.setBoolean("IsExtending", isExtending);
        tags.setFloat("Cooldown", cooldown);

        if (placeDirection == null)
            setPlaceDirectionRelativeToBlock(rawPlaceDirection);

        tags.setInteger("PlaceDirection", placeDirection.ordinal());

        return tags;
    }

    @Override
    public String getName() {
        if (hasCustomName())
            return super.getName();

        return super.getName() + "." + getVariantName();
    }

    public abstract void setupStatistics(DrawbridgeStats ds);

    public abstract boolean extendNext();

    public abstract boolean retractNext();

    public abstract String getVariantName();

    final class DrawbridgeStats {

        public int extendLength = 16;
        public float extendDelay = 0.5F;
    }

    public enum Angle {
        NEUTRAL,
        HIGH,
        LOW
    }
}
