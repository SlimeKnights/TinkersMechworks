package tmechworks.blocks.logic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import mantle.blocks.BlockUtils;
import mantle.blocks.abstracts.InventoryLogic;
import mantle.blocks.iface.IActiveLogic;
import mantle.blocks.iface.IFacingLogic;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Facing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import tmechworks.inventory.AdvancedDrawbridgeContainer;
import tmechworks.lib.TMechworksRegistry;
import tmechworks.lib.blocks.IDrawbridgeLogicBase;
import tmechworks.lib.player.FakePlayerLogic;

import com.mojang.authlib.GameProfile;

public class AdvancedDrawbridgeLogic extends InventoryLogic implements IFacingLogic, IActiveLogic, IDrawbridgeLogicBase
{
    boolean active;
    boolean working;
    int ticks;
    public int selSlot = 0;
    byte extension;
    byte direction;
    byte placementDirection = 4;
    FakePlayerLogic fakePlayer;

    ItemStack[] bufferStacks = new ItemStack[getSizeInventory()];

    public InvCamo camoInventory = new InvCamo();

    public AdvancedDrawbridgeLogic()
    {
        super(16);
    }

    @Override
    public void setWorldObj (World par1World)
    {
        this.worldObj = par1World;
    }

    // Super awesome hack of post initialization hashmap-adding nonsense! 
    // Completely necessary due to the way players load chunks, including fake ones.
    private void initFakePlayer ()
    {
        if (fakePlayer == null && !isInvalid())
            fakePlayer = new FakePlayerLogic(new GameProfile(null, "Player.Drawbridge"), this);
    }

    //This gets called too early. Adding the fake player here creates multiple copies of the TileEntity and causes havoc!
    /*@Override
    public void validate ()
    {
        this.tileEntityInvalid = false;
        fakePlayer = new FakePlayerLogic("Player.Drawbridge", this);
    }*/

    @Override
    public boolean getActive ()
    {
        return active;
    }

    @Override
    public void setActive (boolean flag)
    {
        active = flag;
        working = true;
    }

    @Override
    public byte getRenderDirection ()
    {
        return direction;
    }

    @Override
    public ForgeDirection getForgeDirection ()
    {
        return ForgeDirection.VALID_DIRECTIONS[direction];
    }

    @Override
    public void setDirection (int side)
    {
    }

    public boolean canDropInventorySlot (int slot)
    {
        return false;
    }

    @Override
    public void setDirection (float yaw, float pitch, EntityLivingBase player)
    {
        if (pitch > 45)
        {
            direction = 1;
        }
        else if (pitch < -45)
        {
            direction = 0;
        }
        else
        {
            int facing = MathHelper.floor_double((double) (yaw / 360) + 0.5D) & 3;
            switch (facing)
            {
            case 0:
                direction = 2;
                break;

            case 1:
                direction = 5;
                break;

            case 2:
                direction = 3;
                break;

            case 3:
                direction = 4;
                break;
            }
        }
    }

    /*
     * 0 = Up 1 = Right 2 = Down 3 = Left 4 = Center, neutral
     */
    public void setPlacementDirection (byte keycode)
    {
        placementDirection = keycode;
        if (!worldObj.isRemote)
        {
            initFakePlayer();
            setFakePlayerRotation();
        }

    }

    private void setFakePlayerRotation ()
    {
        if (placementDirection == 4)
        {
            fakePlayer.rotationYaw = 0;
            fakePlayer.rotationPitch = 0;
        }
        else if (this.direction == 0 || this.direction == 1)
        {
            switch (placementDirection)
            {
            case 0:
                fakePlayer.rotationYaw = 0;
                break;
            case 1:
                fakePlayer.rotationYaw = 90;
                break;
            case 2:
                fakePlayer.rotationYaw = 180;
                break;
            case 3:
                fakePlayer.rotationYaw = 270;
                break;
            }

            if (this.direction == 0)
                fakePlayer.rotationPitch = -90;
            else
                fakePlayer.rotationPitch = 90;
        }
        else
        {
            if (placementDirection == 0) // Forward
            {
                fakePlayer.rotationYaw = mapDirection() * 90;

                if (placementDirection == 0)
                    fakePlayer.rotationPitch = 90;
                else
                    fakePlayer.rotationPitch = -90;
            }
            else if (placementDirection == 2) // Backward
            {
                int face = mapDirection() + 2;
                if (face > 3)
                    face -= 4;
                fakePlayer.rotationYaw = face * 90;

                if (placementDirection == 0)
                    fakePlayer.rotationPitch = 90;
                else
                    fakePlayer.rotationPitch = -90;
            }
            else
            {
                fakePlayer.rotationPitch = 0;

                int facing = mapDirection();
                if (placementDirection == 1)
                    facing += 1;
                else
                    facing -= 1;

                if (facing >= 4)
                    facing = 0;
                if (facing < 0)
                    facing = 3;

                fakePlayer.rotationYaw = facing * 90;
            }
        }
    }

    int mapDirection ()
    {
        if (this.direction == 2) // North
            return 0;
        if (this.direction == 5) // East
            return 1;
        if (this.direction == 3) // South
            return 2;

        return 3; // West
    }

    public byte getPlacementDirection ()
    {
        return placementDirection;
    }

    @Override
    public ItemStack getStackInSlot (int slot)
    {
        return slot < inventory.length ? inventory[slot] : null;
    }

    public ItemStack getStackInBufferSlot (int slot)
    {
        return slot < bufferStacks.length ? bufferStacks[slot] : null;
    }

    public void setBufferSlotContents (int slot, ItemStack itemstack)
    {
        if (slot < bufferStacks.length)
        {
            bufferStacks[slot] = itemstack;
        }
        else
        {
            return;
        }
        if (itemstack != null && itemstack.stackSize > getInventoryStackLimit())
        {
            itemstack.stackSize = getInventoryStackLimit();
        }
    }

    @Override
    public Container getGuiContainer (InventoryPlayer inventoryplayer, World world, int x, int y, int z)
    {
        return new AdvancedDrawbridgeContainer(inventoryplayer, this);
    }

    @Override
    protected String getDefaultName ()
    {
        return "tinker.drawbridge";
    }

    @Override
    public ItemStack decrStackSize (int slot, int quantity)
    {
        ItemStack stack = super.decrStackSize(slot, quantity);
        if (slot == 1)
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        return stack;
    }

    public void updateEntity ()
    {
        if (working && !isInvalid() && !worldObj.isRemote)
        {
            ticks++;
            if (ticks == 5)
            {
                ticks = 0;
                if (active) // Placement
                {
                    if (getStackInSlot(extension) != null && getStackInSlot(extension).stackSize > 0 && extension < 15)
                    {
                        extension++;
                        int xPos = xCoord;
                        int yPos = yCoord;
                        int zPos = zCoord;

                        switch (direction)
                        {
                        case 0:
                            yPos -= extension;
                            break;
                        case 1:
                            yPos += extension;
                            break;
                        case 2:
                            zPos -= extension;
                            break;
                        case 3:
                            zPos += extension;
                            break;
                        case 4:
                            xPos -= extension;
                            break;
                        case 5:
                            xPos += extension;
                            break;
                        }

                        if (xPos < -30000000 || zPos < -30000000 || xPos >= 30000000 || zPos >= 30000000 || yPos < 0 || yPos >= 256)
                        {
                            extension--;
                            working = false;
                            return;
                        }

                        Block block = worldObj.getBlock(xPos, yPos, zPos);
                        if (block == null || block.isAir(worldObj, xPos, yPos, zPos) || block.canPlaceBlockAt(worldObj, xPos, yPos, zPos))
                        {
                            // tryExtend(worldObj, xPos, yPos, zPos, direction);
                            initFakePlayer();
                            setFakePlayerRotation();
                            Item blockToItem = getStackInBufferSlot(extension - 1) != null && getStackInBufferSlot(extension - 1).getItem() != null ? TMechworksRegistry.blockToItemMapping.get(Block
                                    .getBlockFromItem(getStackInBufferSlot(extension - 1).getItem())) : Items.stick;
                            if (blockToItem == Item.getItemFromBlock(Blocks.air))
                            {
                                if (getStackInSlot(extension - 1) == null || Block.getBlockFromItem(getStackInSlot(extension - 1).getItem()) == null)
                                    return;
                                ItemStack placeStack = getStackInBufferSlot(extension - 1);
                                if (placeStack != null)
                                {
                                    Block placeBlock = Block.getBlockFromItem(placeStack.getItem());
                                    placeBlockAt(getStackInSlot(extension - 1), fakePlayer, worldObj, xPos, yPos, zPos, direction, 0, 0, 0, getStackInSlot(extension - 1).getItemDamage(), placeBlock);
                                }
                            }
                            else
                            {
                                Block placeBlock = Block.getBlockFromItem(blockToItem);
                                placeBlockAt(getStackInSlot(extension - 1), fakePlayer, worldObj, xPos, yPos, zPos, direction, 0, 0, 0, getStackInSlot(extension - 1).getItemDamage(), placeBlock);
                            }
                            worldObj.playSoundEffect((double) xPos + 0.5D, (double) yPos + 0.5D, (double) zPos + 0.5D, "tile.piston.out", 0.25F, worldObj.rand.nextFloat() * 0.25F + 0.6F);
                            List pushedObjects = new ArrayList();

                            Block axis = worldObj.getBlock(xPos, yPos, zPos);
                            AxisAlignedBB axisalignedbb = axis != null ? axis.getCollisionBoundingBoxFromPool(worldObj, xPos, yPos, zPos) : null;

                            if (axisalignedbb != null)
                            {
                                List list = worldObj.getEntitiesWithinAABBExcludingEntity((Entity) null, axisalignedbb);
                                if (!list.isEmpty())
                                {
                                    pushedObjects.addAll(list);
                                    Iterator iterator = pushedObjects.iterator();

                                    while (iterator.hasNext())
                                    {
                                        Entity entity = (Entity) iterator.next();
                                        entity.moveEntity(Facing.offsetsXForSide[this.direction], Facing.offsetsYForSide[this.direction], Facing.offsetsZForSide[this.direction]);
                                    }

                                    pushedObjects.clear();
                                }
                            }
                            decrStackSize(extension - 1, 1);
                        }
                        else
                        {
                            extension--;
                            working = false;
                        }
                    }
                    else
                    {
                        working = false;
                    }
                }
                else
                // Retraction
                {
                    if ((getStackInSlot(extension) == null || getStackInSlot(extension).stackSize < getStackInSlot(extension).getMaxStackSize()) && extension > 0)
                    {
                        int xPos = xCoord;
                        int yPos = yCoord;
                        int zPos = zCoord;

                        switch (direction)
                        {
                        case 0:
                            yPos -= extension;
                            break;
                        case 1:
                            yPos += extension;
                            break;
                        case 2:
                            zPos -= extension;
                            break;
                        case 3:
                            zPos += extension;
                            break;
                        case 4:
                            xPos -= extension;
                            break;
                        case 5:
                            xPos += extension;
                            break;
                        }

                        Block block = worldObj.getBlock(xPos, yPos, zPos);
                        if (block != null)
                        {
                            int meta = worldObj.getBlockMetadata(xPos, yPos, zPos);
                            if (getStackInBufferSlot(extension - 1) != null && validBlock(extension - 1, block) && validMetadata(extension - 1, block, meta) && validDrawbridge(xPos, yPos, zPos))
                            {
                                worldObj.playSoundEffect((double) xPos + 0.5D, (double) yPos + 0.5D, (double) zPos + 0.5D, "tile.piston.in", 0.25F, worldObj.rand.nextFloat() * 0.15F + 0.6F);
                                if (worldObj.setBlock(xPos, yPos, zPos, Blocks.air))
                                    if (getStackInSlot(extension - 1) == null)
                                    {
                                        setInventorySlotContents(extension - 1, getStackInBufferSlot(extension - 1).copy());
                                    }
                                    else
                                    {
                                        getStackInSlot(extension - 1).stackSize++;
                                    }
                            }
                            else
                            {
                                working = false;
                            }
                        }
                        extension--;
                    }
                    else
                    {
                        working = false;
                    }
                }
            }
        }
    }

    /**
     * Called to actually place the block, after the location is determined and
     * all permission checks have been made. Copied from ItemBlock
     * 
     * @param stack
     *            The item stack that was used to place the block. This can be
     *            changed inside the method.
     * @param player
     *            The player who is placing the block. Can be null if the block
     *            is not being placed by a player.
     * @param side
     *            The side the player (or machine) right-clicked on.
     */
    public boolean placeBlockAt (ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata, Block block)
    {
        Item blockItem = stack.getItem();
        metadata = blockItem.getMetadata(metadata);
        metadata = block.onBlockPlaced(world, x, y, z, side, hitX, hitY, hitZ, metadata);

        if (blockItem instanceof ItemBlock)
        {
            return ((ItemBlock) blockItem).placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata);
        }

        if (!world.setBlock(x, y, z, block, metadata, 3))
        {
            return false;
        }

        if (world.getBlock(x, y, z) == block)
        {
            block.onBlockPlacedBy(world, x, y, z, player, stack);
            block.onPostBlockPlaced(world, x, y, z, metadata);
        }

        return true;
    }

    boolean validDrawbridge (int x, int y, int z)
    {
        TileEntity te = worldObj.getTileEntity(x, y, z);
        if (te instanceof IDrawbridgeLogicBase && ((IDrawbridgeLogicBase) te).hasExtended())
            return false;

        return true;
    }

    boolean validBlock (int slot, Block block)
    {
        Block type = TMechworksRegistry.interchangableBlockMapping.get(new ItemStack(block).getItem());
        if (type != Blocks.air)
        {
            if (type == Block.getBlockFromItem(getStackInBufferSlot(slot).getItem()))
                return true;
        }
        Item blockToItem = TMechworksRegistry.blockToItemMapping.get(new ItemStack(block).getItem());
        if (blockToItem != Item.getItemFromBlock(Blocks.air))
        {
            if (blockToItem == getStackInBufferSlot(slot).getItem())
                return true;
        }
        return new ItemStack(block).getItem() == getStackInBufferSlot(slot).getItem();
    }

    boolean validMetadata (int slot, Block block, int metadata)
    {
        /**int type = TMechworksRegistry.drawbridgeState.get(block).getTypeID();
        if (type == 0)
        {
            return metadata == getStackInBufferSlot(slot).getItemDamage();
        }
        if (type == 1)
        {
            return true;
        }
        if (type == 2)
        {
            return false;
        }
        if (type == 3)
        {
            return true; // TODO: rotational metadata, probably not needed
                         // anymore
        }
        if (type == 4)
        {
            return true;
        }
        if (type == 5)
        {
            return metadata == getStackInBufferSlot(slot).getItemDamage();
        }*/
        return true;
    }

    @Override
    public void readFromNBT (NBTTagCompound tags)
    {
        super.readFromNBT(tags);
        active = tags.getBoolean("Active");
        working = tags.getBoolean("Working");
        extension = tags.getByte("Extension");

        NBTTagCompound camoTag = (NBTTagCompound) tags.getTag("Camo");
        if (camoTag != null)
        {
            camoInventory.setInventorySlotContents(0, ItemStack.loadItemStackFromNBT(camoTag));
        }

        readBufferFromNBT(tags);
        readCustomNBT(tags);
    }

    @Override
    public void writeToNBT (NBTTagCompound tags)
    {
        super.writeToNBT(tags);
        tags.setBoolean("Active", active);
        tags.setBoolean("Working", working);
        tags.setByte("Extension", extension);

        if (camoInventory.getStackInSlot(0) != null)
        {
            NBTTagCompound camoTag = new NBTTagCompound();
            camoInventory.getStackInSlot(0).writeToNBT(camoTag);
            tags.setTag("Camo", camoTag);
        }

        writeBufferToNBT(tags);
        writeCustomNBT(tags);
    }

    public void readBufferFromNBT (NBTTagCompound tags)
    {
        NBTTagList nbttaglist = tags.getTagList("Buffer", 10);
        bufferStacks = new ItemStack[getSizeInventory()];
        //		bufferStacks.ensureCapacity(nbttaglist.tagCount() > getSizeInventory() ? getSizeInventory() : nbttaglist.tagCount());
        for (int iter = 0; iter < nbttaglist.tagCount(); iter++)
        {
            NBTTagCompound tagList = (NBTTagCompound) nbttaglist.getCompoundTagAt(iter);
            byte slotID = tagList.getByte("Slot");
            if (slotID >= 0 && slotID < bufferStacks.length)
            {
                setBufferSlotContents(slotID, ItemStack.loadItemStackFromNBT(tagList));
            }
        }
    }

    public void writeBufferToNBT (NBTTagCompound tags)
    {
        NBTTagList nbttaglist = new NBTTagList();
        for (int iter = 0; iter < bufferStacks.length; iter++)
        {
            if (getStackInBufferSlot(iter) != null)
            {
                NBTTagCompound tagList = new NBTTagCompound();
                tagList.setByte("Slot", (byte) iter);
                getStackInBufferSlot(iter).writeToNBT(tagList);
                nbttaglist.appendTag(tagList);
            }
        }

        tags.setTag("Buffer", nbttaglist);
    }

    public void readCustomNBT (NBTTagCompound tags)
    {
        direction = tags.getByte("Direction");
        placementDirection = tags.getByte("Placement");
    }

    public void writeCustomNBT (NBTTagCompound tags)
    {
        tags.setByte("Direction", direction);
        tags.setByte("Placement", placementDirection);
    }

    /* Packets */
    @Override
    public Packet getDescriptionPacket ()
    {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);
    }

    @Override
    public void onDataPacket (NetworkManager net, S35PacketUpdateTileEntity packet)
    {
        readFromNBT(packet.func_148857_g());
        worldObj.func_147479_m(xCoord, yCoord, zCoord);
    }

    public boolean hasExtended ()
    {
        return extension != 0;
    }

    @Override
    public void markDirty ()
    {
        super.markDirty();
        for (int i = 0; i < getSizeInventory(); i++)
        {
            if (getStackInSlot(i) != null)
            {
                setBufferSlotContents(i, getStackInSlot(i).copy());
                getStackInBufferSlot(i).stackSize = 1;
            }
        }
        this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    //	@Override
    //	public int getMaxSize() {
    //		return 16;
    //	}

    @Override
    public int getInventoryStackLimit ()
    {
        return 1;
    }

    public class InvCamo extends InventoryBasic
    {

        private InvCamo()
        {
            super("camoSlot", false, 1);
        }

        public ItemStack getCamoStack ()
        {
            return this.getStackInSlot(0);
        }

        @Override
        public void setInventorySlotContents (int slot, ItemStack content)
        {
            super.setInventorySlotContents(slot, content);
            if (slot == 0 && worldObj != null)
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }

        @Override
        public void markDirty ()
        {
            super.markDirty();
            if (worldObj != null)
            {
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            }
        }

        @Override
        public int getInventoryStackLimit ()
        {
            return 1;
        }
    }

    @Override
    public String getInventoryName ()
    {
        return getDefaultName();
    }

    @Override
    public boolean hasCustomInventoryName ()
    {
        return true;
    }

    @Override
    public void closeInventory ()
    {
    }

    @Override
    public void openInventory ()
    {
    }

    @Override
    public boolean isItemValidForSlot (int slot, ItemStack itemstack)
    {
        if (itemstack == null || !(itemstack.getItem() instanceof ItemBlock))
        {
            return false;
        }

        if (TMechworksRegistry.isItemDBBlacklisted((ItemBlock) itemstack.getItem()))
        {
            return false;
        }

        return super.isItemValidForSlot(slot, itemstack);
    }
}