package slimeknights.tmechworks.blocks.logic;

import com.google.common.base.Predicates;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import slimeknights.mantle.tileentity.TileInventory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class RedstoneMachineLogicBase extends TileInventory implements IDisguisable, ITickable {
    private InventoryBasic disguiseInventory;

    private int redstoneState;
    private boolean isFirstTick = true;

    /**
     * The facing direction of the redstone machine, some machines may choose to completely ignore this attribute
     */
    private EnumFacing facingDirection = EnumFacing.NORTH;

    public RedstoneMachineLogicBase(String name, int inventorySize) {
        this(name, inventorySize, 64);
    }

    public RedstoneMachineLogicBase(String name, int inventorySize, int maxStackSize) {
        super(name, inventorySize, maxStackSize);

        disguiseInventory = new InventoryBasic(name + ".disguise", false, 1);
    }

    public void updateRedstone() {
        if (isFirstTick)
            return;

        int oldPow = redstoneState;

        int sidePow = 0;

        for (EnumFacing face : EnumFacing.values()) {
            int pow = world.getRedstonePower(pos.offset(face), face);

            if (face != getFacingDirection() && pow > 0) {
                if (pow > sidePow) {
                    sidePow = pow;
                }
            }
        }

        int pow = world.getRedstonePower(pos, EnumFacing.DOWN);

        if (pow > sidePow) {
            sidePow = pow;
        }

        redstoneState = sidePow;

        if(pow != oldPow) {
            onRedstoneUpdate();
        }
        onBlockUpdate();

        if (oldPow != redstoneState) {
            sync();
        }
    }

    public void onRedstoneUpdate() {
    }

    public void onBlockUpdate() {}

    public int getRedstoneState() {
        return redstoneState;
    }

    public EnumFacing getFacingDirection() {
        return facingDirection;
    }

    public void setFacingDirection(EnumFacing direction) {
        facingDirection = direction;
        markDirty();
    }

    @Override
    public void update() {
        if (isFirstTick) {
            isFirstTick = false;
            updateRedstone();
            loadData();
            sync();
        }
    }

    public void loadData() {

    }

    @Override
    public ItemStack getDisguiseBlock() {
        return disguiseInventory.getStackInSlot(0);
    }

    @Override
    public void setDisguiseBlock(ItemStack disguise) {
        disguiseInventory.setInventorySlotContents(0, disguise);
    }

    @Override
    public boolean canEditDisguise() {
        return true;
    }

    public NBTTagCompound writeItemData(NBTTagCompound tags) {
        tags.setInteger("InventorySize", getSizeInventory());

        writeInventoryToNBT(tags);

        if (this.hasCustomName()) {
            tags.setString("CustomName", this.inventoryTitle);
        }

        ItemStack disguise = getDisguiseBlock();

        if (disguise != null) {
            NBTTagCompound itemNBT = new NBTTagCompound();

            itemNBT = disguise.writeToNBT(itemNBT);

            tags.setTag("Disguise", itemNBT);
        }

        return tags;
    }

    public void readItemData(NBTTagCompound tags) {
        if (tags.hasKey("Disguise")) {
            NBTTagCompound itemNBT = tags.getCompoundTag("Disguise");

            ItemStack disguise = new ItemStack(itemNBT);

            setDisguiseBlock(disguise);
        }
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound tags) {
        tags = super.writeToNBT(tags);
        tags = writeItemData(tags);

        tags.setInteger("Redstone", redstoneState);
        tags.setInteger("Facing", facingDirection.ordinal());

        return tags;
    }

    @Override
    public void readFromNBT(NBTTagCompound tags) {
        super.readFromNBT(tags);
        readItemData(tags);

        redstoneState = tags.getInteger("Redstone");
        facingDirection = EnumFacing.values()[tags.getInteger("Facing")];
    }

    @Override
    @Nullable
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound tags = new NBTTagCompound();

        writeToNBT(tags);

        return new SPacketUpdateTileEntity(pos, world.getBlockState(pos).getBlock().getMetaFromState(world.getBlockState(pos)), tags);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        NBTTagCompound tags = pkt.getNbtCompound();

        handleUpdateTag(tags);
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public void handleUpdateTag(@Nonnull NBTTagCompound tag) {
        readFromNBT(tag);

        world.markBlockRangeForRenderUpdate(pos, pos);
    }

    public void sync() {
        markDirty();
        world.markBlockRangeForRenderUpdate(pos, pos);

        if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
            SPacketUpdateTileEntity packetUpdateTileEntity = getUpdatePacket();

            if (packetUpdateTileEntity == null) {
                return;
            }

            for (EntityPlayerMP player : world.getPlayers(EntityPlayerMP.class, Predicates.alwaysTrue())) {
                player.connection.sendPacket(packetUpdateTileEntity);
            }
        }
    }

    public ItemStack storeTileData(ItemStack stack) {
        NBTTagCompound nbttagcompound = writeItemData(new NBTTagCompound());

        stack.setTagInfo("BlockEntityTag", nbttagcompound);
        return stack;
    }
}
