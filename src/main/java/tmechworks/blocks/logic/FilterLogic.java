package tmechworks.blocks.logic;

import java.util.List;

import mantle.world.CoordTuple;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import tmechworks.blocks.FilterBlock;

//Only implements ISidedInventory as a dummy so that the hopper logic can work properly.
public class FilterLogic extends TileEntity implements ISidedInventory
{
    int ticksPerUpdate = 10;
    //Dummy functionality
    public static final int[] accessibleSlots = new int[0];

    @Override
    public void updateEntity ()
    {
        super.updateEntity();
        if ((worldObj.getTotalWorldTime() % ticksPerUpdate) == 0)
        {
            CoordTuple position = new CoordTuple(xCoord, yCoord, zCoord);
            int metadata = worldObj.getBlockMetadata(position.x, position.y, position.z);
            FilterBlock filter = (FilterBlock) worldObj.getBlock(position.x, position.y, position.z);
            boolean passable = false;
            if (worldObj.getBlock(position.x, position.y - 1, position.z) == null)
            {
                passable = true;
            }
            else if (worldObj.getBlock(position.x, position.y - 1, position.z).isAir(worldObj, position.x, position.y - 1, position.z))
            {
                passable = true;
            }

            IInventory inv = getInventoryAtLocation(worldObj, new CoordTuple(position.x, position.y - 1, position.z));
            if (inv != null)
            {
                //Logic for if an inventory is below us.
                if (!filter.isTop(worldObj, position))
                {
                    List list = getEntitiesIn(worldObj, position);
                    for (Object item : list)
                    {
                        if (item instanceof EntityItem)
                        {
                            EntityItem entity = (EntityItem) item;
                            //Does this filter allow the entity in question to pass? If so, return without adding anything to the list.
                            if (filter.subFilters[filter.getSubFilter(metadata)] != null)
                            {
                                if (filter.subFilters[filter.getSubFilter(metadata)].canPass(entity))
                                {
                                    if (!worldObj.isRemote)
                                    {
                                        //Move our item past the filter.
                                        insertStackFromEntity(inv, entity);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            inv = getInventoryAtLocation(worldObj, new CoordTuple(position.x, position.y + 1, position.z));
            if (inv != null)
            {
                //Logic for if an inventory is above us.
                if (filter.isTop(worldObj, position))
                {

                    for (int i = 0; i < inv.getSizeInventory(); ++i)
                    {
                        //Make sure it's a valid item and a valid filter.
                        ItemStack itemStack = inv.getStackInSlot(i);
                        if ((itemStack != null) && (filter.subFilters[filter.getSubFilter(metadata)] != null))
                        {
                            //Check to see if the item can pass the filter.
                            if (filter.subFilters[filter.getSubFilter(metadata)].canPass(itemStack))
                            {
                                if (!worldObj.isRemote)
                                {
                                    //Remove the item and drop it as an EntityItem.
                                    ItemStack resultStack = inv.decrStackSize(i, itemStack.stackSize);
                                    EntityItem entityItem = new EntityItem(worldObj, position.x + 0.5D, position.y + (0.5D - filter.thickness), position.z + 0.5D);
                                    entityItem.setEntityItemStack(resultStack);
                                    worldObj.spawnEntityInWorld(entityItem);
                                    entityItem.motionX = 0.0D;
                                    entityItem.motionY = -entityItem.motionY;
                                    entityItem.motionZ = 0.0D;
                                }
                            }
                        }
                    }
                }
            }
            else
            {
                //Item pass logic.
                List list = null;
                if (filter.isTop(worldObj, position))
                {
                    list = getEntitiesIn(worldObj, position.x, position.y + 1, position.z);
                }
                else
                {
                    list = getEntitiesIn(worldObj, position);
                }
                if (list != null)
                {
                    for (Object item : list)
                    {
                        if (item instanceof Entity)
                        {
                            if (passable || filter.isTop(worldObj, position))
                            {
                                Entity entity = (Entity) item;
                                //Does this filter allow the entity in question to pass? If so, return without adding anything to the list.
                                if (filter.subFilters[filter.getSubFilter(metadata)] != null)
                                {
                                    if (filter.subFilters[filter.getSubFilter(metadata)].canPass(entity))
                                    {
                                        entity.motionX = 0.0F;
                                        entity.motionZ = 0.0F;
                                        //Move our item past the filter.
                                        //
                                        if (filter.isTop(worldObj, position))
                                        {
                                            entity.setPosition(entity.posX, position.y + (0.5D - filter.thickness), entity.posZ);
                                        }
                                        else
                                        {
                                            entity.setPosition(entity.posX, position.y - (entity.height + 0.1D), entity.posZ);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static IInventory getInventoryAtLocation (World world, CoordTuple position)
    {
        TileEntity tileentity = world.getTileEntity(position.x, position.y, position.z);

        if (tileentity != null && tileentity instanceof IInventory)
        {
            return (IInventory) tileentity;
        }

        return null;
    }

    public static boolean insertStackFromEntity (IInventory inv, EntityItem entity)
    {
        boolean flag = false;

        if (entity != null)
        {
            ItemStack itemstack = entity.getEntityItem().copy();
            ItemStack itemstack1 = insertStack(inv, itemstack);

            if (itemstack1 != null && itemstack1.stackSize != 0)
            {
                entity.setEntityItemStack(itemstack1);
            }
            else
            {
                flag = true;
                entity.setDead();
            }
        }
        return flag;
    }

    public static ItemStack insertStack (IInventory inv, ItemStack itemStack)
    {
        for (int i = 0; i < inv.getSizeInventory(); ++i)
        {
            ItemStack slotStack = inv.getStackInSlot(i);
            int remainingSpace = inv.getInventoryStackLimit();
            if (slotStack != null)
            {
                remainingSpace -= slotStack.stackSize;
            }
            if (remainingSpace >= itemStack.stackSize)
            {
                if (slotStack != null)
                {
                    //Inserting our whole stack of the same type of item
                    if (slotStack.isItemEqual(itemStack))
                    {
                        slotStack.stackSize += itemStack.stackSize;
                        inv.setInventorySlotContents(i, slotStack);
                        return null;
                    }
                }
                else
                {
                    //whole stack into empty slot
                    if (inv.isItemValidForSlot(i, itemStack))
                    {
                        inv.setInventorySlotContents(i, itemStack);
                        return null;
                    }
                }
            }
            else
            {
                if (slotStack != null)
                {
                    //Part of stack into slot with same item.
                    if (slotStack.isItemEqual(itemStack))
                    {
                        slotStack.stackSize += remainingSpace;
                        itemStack.stackSize -= remainingSpace;
                        inv.setInventorySlotContents(i, slotStack);
                    }
                }
                else
                {
                    //Part of stack into empty slot.
                    if (inv.isItemValidForSlot(i, itemStack))
                    {
                        inv.setInventorySlotContents(i, itemStack.splitStack(remainingSpace));
                    }
                }
            }
        }
        return itemStack;
    }

    public static List getEntitiesIn (World world, CoordTuple pos)
    {
        return getEntitiesIn(world, pos.x, pos.y, pos.z);
    }

    protected static List getEntitiesIn (World world, int x, int y, int z)
    {
        return world.selectEntitiesWithinAABB(EntityItem.class, AxisAlignedBB.getAABBPool().getAABB(x, y, z, x + 1.0D, y + 1.0D, z + 1.0D), IEntitySelector.selectAnything);
    }

    //Functions to make this a dummy inventory.
    @Override
    public int getSizeInventory ()
    {
        return 0;
    }

    @Override
    public ItemStack getStackInSlot (int i)
    {
        return null;
    }

    @Override
    public ItemStack decrStackSize (int i, int j)
    {
        return null;
    }

    @Override
    public ItemStack getStackInSlotOnClosing (int i)
    {
        return null;
    }

    @Override
    public void setInventorySlotContents (int i, ItemStack itemstack)
    {
    }

    @Override
    public int getInventoryStackLimit ()
    {
        return 0;
    }

    @Override
    public boolean isUseableByPlayer (EntityPlayer entityplayer)
    {
        return false;
    }

    @Override
    public boolean isItemValidForSlot (int i, ItemStack itemstack)
    {
        return false;
    }

    @Override
    public int[] getAccessibleSlotsFromSide (int var1)
    {
        return accessibleSlots;
    }

    @Override
    public boolean canInsertItem (int i, ItemStack itemstack, int j)
    {
        return false;
    }

    @Override
    public boolean canExtractItem (int i, ItemStack itemstack, int j)
    {
        return false;
    }

    @Override
    public void closeInventory ()
    {

    }

    @Override
    public String getInventoryName ()
    {
        return "TMechworks:Filter";
    }

    @Override
    public boolean hasCustomInventoryName ()
    {
        return false;
    }

    @Override
    public void openInventory ()
    {
    }
}
