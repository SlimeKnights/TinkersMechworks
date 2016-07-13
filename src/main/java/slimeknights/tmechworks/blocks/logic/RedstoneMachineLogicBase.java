package slimeknights.tmechworks.blocks.logic;

import com.google.common.base.Predicates;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import slimeknights.mantle.tileentity.TileInventory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class RedstoneMachineLogicBase extends TileInventory implements IDisguisable, ITickable
{
    private InventoryBasic disguiseInventory;

    private int redstoneState;
    private boolean isFirstTick = true;

    /**
     * The facing direction of the redstone machine, some machines may choose to completely ignore this attribute
     */
    private EnumFacing facingDirection = EnumFacing.NORTH;

    public RedstoneMachineLogicBase (String name, int inventorySize)
    {
        this(name, inventorySize, 64);
    }

    public RedstoneMachineLogicBase (String name, int inventorySize, int maxStackSize)
    {
        super(name, inventorySize, maxStackSize);

        disguiseInventory = new InventoryBasic(name + ".disguise", false, 1);
    }

    public void updateRedstone ()
    {
        int oldPow = redstoneState;

        int idPow = worldObj.isBlockIndirectlyGettingPowered(pos);
        int sidePow = 0;

        for (EnumFacing face : EnumFacing.HORIZONTALS)
        {
            BlockPos miscPos = new BlockPos(pos.getX() + face.getFrontOffsetX(), pos.getY() + face.getFrontOffsetY(), pos.getZ() + face.getFrontOffsetZ());
            IBlockState miscState = worldObj.getBlockState(miscPos);

            if (!miscState.canProvidePower())
            {
                continue;
            }

            int pow = miscState.getStrongPower(worldObj, miscPos, face.getOpposite());

            if (pow > sidePow)
            {
                sidePow = pow;
            }
        }

        redstoneState = idPow > sidePow ? idPow : sidePow;

        onBlockUpdate();

        if (oldPow != redstoneState)
        {
            sync();
        }
    }

    public void onBlockUpdate ()
    {
    }

    public int getRedstoneState ()
    {
        return redstoneState;
    }

    public EnumFacing getFacingDirection ()
    {
        return facingDirection;
    }

    public void setFacingDirection (EnumFacing direction)
    {
        facingDirection = direction;
    }

    @Override public void update ()
    {
        if (isFirstTick)
        {
            updateRedstone();
            loadData();
            sync();
            isFirstTick = false;
        }
    }

    public void loadData ()
    {

    }

    @Override public ItemStack getDisguiseBlock ()
    {
        return disguiseInventory.getStackInSlot(0);
    }

    @Override public void setDisguiseBlock (ItemStack disguise)
    {
        disguiseInventory.setInventorySlotContents(0, disguise);
    }

    @Override public boolean canEditDisguise ()
    {
        return true;
    }

    @Override @Nonnull public NBTTagCompound writeToNBT (NBTTagCompound tags)
    {
        NBTTagCompound data = super.writeToNBT(tags);

        ItemStack disguise = getDisguiseBlock();

        if (disguise != null)
        {
            NBTTagCompound itemNBT = new NBTTagCompound();

            itemNBT = disguise.writeToNBT(itemNBT);

            data.setTag("Disguise", itemNBT);
        }

        data.setInteger("Redstone", redstoneState);
        data.setInteger("Facing", facingDirection.ordinal());

        return data;
    }

    @Override public void readFromNBT (NBTTagCompound tags)
    {
        super.readFromNBT(tags);

        if (tags.hasKey("Disguise"))
        {
            NBTTagCompound itemNBT = tags.getCompoundTag("Disguise");

            ItemStack disguise = ItemStack.loadItemStackFromNBT(itemNBT);

            setDisguiseBlock(disguise);
        }

        redstoneState = tags.getInteger("Redstone");
        facingDirection = EnumFacing.values()[tags.getInteger("Facing")];
    }

    @Override @Nullable public SPacketUpdateTileEntity getUpdatePacket ()
    {
        NBTTagCompound tags = new NBTTagCompound();

        writeToNBT(tags);

        return new SPacketUpdateTileEntity(pos, worldObj.getBlockState(pos).getBlock().getMetaFromState(worldObj.getBlockState(pos)), tags);
    }

    @Override public void onDataPacket (NetworkManager net, SPacketUpdateTileEntity pkt)
    {
        NBTTagCompound tags = pkt.getNbtCompound();

        handleUpdateTag(tags);
    }

    @Override public NBTTagCompound getUpdateTag ()
    {
        return writeToNBT(new NBTTagCompound());
    }

    @Override public void handleUpdateTag (@Nonnull NBTTagCompound tag)
    {
        readFromNBT(tag);

        worldObj.markBlockRangeForRenderUpdate(pos, pos);
    }

    public void sync ()
    {
        markDirty();
        worldObj.markBlockRangeForRenderUpdate(pos, pos);

        if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
        {
            SPacketUpdateTileEntity packetUpdateTileEntity = getUpdatePacket();

            if (packetUpdateTileEntity == null)
            {
                return;
            }

            for (EntityPlayerMP player : worldObj.getPlayers(EntityPlayerMP.class, Predicates.alwaysTrue()))
            {
                player.connection.sendPacket(packetUpdateTileEntity);
            }
        }
    }
}
