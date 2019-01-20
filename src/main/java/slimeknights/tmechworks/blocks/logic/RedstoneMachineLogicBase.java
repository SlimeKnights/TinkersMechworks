package slimeknights.tmechworks.blocks.logic;

import com.google.common.base.Predicates;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import slimeknights.mantle.tileentity.TileInventory;
import slimeknights.tmechworks.integration.IInformationProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public abstract class RedstoneMachineLogicBase extends TileInventory implements IDisguisable, ITickable, IInformationProvider {
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

    /**
     * Updates redstone state
     */
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

    /**
     * Called when the redstone state changes
     */
    public void onRedstoneUpdate() {
    }

    /**
     * Called when redstone state is updated, but the redstone state remains unchanged
     */
    public void onBlockUpdate() {}

    /**
     * @return The redstone power level
     */
    public int getRedstoneState() {
        return redstoneState;
    }

    /**
     * @return The direction the block is facing
     */
    public EnumFacing getFacingDirection() {
        return facingDirection;
    }

    /**
     * Sets the facing direction
     */
    public void setFacingDirection(EnumFacing direction) {
        facingDirection = direction;
        markDirty();
    }

    @Override
    public void update() {
        if (isFirstTick) {
            isFirstTick = false;
            updateRedstone();
            init();
            sync();
        }
    }

    /**
     * Gets called the first tick this tile exists
     */
    public void init() {

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

    /**
     * Writes inventory information
     */
    public NBTTagCompound writeItemData(NBTTagCompound tags) {
        tags.setInteger("InventorySize", getSizeInventory());
        writeInventoryToNBT(tags);

        if(this.hasCustomName()) {
            tags.setString("CustomName", this.inventoryTitle);
        }

        ItemStack disguise = getDisguiseBlock();

        if (!disguise.isEmpty()) {
            NBTTagCompound itemNBT = new NBTTagCompound();

            itemNBT = disguise.writeToNBT(itemNBT);

            tags.setTag("Disguise", itemNBT);
        }

        return tags;
    }

    /**
     * Reads inventory information
     */
    public void readItemData(NBTTagCompound tags) {
        super.readFromNBT(tags);

        if (tags.hasKey("Disguise")) {
            NBTTagCompound itemNBT = tags.getCompoundTag("Disguise");

            ItemStack disguise = new ItemStack(itemNBT);

            setDisguiseBlock(disguise);
        }
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound tags) {
        super.writeToNBT(tags);
        tags = writeItemData(tags);

        tags.setInteger("Redstone", redstoneState);
        tags.setInteger("Facing", facingDirection.ordinal());

        return tags;
    }

    @Override
    public void readFromNBT(NBTTagCompound tags) {
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

    /**
     * Stores information in an itemstack
     */
    public ItemStack storeTileData(ItemStack stack) {
        NBTTagCompound nbttagcompound = writeItemData(new NBTTagCompound());

        stack.setTagInfo("BlockEntityTag", nbttagcompound);

        if(this.hasCustomName()){
            NBTTagCompound name = new NBTTagCompound();
            name.setString("Name", inventoryTitle);

            stack.setTagInfo("display", name);
        }

        return stack;
    }

    @Override
    public void getInformation(@Nonnull List<String> info, InformationType type) {
        if(type != InformationType.BODY)
            return;

        info.add(I18n.format("hud.msg.power") + ": " + getRedstoneState());
    }
}
