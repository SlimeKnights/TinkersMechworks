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

//Only implements ISidedInventory as a dummy so that the hopper logic can work properly.
public class FilterLogic extends TileEntity implements ISidedInventory {
	int ticksPerUpdate = 10;
	//Dummy functionality
	public static final int[] accessibleSlots = new int[0];
	@Override
	public void updateEntity() {
		super.updateEntity();
		if((worldObj.getTotalWorldTime() % ticksPerUpdate) == 0)
		{
			int metadata = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
			FilterBlock filter = (FilterBlock)Block.blocksList[worldObj.getBlockId(xCoord, yCoord, zCoord)];
			boolean passable = false;
			if(Block.blocksList[worldObj.getBlockId(xCoord, yCoord-1, zCoord)] == null)
			{
				passable = true;
			}
			else if(!Block.isNormalCube(worldObj.getBlockId(xCoord, yCoord-1, zCoord)))
			{
				passable = true;
			}

			//Mojang I have never loved you more than I do now for making these static.
			IInventory inv = TileEntityHopper.getInventoryAtLocation(worldObj, xCoord, yCoord-1, zCoord);
			if(inv != null)
			{
				//Logic for if an inventory is below us.
				if(!filter.isTop(worldObj, xCoord, yCoord, zCoord))
				{
					List list = getEntitiesIn(worldObj, xCoord, yCoord, zCoord);
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
									//Move our item past the filter.
									TileEntityHopper.insertStackFromEntity(inv, entity);
								}
							}
						}
					}
				}
			}
			else if(passable)
			{
				List list = null;
				if(filter.isTop(worldObj, xCoord, yCoord, zCoord))
				{
					list = getEntitiesIn(worldObj, xCoord, yCoord+1, zCoord);
				}
				else
				{
					list = getEntitiesIn(worldObj, xCoord, yCoord, zCoord);
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
									//entity.motionX = 0.0F;
									//entity.motionY = 0.0F;
									//Move our item past the filter.
									entity.setPosition(entity.posX, entity.posY-(entity.height + (filter.thickness*1.2)), entity.posZ);
								}
							}
						}
					}
				}
			}
		}
	}
    public static List getEntitiesIn(World par0World, int par1, int par3, int par5)
    {
        return par0World.selectEntitiesWithinAABB(EntityItem.class, 
        		AxisAlignedBB.getAABBPool().getAABB(par1, par3, par5, par1 + 1.0D, par3 + 1.0D, par5 + 1.0D), IEntitySelector.selectAnything);
    }
	@Override
	public int getSizeInventory() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public ItemStack getStackInSlot(int i) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public ItemStack decrStackSize(int i, int j) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public String getInvName() {
		// TODO Auto-generated method stub
		return "TMechworks:Filter";
	}
	@Override
	public boolean isInvNameLocalized() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public int getInventoryStackLimit() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void openChest() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void closeChest() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public int[] getAccessibleSlotsFromSide(int var1) {
		return accessibleSlots;
	}
	@Override
	public boolean canInsertItem(int i, ItemStack itemstack, int j) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		// TODO Auto-generated method stub
		return false;
	}
}
