package tmechworks.blocks.logic;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import tconstruct.library.TConstructRegistry;
import mantle.blocks.BlockUtils;
import mantle.blocks.iface.*;
import tmechworks.inventory.DrawbridgeContainer;
import mantle.blocks.abstracts.InventoryLogic;
import mantle.common.ComparisonHelper;
import tmechworks.lib.player.FakePlayerLogic;
import tmechworks.lib.blocks.IDrawbridgeLogicBase;


public class DrawbridgeLogic extends InventoryLogic implements IFacingLogic, IActiveLogic, IDrawbridgeLogicBase
{
    boolean active;
    boolean working;
    int ticks;
    byte extension;
    byte maxExtension = 15;
    byte direction;
    byte placementDirection = 4;
    FakePlayerLogic fakePlayer;

    ItemStack bufferStack = null;

    public DrawbridgeLogic()
    {
        super(2);
    }

    @Override
    public void setfield_145850_b (World par1World)
    {
        this.field_145850_b = par1World;
        if (!field_145850_b.isRemote)
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

    public void setMaximumExtension (byte length)
    {
        maxExtension = length;
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

    /* 0 = Up
     * 1 = Right
     * 2 = Down
     * 3 = Left
     * 4 = Center, neutral
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
                if (keycode == 0) //Forward
                {
                    fakePlayer.rotationYaw = mapDirection() * 90;

                    if (keycode == 0)
                        fakePlayer.rotationPitch = 90;
                    else
                        fakePlayer.rotationPitch = -90;
                }
                else if (keycode == 2) //Backward
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
        if (this.direction == 2) //North
            return 0;
        if (this.direction == 5) //East
            return 1;
        if (this.direction == 3) //South
            return 2;

        return 3; //West
    }

    public byte getPlacementDirection ()
    {
        return placementDirection;
    }

    @Override
    public Container getGuiContainer (InventoryPlayer inventoryplayer, World world, int x, int y, int z)
    {
        return new DrawbridgeContainer(inventoryplayer, this);
    }

    @Override
    protected String getDefaultName ()
    {
        return "tmechworks.drawbridge";
    }

    @Override
    public void setInventorySlotContents (int slot, ItemStack itemstack)
    {
        super.setInventorySlotContents(slot, itemstack);
        if (slot == 1)
            field_145850_b.markBlockForUpdate(field_145851_c, field_145848_d, field_145849_e);
    }

    @Override
    public ItemStack decrStackSize (int slot, int quantity)
    {
        ItemStack stack = super.decrStackSize(slot, quantity);
        if (slot == 1)
            field_145850_b.markBlockForUpdate(field_145851_c, field_145848_d, field_145849_e);
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
                if (active) //Placement
                {
                    if (inventory[0] != null && inventory[0].stackSize > 0 && extension < maxExtension)
                    {
                        extension++;
                        int xPos = field_145851_c;
                        int yPos = field_145848_d;
                        int zPos = field_145849_e;

                        bufferStack = inventory[0].copy();
                        bufferStack.stackSize = 1;

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

                        Block block = field_145850_b.getBlock(xPos, yPos, zPos)];
                        if (block == null || block.isAirBlock(field_145850_b, xPos, yPos, zPos) || block.isBlockReplaceable(field_145850_b, xPos, yPos, zPos))
                        {
                            //tryExtend(field_145850_b, xPos, yPos, zPos, direction);
                            int blockToItem = TConstructRegistry.blockToItemMapping[bufferStack];
                            if (blockToItem == 0)
                            {
                                if (inventory[0].itemID >= 4096 || BlockUtils.getBlockFromItem(inventory[0].getItem()) == null)
                                    return;
                                Block placeBlock = BlockUtils.getBlockFromItem(bufferStack.getItem());
                                placeBlockAt(bufferStack, fakePlayer, field_145850_b, xPos, yPos, zPos, direction, 0, 0, 0, bufferStack.getItemDamage(), placeBlock);
                            }
                            else
                            {
                                Block placeBlock = Block.blocksList[blockToItem];
                                placeBlockAt(bufferStack, fakePlayer, field_145850_b, xPos, yPos, zPos, direction, 0, 0, 0, bufferStack.getItemDamage(), placeBlock);
                            }
                            field_145850_b.playSoundEffect((double) xPos + 0.5D, (double) yPos + 0.5D, (double) zPos + 0.5D, "tile.piston.out", 0.25F, field_145850_b.rand.nextFloat() * 0.25F + 0.6F);
                            decrStackSize(0, 1);
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
                //Retraction
                {
                    if ((inventory[0] == null || inventory[0].stackSize < inventory[0].getMaxStackSize()) && extension > 0)
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
                            if (bufferStack != null && validBlock(block) && validMetadata(block, meta) && validDrawbridge(xPos, yPos, zPos))
                            {
                                field_145850_b.playSoundEffect((double) xPos + 0.5D, (double) yPos + 0.5D, (double) zPos + 0.5D, "tile.piston.in", 0.25F, field_145850_b.rand.nextFloat() * 0.15F + 0.6F);
                                if (field_145850_b.setBlock(xPos, yPos, zPos, 0))
                                    if (inventory[0] == null)
                                    {
                                        inventory[0] = bufferStack.copy();
                                    }
                                    else
                                    {
                                        inventory[0].stackSize++;
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
     * Called to actually place the block, after the location is determined
     * and all permission checks have been made.
     * Copied from ItemBlock
     *
     * @param stack The item stack that was used to place the block. This can be changed inside the method.
     * @param player The player who is placing the block. Can be null if the block is not being placed by a player.
     * @param side The side the player (or machine) right-clicked on.
     */
    public boolean placeBlockAt (ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata, Block block)
    {
        if (!world.setBlock(x, y, z, block, metadata, 3))
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

    boolean validBlock (Block block)
    {
        Item type = TConstructRegistry.interchangableBlockMapping[block].getItem();
        if (type != null)
        {
            if (type == bufferStack.getItem())
                return true;
        }
        Item blockToItem = TConstructRegistry.blockToItemMapping[new ItemStack(block).getItem()];
        if (blockToItem != null)
        {
            if (blockToItem == bufferStack.getItem())
                return true;
        }
        return ComparisonHelper.areEquivalent(bufferStack.getItem(), block);
    }

    boolean validMetadata (Block block, int metadata)
    {
        int type = TConstructRegistry.drawbridgeState[block];
        if (type == 0)
        {
            return metadata == bufferStack.getItemDamage();
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
            return true; //TODO: rotational metadata, probably not needed anymore
        }
        if (type == 4)
        {
            return true;
        }
        if (type == 5)
        {
            return metadata == bufferStack.getItemDamage();
        }
        return false;
    }

    @Override
    public void readFromNBT (NBTTagCompound tags)
    {
        super.readFromNBT(tags);
        active = tags.getBoolean("Active");
        working = tags.getBoolean("Working");
        extension = tags.getByte("Extension");
        maxExtension = tags.getByte("MaxExtension");

        NBTTagCompound bufferInv = (NBTTagCompound) tags.getTag("BufferInv");
        if (bufferInv != null)
        {
            bufferStack = ItemStack.loadItemStackFromNBT(bufferInv);
        }
        if (bufferStack == null && inventory[0] != null)
        {
            bufferStack = inventory[0];
        }

        readCustomNBT(tags);
    }

    @Override
    public void writeToNBT (NBTTagCompound tags)
    {
        super.writeToNBT(tags);
        tags.setBoolean("Active", active);
        tags.setBoolean("Working", working);
        tags.setByte("Extension", extension);
        tags.setByte("MaxExtension", maxExtension);

        if (bufferStack != null)
        {
            NBTTagCompound bufferInv = new NBTTagCompound();
            bufferStack.writeToNBT(bufferInv);
            tags.setTag("BufferInv", bufferInv);
        }

        writeCustomNBT(tags);
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
        return new Packet132TileEntityData(field_145851_c, field_145848_d, field_145849_e, 1, tag);
    }

    @Override
    public void onDataPacket (NetworkManager net, Packet132TileEntityData packet)
    {
        readFromNBT(packet.data);
        field_145850_b.markBlockForRenderUpdate(field_145851_c, field_145848_d, field_145849_e);
    }

    public boolean hasExtended ()
    {
        return extension != 0;
    }

    @Override
    public void onInventoryChanged ()
    {
        super.onInventoryChanged();
        if (getStackInSlot(0) != null)
        {
            bufferStack = getStackInSlot(0).copy();
            bufferStack.stackSize = 1;
        }
        this.field_145850_b.markBlockForUpdate(field_145851_c, field_145848_d, field_145849_e);
    }
}
