package slimeknights.tmechworks.blocks.logic;

import com.google.common.base.Predicates;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import slimeknights.mantle.tileentity.TileInventory;
import slimeknights.tmechworks.library.Util;

public abstract class DrawbridgeLogicBase extends TileInventory implements IDisguisable, ITickable {

  private static final float TICK_TIME = 0.05F;

  private FakePlayer fakePlayer;
  private DrawbridgeStats statistics = new DrawbridgeStats();

  private int redstoneState;
  private int extendState;
  private boolean isExtended;
  private boolean isExtending;
  private float cooldown;

  private boolean isFirstTick = true;

  private EnumFacing facingDirection = EnumFacing.NORTH;

  private long lastWorldTime;

  public DrawbridgeLogicBase(String name, int inventorySize) {
    super(name, inventorySize + 1);
  }

  public void updateRedstone() {
    int oldPow = redstoneState;

    int idPow = worldObj.isBlockIndirectlyGettingPowered(pos);
    int sidePow = 0;

    for(EnumFacing face : EnumFacing.HORIZONTALS) {
      BlockPos miscPos = new BlockPos(pos.getX() + face.getFrontOffsetX(), pos.getY() + face
          .getFrontOffsetY(), pos.getZ() + face.getFrontOffsetZ());
      IBlockState miscState = worldObj.getBlockState(miscPos);

      if(!miscState.canProvidePower()) {
        continue;
      }

      int pow = miscState.getStrongPower(worldObj, miscPos, face.getOpposite());

      if(pow > sidePow) {
        sidePow = pow;
      }
    }

    redstoneState = idPow > sidePow ? idPow : sidePow;

    updateExtension();

    if(oldPow != redstoneState) {
      markDirty();

      for(EntityPlayerMP player : worldObj.getPlayers(EntityPlayerMP.class, Predicates.alwaysTrue())) {
        player.connection.sendPacket(getUpdatePacket());
      }

    }
  }

  public void updateExtension() {
    if(isExtended && redstoneState <= 0) {
      isExtended = false;
      isExtending = true;
    } else if(!isExtended && redstoneState > 0) {
      isExtended = true;
      isExtending = true;
    }
  }

  public void playExtendSound() {
    worldObj.playSound(null, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos
        .getZ() + 0.5D, SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, 0.25F, worldObj.rand
                                                                                          .nextFloat() * 0.25F + 0.6F);
  }

  public void playRetractSound() {
    worldObj.playSound(null, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos
        .getZ() + 0.5D, SoundEvents.BLOCK_PISTON_CONTRACT, SoundCategory.BLOCKS, 0.25F, worldObj.rand
                                                                                            .nextFloat() * 0.15F + 0.6F);
  }

  public int getRedstoneState() {
    return redstoneState;
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

  public EnumFacing getFacingDirection() {
    return facingDirection;
  }

  public void setFacingDirection(EnumFacing direction) {
    facingDirection = direction;
  }

  @Override
  public void onLoad() {
    super.onLoad();

    fakePlayer = Util.createFakePlayer(worldObj);

    setupStatistics(statistics);

    lastWorldTime = worldObj.getWorldTime();
  }

  @Override
  public void update() {
    if(isFirstTick) {
      updateRedstone();
      isFirstTick = false;
    }

    if(isExtending) {
      if(cooldown > 0) {
        cooldown -= (worldObj.getTotalWorldTime() - lastWorldTime) * TICK_TIME;
      } else if(isExtended) {
        if(extendState == statistics.extendLength) {
          isExtending = false;
        } else if(extendNext()) {
          extendState++;
          cooldown = statistics.extendDelay;
          playExtendSound();
        }
      } else {
        if(extendState <= 0) {
          isExtending = false;
        } else if(retractNext()) {
          extendState--;
          cooldown = statistics.extendDelay;
          playRetractSound();
        }
      }
    }

    lastWorldTime = worldObj.getTotalWorldTime();
  }

  @Override
  public void invalidate() {
    super.invalidate();

    if(fakePlayer != null) {
      worldObj.removeEntity(fakePlayer);
      fakePlayer = null;
    }
  }

  @Override
  public void validate() {
    super.validate();

    if(fakePlayer == null) {
      fakePlayer = Util.createFakePlayer(worldObj);
    }
  }

  @Override
  public ItemStack getDisguiseBlock() {
    return getStackInSlot(getSizeInventory() - 1);
  }

  @Override
  public void setDisguiseBlock(ItemStack disguise) {
    setInventorySlotContents(getSizeInventory() - 1, disguise);
  }

  /* NBT */
  @Override
  public void readFromNBT(NBTTagCompound tags) {
    super.readFromNBT(tags);

    extendState = tags.getInteger("ExtendState");
    isExtended = tags.getBoolean("IsExtended");
    isExtending = tags.getBoolean("IsExtending");
    cooldown = tags.getFloat("Cooldown");
    facingDirection = EnumFacing.values()[tags.getInteger("Facing")];
  }

  @Nonnull
  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound tags) {
    NBTTagCompound data = super.writeToNBT(tags);

    data.setInteger("ExtendState", extendState);
    data.setBoolean("IsExtended", isExtended);
    data.setBoolean("IsExtending", isExtending);
    data.setFloat("Cooldown", cooldown);
    data.setInteger("Facing", facingDirection.ordinal());

    return data;
  }

  @Override
  public boolean shouldRefresh(World world, BlockPos pos, @Nonnull IBlockState oldState, @Nonnull IBlockState newSate) {
    return oldState.getBlock() != newSate.getBlock();
  }

  @Override
  @Nullable
  public SPacketUpdateTileEntity getUpdatePacket() {
    NBTTagCompound tags = new NBTTagCompound();

    tags = writeToNBT(tags);

    SPacketUpdateTileEntity packet = new SPacketUpdateTileEntity(pos, worldObj.getBlockState(pos).getBlock()
                                                                              .getMetaFromState(worldObj
                                                                                                    .getBlockState(pos)), tags);

    return packet;
  }

  @Override
  public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
    NBTTagCompound tags = pkt.getNbtCompound();

    readFromNBT(tags);

    updateRedstone();
  }

  public abstract void setupStatistics(DrawbridgeStats ds);

  public abstract boolean extendNext();

  public abstract boolean retractNext();

  final class DrawbridgeStats {

    public int extendLength = 16;
    public float extendDelay = 0.5F;
  }
}
