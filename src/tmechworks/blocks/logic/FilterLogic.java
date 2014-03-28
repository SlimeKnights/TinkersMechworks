package tmechworks.blocks.logic;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import tmechworks.blocks.FilterBlock;
import tmechworks.lib.util.CoordTuple;

//Only implements ISidedInventory as a dummy so that the hopper logic can work properly.
public class FilterLogic extends TileEntity implements ISidedInventory
{
	int ticksPerUpdate = 10;
	//Dummy functionality
	public static final int[] accessibleSlots = new int[0];
	
	@Override
	public void updateEntity()
	{
		super.updateEntity();
		if((worldObj.getTotalWorldTime() % ticksPerUpdate) == 0)
		{
			CoordTuple position = new CoordTuple(xCoord, yCoord, zCoord);
			int metadata = worldObj.getBlockMetadata(position.x, position.y, position.z);
			FilterBlock filter = (FilterBlock)Block.blocksList[worldObj.getBlockId(position.x, position.y, position.z)];
			boolean passable = false;
			if(Block.blocksList[worldObj.getBlockId(position.x, position.y-1, position.z)] == null)
			{
				passable = true;
			}
			else if(!Block.isNormalCube(worldObj.getBlockId(position.x, position.y-1, position.z)))
			{
				passable = true;
			}

			//Mojang I have never loved you more than I do now for making these static.
			IInventory inv = TileEntityHopper.getInventoryAtLocation(worldObj, position.x, position.y-1, position.z);
			if(inv != null)
			{
				//Logic for if an inventory is below us.
				if(!filter.isTop(worldObj, position))
				{
					List list = getEntitiesIn(worldObj, position);
					for(Object item:list)
					{
						if(item instanceof EntityItem)
						{
							EntityItem entity = (EntityItem)item;
							//Does this filter allow the entity in question to pass? If so, return without adding anything to the list.
							if(filter.subFilters[filter.getSubFilter(metadata)] != null)
							{
								if(filter.subFilters[filter.getSubFilter(metadata)].canPass(entity))
								{
									if(!worldObj.isRemote)
									{
										//Move our item past the filter.
										TileEntityHopper.insertStackFromEntity(inv, entity);
									}
								}
							}
						}
					}
				}
			}
			inv = TileEntityHopper.getInventoryAtLocation(worldObj, position.x, position.y+1, position.z);
			if(inv != null)
			{
				//Logic for if an inventory is above us.
				if(filter.isTop(worldObj, position))
				{
					
					for(int i = 0; i < inv.getSizeInventory(); ++i)
					{
						//Make sure it's a valid item and a valid filter.
						ItemStack itemStack = inv.getStackInSlot(i);
						if((itemStack != null) && (filter.subFilters[filter.getSubFilter(metadata)] != null))
						{
							//Check to see if the item can pass the filter.
							if(filter.subFilters[filter.getSubFilter(metadata)].canPass(itemStack))
							{
								if(!worldObj.isRemote)
								{
									//Remove the item and drop it as an EntityItem.
									ItemStack resultStack = inv.decrStackSize(i, itemStack.stackSize);
									EntityItem entityItem = new EntityItem(worldObj, position.x+0.5D, position.y+(0.5D - filter.thickness), position.z+0.5D);
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
			else if(passable)
			{
				List list = null;
				if(filter.isTop(worldObj, position))
				{
					list = getEntitiesIn(worldObj, position.x, position.y+1, position.z);
				}
				else
				{
					list = getEntitiesIn(worldObj, position);
				}
				if(list != null)
				{
					for(Object item:list)
					{
						if(item instanceof Entity)
						{
							Entity entity = (Entity)item;
							//Does this filter allow the entity in question to pass? If so, return without adding anything to the list.
							if(filter.subFilters[filter.getSubFilter(metadata)] != null)
							{
								if(filter.subFilters[filter.getSubFilter(metadata)].canPass(entity))
								{
									entity.motionX = 0.0F;
									entity.motionZ = 0.0F;
									//Move our item past the filter.
									//
									if(filter.isTop(worldObj, position)) {
										entity.setPosition(entity.posX, position.y + (0.5D - filter.thickness), entity.posZ);
									}
									else {
										entity.setPosition(entity.posX, position.y - (entity.height+0.1D), entity.posZ);
									}
								}
							}
						}
					}
				}
			}
		}
	}
    public static List getEntitiesIn(World world, CoordTuple pos)
    {
        return getEntitiesIn(world, pos.x, pos.y, pos.z);
    }   
    protected static List getEntitiesIn(World world, int x, int y, int z)
    {
        return world.selectEntitiesWithinAABB(EntityItem.class,
         AxisAlignedBB.getAABBPool().getAABB(x, y, z, x + 1.0D, y + 1.0D, z + 1.0D), IEntitySelector.selectAnything);
    }
    
    //Functions to make this a dummy inventory.
	@Override
	public int getSizeInventory()
	{
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public ItemStack getStackInSlot(int i)
	{
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public ItemStack decrStackSize(int i, int j)
	{
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public ItemStack getStackInSlotOnClosing(int i)
	{
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack)
	{
	}
	@Override
	public String getInvName()
	{
		return "TMechworks:Filter";
	}
	@Override
	public boolean isInvNameLocalized()
	{
		return false;
	}
	@Override
	public int getInventoryStackLimit()
	{
		return 0;
	}
	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer)
	{
		return false;
	}
	@Override
	public void openChest()
	{
	}
	@Override
	public void closeChest()
	{
	}
	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack)
	{
		return false;
	}
	@Override
	public int[] getAccessibleSlotsFromSide(int var1)
	{
		return accessibleSlots;
	}
	@Override
	public boolean canInsertItem(int i, ItemStack itemstack, int j)
	{
		return false;
	}
	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j)
	{
		return false;
	}
}
