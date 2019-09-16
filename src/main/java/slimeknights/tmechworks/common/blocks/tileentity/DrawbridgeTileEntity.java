package slimeknights.tmechworks.common.blocks.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import slimeknights.tmechworks.common.MechworksConfig;
import slimeknights.tmechworks.common.MechworksContent;
import slimeknights.tmechworks.common.blocks.DrawbridgeBlock;
import slimeknights.tmechworks.common.blocks.RedstoneMachineBlock;
import slimeknights.tmechworks.common.inventory.DrawbridgeContainer;
import slimeknights.tmechworks.common.inventory.FragmentedInventory;
import slimeknights.tmechworks.common.items.MachineUpgradeItem;
import slimeknights.tmechworks.library.Util;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;

public class DrawbridgeTileEntity extends RedstoneMachineTileEntity implements IPlaceDirection {
    private static final float TICK_TIME = 0.05F;

    public DrawbridgeStats stats;

    public static final int UPGRADES_SIZE = 4;

    public final FragmentedInventory upgrades;
    public final FragmentedInventory slots;

    private WeakReference<FakePlayer> fakePlayer;

    private Direction placeDirection;
    private Direction rawPlaceDirection;
    private Angle placeAngle = Angle.NEUTRAL;

    private long lastWorldTime;

    public DrawbridgeTileEntity() {
        super(MechworksContent.TileEntities.drawbridge, new TranslationTextComponent(Util.prefix("inventory.drawbridge")), UPGRADES_SIZE + 1);

        upgrades = new FragmentedInventory(this, 0, UPGRADES_SIZE).overrideStackLimit(1);
        slots = new FragmentedInventory(this, UPGRADES_SIZE, 1);
    }

    @Override
    public void tick() {
        super.tick();

        float delta = (getWorld().getGameTime() - lastWorldTime) * TICK_TIME;
        lastWorldTime = getWorld().getGameTime();
    }

    @Override
    public void init() {
        super.init();

        if (rawPlaceDirection == null)
            rawPlaceDirection = Direction.NORTH;
        if (placeDirection == null)
            setPlaceDirectionRelativeToBlock(rawPlaceDirection);
        if (placeAngle == null)
            setPlaceAngle(Angle.NEUTRAL);

        computeStats();

        lastWorldTime = world.getGameTime();
    }

    public boolean placeBlock(BlockPos pos, ItemStack stack) {
        if(getWorld().isRemote)
            return false;
        if(stack.isEmpty() || !(stack.getItem() instanceof BlockItem))
            return false;

        FakePlayer player = getFakePlayer(pos);

        if(player == null)
            return false;

        BlockItem item = (BlockItem)stack.getItem();

        double yOffset = 0D;
        switch(getPlaceAngle()){
            case LOW:
                yOffset = 0.1D;
                break;
            case NEUTRAL:
                yOffset = 0.5D;
                break;
            case HIGH:
                yOffset = 0.9D;
                break;
        }

        ItemUseContext ctx = new ItemUseContext(player, Hand.MAIN_HAND, new BlockRayTraceResult(new Vec3d(pos.getX() + 0.5D, pos.getY() + yOffset, pos.getZ() + 0.5D), getPlaceDirection(), pos, false));
        return item.tryPlace(new DrawbridgeItemUseContext(ctx)) == ActionResultType.SUCCESS;
    }

    public void computeStats() {
        DrawbridgeStats stats = new DrawbridgeStats();

        for (int i = 0; i < upgrades.getSizeInventory(); i++) {
            Item item = upgrades.getStackInSlot(i).getItem();

            if (item instanceof MachineUpgradeItem)
                ((MachineUpgradeItem) item).effect.accept(stats);
        }

        this.stats = stats;
        onStatsUpdated();
    }

    public void onStatsUpdated() {
        int blockSlots = stats.isAdvanced ? stats.extendLength : 1;

        resize(UPGRADES_SIZE + blockSlots);
        slots.resize(blockSlots);

        BlockState state = getWorld().getBlockState(getPos());
        getWorld().setBlockState(getPos(), state.with(DrawbridgeBlock.ADVANCED, stats.isAdvanced));
    }

    @Override
    public void readItemData(CompoundNBT tags) {
        super.readItemData(tags);

        rawPlaceDirection = Direction.values()[tags.getInt("PlaceDirectionRaw")];
        placeAngle = Angle.values()[tags.getInt("PlaceAngle")];
    }

    @Override
    public CompoundNBT writeItemData(CompoundNBT tags) {
        if (rawPlaceDirection == null)
            rawPlaceDirection = Direction.NORTH;

        tags.putInt("PlaceDirectionRaw", rawPlaceDirection.ordinal());
        tags.putInt("PlaceAngle", placeAngle.ordinal());

        return super.writeItemData(tags);
    }

    @Nullable
    @Override
    public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new DrawbridgeContainer(id, playerInventory, this);
    }

    @Override
    public void setPlaceDirection(int direction) {
        if (direction < Direction.values().length)
            setPlaceDirectionRelativeToBlock(Direction.values()[direction]);
        else if (direction - Direction.values().length < Angle.values().length)
            setPlaceAngle(Angle.values()[direction - Direction.values().length]);
        else
            throw new IllegalArgumentException("Direction " + direction + " cannot be mapped to any direction or angle.");
    }

    public Direction getPlaceDirection() {
        return placeDirection;
    }

    public Direction getRawPlaceDirection() {
        return rawPlaceDirection;
    }

    public Angle getPlaceAngle() {
        return placeAngle;
    }

    public void setPlaceAngle(Angle angle) {
        placeAngle = angle;
        markDirty();
    }

    public void setPlaceDirection(Direction direction) {
        placeDirection = direction;
        markDirty();
    }

    public void setPlaceDirectionRelativeToBlock(Direction direction) {
        rawPlaceDirection = direction;

        Direction facing = getWorld().getBlockState(getPos()).get(RedstoneMachineBlock.FACING);

        switch (direction) {
            case UP:
                switch (facing) {
                    case UP:
                        setPlaceDirection(Direction.SOUTH);
                        break;
                    case DOWN:
                        setPlaceDirection(Direction.NORTH);
                        break;
                    default:
                        setPlaceDirection(Direction.UP);
                        break;
                }
                break;
            case DOWN:
                switch (facing) {
                    case UP:
                        setPlaceDirection(Direction.NORTH);
                        break;
                    case DOWN:
                        setPlaceDirection(Direction.SOUTH);
                        break;
                    default:
                        setPlaceDirection(Direction.DOWN);
                        break;
                }
                break;
            case NORTH:
                setPlaceDirection(facing);
                break;
            case SOUTH:
                setPlaceDirection(facing.getOpposite());
                break;
            case EAST:
                switch (facing) {
                    case UP:
                    case DOWN:
                        setPlaceDirection(direction);
                        break;
                    default:
                        setPlaceDirection(facing.rotateY());
                        break;
                }
                break;
            case WEST:
                switch (facing) {
                    case UP:
                    case DOWN:
                        setPlaceDirection(direction);
                        break;
                    default:
                        setPlaceDirection(facing.rotateYCCW());
                        break;
                }
                break;
        }
    }

    public void updateFakePlayer(BlockPos pos) {
        if (fakePlayer == null || fakePlayer.get() == null) {
            fakePlayer = Util.createFakePlayer(world);
        }

        if (fakePlayer == null) {
            return;
        }

        FakePlayer player = fakePlayer.get();

        player.rotationYaw = 0;
        player.rotationPitch = 0;
        player.posX = pos.getX();
        player.posY = pos.getY();
        player.posZ = pos.getZ();

        switch (placeDirection) {
            case NORTH:
                player.rotationYaw = 0;
                player.posZ += 2;
                break;
            case SOUTH:
                player.rotationYaw = 180;
                player.posZ -= 2;
                break;
            case UP:
                player.rotationPitch = 90;
                player.posY += 2;
                break;
            case DOWN:
                player.rotationPitch = -90;
                player.posY -= 2;
                break;
            case EAST:
                player.rotationYaw = 90;
                player.posX -= 2;
                break;
            case WEST:
                player.rotationYaw = -90;
                player.posX += 2;
                break;
        }

        switch (placeAngle) {
            case HIGH:
                player.rotationPitch -= 45;
                break;
            case LOW:
                player.rotationPitch += 45;
                break;
        }

        player.prevRotationPitch = player.rotationPitch;
        player.prevRotationYaw = player.rotationYaw;
        player.rotationYawHead = player.rotationYaw;
        player.prevRotationYawHead = player.rotationYawHead;
    }

    public FakePlayer getFakePlayer(BlockPos pos) {
        updateFakePlayer(pos);

        return fakePlayer.get();
    }

    public static class DrawbridgeStats {
        public int extendLength = MechworksConfig.getInstance().drawbridgeExtendLength;
        public float extendDelay = MechworksConfig.getInstance().drawbridgeSpeed;
        public boolean isAdvanced = false;
    }

    public enum Angle {
        HIGH,
        NEUTRAL,
        LOW
    }

    public static class DrawbridgeItemUseContext extends BlockItemUseContext {
        public DrawbridgeItemUseContext(ItemUseContext p_i47813_1_) {
            super(p_i47813_1_);
        }

        @Override
        public BlockPos getPos() {
            return rayTraceResult.getPos();
        }
    }
}
