package tmechworks.blocks.logic;

import mantle.blocks.BlockUtils;
import mantle.blocks.abstracts.InventoryLogic;
import mantle.blocks.iface.*;
import mantle.common.ComparisonHelper;
import mantle.world.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.network.*;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import tconstruct.library.TConstructRegistry;
import tmechworks.inventory.AdvancedDrawbridgeContainer;
import tmechworks.lib.blocks.IDrawbridgeLogicBase;
import tmechworks.lib.player.FakePlayerLogic;

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
    public void setworldObj (World par1World)
    {
        this.field_145850_b = par1World;
        if (!par1World.isRemote)
            fakePlayer = new FakePlayerLogic(field_145850_b, "Player.Drawbridge", this);
    }

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
        if (!field_145850_b.isRemote)
        {
            if (keycode == 4)
            {
                fakePlayer.rotationYaw = 0;
                fakePlayer.rotationPitch = 0;
            }
            else if (this.direction == 0 || this.direction == 1)
            {
                switch (keycode)
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
                if (keycode == 0) // Forward
                {
                    fakePlayer.rotationYaw = mapDirection() * 90;

                    if (keycode == 0)
                        fakePlayer.rotationPitch = 90;
                    else
                        fakePlayer.rotationPitch = -90;
                }
                else if (keycode == 2) // Backward
                {
                    int face = mapDirection() + 2;
                    if (face > 3)
                        face -= 4;
                    fakePlayer.rotationYaw = face * 90;

                    if (keycode == 0)
                        fakePlayer.rotationPitch = 90;
                    else
                        fakePlayer.rotationPitch = -90;
                }
                else
                {
                    fakePlayer.rotationPitch = 0;

                    int facing = mapDirection();
                    if (keycode == 1)
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
        placementDirection = keycode;
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
            field_145850_b.func_147471_g(field_145851_c, field_145848_d, field_145849_e);
        return stack;
    }

    public void updateEntity ()
    {
        if (working)
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
                        int xPos = field_145851_c;
                        int yPos = field_145848_d;
                        int zPos = field_145849_e;

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

                        Block block = field_145850_b.func_147439_a(xPos, yPos, zPos);
                        if (block == null || block.isAirBlock(field_145850_b, xPos, yPos, zPos) || block.isBlockReplaceable(field_145850_b, xPos, yPos, zPos))
                        {
                            // tryExtend(field_145850_b, xPos, yPos, zPos, direction);
                            int blockToItem = getStackInBufferSlot(extension - 1) != null ? TConstructRegistry.blockToItemMapping[getStackInBufferSlot(extension - 1).getItem()] : 0;
                            if (blockToItem == 0)
                            {
                                if (getStackInSlot(extension - 1) == null || getStackInSlot(extension - 1).itemID >= 4096 || BlockUtils.getBlockFromItem(getStackInSlot(extension - 1).getItem()) == null)
                                    return;
                                Block placeBlock = BlockUtils.getBlockFromItem(getStackInBufferSlot(extension - 1).getItem());
                                placeBlockAt(getStackInSlot(extension - 1), fakePlayer, field_145850_b, xPos, yPos, zPos, direction, 0, 0, 0, getStackInSlot(extension - 1).getItemDamage(), placeBlock);
                            }
                            else
                            {
                                Block placeBlock = Block.blocksList[blockToItem];
                                placeBlockAt(getStackInSlot(extension - 1), fakePlayer, field_145850_b, xPos, yPos, zPos, direction, 0, 0, 0, getStackInSlot(extension - 1).getItemDamage(), placeBlock);
                            }
                            field_145850_b.playSoundEffect((double) xPos + 0.5D, (double) yPos + 0.5D, (double) zPos + 0.5D, "tile.piston.out", 0.25F, field_145850_b.rand.nextFloat() * 0.25F + 0.6F);
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
                        int xPos = field_145851_c;
                        int yPos = field_145848_d;
                        int zPos = field_145849_e;

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

                        Block block = field_145850_b.getBlock(xPos, yPos, zPos);
                        if (block != null)
                        {
                            int meta = field_145850_b.getBlockMetadata(xPos, yPos, zPos);
                            if (getStackInBufferSlot(extension - 1) != null && validBlock(extension - 1, block) && validMetadata(extension - 1, block, meta) && validDrawbridge(xPos, yPos, zPos))
                            {
                                field_145850_b.playSoundEffect((double) xPos + 0.5D, (double) yPos + 0.5D, (double) zPos + 0.5D, "tile.piston.in", 0.25F, field_145850_b.rand.nextFloat() * 0.15F + 0.6F);
                                if (WorldHelper.setBlockToAirBool(field_145850_b, xPos, yPos, zPos))
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
        if (!world.func_147465_d(x, y, z, block, metadata, 3))
        {
            return false;
        }

        if (world.func_147439_a(x, y, z) == block)
        {
            block.onBlockPlacedBy(world, x, y, z, player, stack);
            block.onPostBlockPlaced(world, x, y, z, metadata);
        }

        return true;
    }

    boolean validDrawbridge (int x, int y, int z)
    {
        TileEntity te = field_145850_b.func_147438_o(x, y, z);
        if (te instanceof IDrawbridgeLogicBase && ((IDrawbridgeLogicBase) te).hasExtended())
            return false;

        return true;
    }

    boolean validBlock (int slot, Block block)
    {
        ItemStack type = TConstructRegistry.interchangableBlockMapping[block];
        if (type != null)
        {
            if (type == getStackInBufferSlot(slot))
                return true;
        }
        Item blockToItem = TConstructRegistry.blockToItemMapping[block];
        if (blockToItem != null)
        {
            if (blockToItem == getStackInBufferSlot(slot).getItem())
                return true;
        }
        return ComparisonHelper.areEquivalent(getStackInBufferSlot(slot).getItem(),block);
    }

    boolean validMetadata (int slot, Block block, int metadata)
    {
        int type = TConstructRegistry.drawbridgeState[block];
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
        }
        return false;
    }

    @Override
    public void func_145839_a (NBTTagCompound tags)
    {
        super.func_145839_a(tags);
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
    public void func_145841_b (NBTTagCompound tags)
    {
        super.func_145841_b(tags);
        tags.setBoolean("Active", active);
        tags.setBoolean("Working", working);
        tags.setByte("Extension", extension);

        if (camoInventory.getStackInSlot(0) != null)
        {
            NBTTagCompound camoTag = new NBTTagCompound();
            camoInventory.getStackInSlot(0).func_145839_a(camoTag);
            tags.setTag("Camo", camoTag);
        }

        writeBufferToNBT(tags);
        writeCustomNBT(tags);
    }

    public void readBufferFromNBT (NBTTagCompound tags)
    {
        NBTTagList nbttaglist = tags.getTagList("Buffer");
        bufferStacks = new ItemStack[getSizeInventory()];
        //		bufferStacks.ensureCapacity(nbttaglist.tagCount() > getSizeInventory() ? getSizeInventory() : nbttaglist.tagCount());
        for (int iter = 0; iter < nbttaglist.tagCount(); iter++)
        {
            NBTTagCompound tagList = (NBTTagCompound) nbttaglist.tagAt(iter);
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
                getStackInBufferSlot(iter).func_145839_a(tagList);
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
    public Packet func_145844_m ()
    {
        NBTTagCompound tag = new NBTTagCompound();
        func_145841_b(tag);
        return new S35PacketUpdateTileEntity(field_145851_c, field_145848_d, field_145849_e, 1, tag);
    }

    @Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet)
    {
        func_145839_a(packet.func_148857_g());
        field_145850_b.func_147479_m(field_145851_c, field_145848_d, field_145849_e);
    }

    public boolean hasExtended ()
    {
        return extension != 0;
    }

    @Override
    public void onInventoryChanged ()
    {
        super.onInventoryChanged();
        for (int i = 0; i < getSizeInventory(); i++)
        {
            if (getStackInSlot(i) != null)
            {
                setBufferSlotContents(i, getStackInSlot(i).copy());
                getStackInBufferSlot(i).stackSize = 1;
            }
        }
        this.field_145850_b.func_147471_g(field_145851_c, field_145848_d, field_145849_e);
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
            if (slot == 0 && field_145850_b != null)
                field_145850_b.func_147471_g(field_145851_c, field_145848_d, field_145849_e);
        }

        @Override
        public void onInventoryChanged ()
        {
            super.onInventoryChanged();
            if (field_145850_b != null)
            {
                field_145850_b.func_147471_g(field_145851_c, field_145848_d, field_145849_e);
            }
        }

        @Override
        public int getInventoryStackLimit ()
        {
            return 1;
        }
    }
}
