package tmechworks.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.FakePlayer;
import tmechworks.blocks.logic.SubFilter;
import tmechworks.client.block.FilterRender;
import tmechworks.lib.TMechworksRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class FilterBlock extends Block
{
	//For rendering and collision
	public static final double thickness = 0.1875D;
    //Width of the frame pieces.
    public static final double sideWidth = 0.1D;
	//Mapping of metadata to filter logic
	public SubFilter[] subFilters = new SubFilter[8];
	protected Icon[] subMeshIcons = new Icon[8];
	
	public FilterBlock(int id)
	{
        super(id, Material.iron);
        this.setCreativeTab(TMechworksRegistry.Mechworks);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, (float)thickness, 1.0F);
    }
	
    public boolean isOpaqueCube()
    {
        return false;
    }

    public boolean renderAsNormalBlock()
    {
        return false;
    }

    //Seems to be used in pathfinding.
    public boolean getBlocksMovement(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
        return true;
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return FilterRender.renderID;
    }
    
    public Icon getMeshIcon(int metadata)
    {
    	if(subMeshIcons[metadata&7] != null) {
    		return subMeshIcons[metadata&7];
    	}
    	return null;
    }

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister)
	{
		super.registerIcons(iconRegister);
		for(int i = 1; i < 8; i++)
		{
			if(subFilters[i] != null)
			{
				subMeshIcons[i] = iconRegister.registerIcon(subFilters[i].getMeshIconName());
			}
		}
	}
	
	public void setSubFilter(SubFilter setTo, int i) 
	{
		//0 is reserved.
		if((i != 0) && (i < 8)) {
			subFilters[i] = setTo;
		}
	}
	public int getSubFilter(IBlockAccess world, int x, int y, int z)
	{
		//Extract out lowest 3 bits, ignoring 4th (the 8 bit).
		return world.getBlockMetadata(x, y, z) & 7;
	}
	public boolean isTop(IBlockAccess world, int x, int y, int z)
	{
		//Check highest-order metadata bit.
		return (world.getBlockMetadata(x, y, z) & 8) != 0;
	}
	/**
	 * Updates the blocks bounds based on its current state. Args: world, x, y, z
	 */
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z)
	{
		if (isTop(world, x, y, z))
		{
			this.setBlockBounds(0.0F, 1.0F-(float)thickness, 0.0F, 1.0F, 1.0F, 1.0F);
		}
		else
		{
			this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, (float)thickness, 1.0F);
		}
	}

	/**
	 * Sets the block's bounds for rendering it as an item
	 */
	public void setBlockBoundsForItemRender()
	{
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, (float)thickness, 1.0F);
	}

	/**
	 * Adds all intersecting collision boxes to a list. (Be sure to only add boxes to the list if they intersect the
	 * mask.) Parameters: World, X, Y, Z, mask, list, colliding entity
	 * 
	 * Filter logic goes here.
	 */
	@Override
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB region, List result, Entity entity)
	{
		/*
		AxisAlignedBB broadphase = AxisAlignedBB.getAABBPool().getAABB(x, y, z, x + 1, y + 1, z + 1);
		if (broadphase != null && broadphase.intersectsWith(region)) {
			//Check to make sure that we're not above a solid cube.
			boolean passable = false;
			if(Block.blocksList[world.getBlockId(x, y-1, z)] == null)
			{
				passable = true;
			}
			else if(!Block.isNormalCube(world.getBlockId(x, y-1, z)))
			{
				passable = true;
			}
			
			if(passable){
				//Does this filter allow the entity in question to pass? If so, return without adding anything to the list.
				if(subFilters[getSubFilter(world, x, y, z)] != null)
				{
					if(subFilters[getSubFilter(world, x, y, z)].canPass(entity))
					{
						//Prevent strange bugs. Vertical movement is the only movement through a filter.
						entity.motionX = 0.0F;
						entity.motionZ = 0.0F;
						return;
					}
				}
				//Does this metadata correspond to a filter? If no, it's empty and entities can pass through the middle.
				if(subFilters[getSubFilter(world, x, y, z)] == null)
				{
					this.addCollisionEmpty(world, x, y, z, region, result, entity);
					return;
				}
			}
		}*/
		//Does this metadata correspond to a filter? If no, it's empty and entities can pass through the middle.
		if(subFilters[getSubFilter(world, x, y, z)] == null)
		{
			this.addCollisionEmpty(world, x, y, z, region, result, entity);
			return;
		}
		this.setBlockBoundsBasedOnState(world, x, y, z);
		super.addCollisionBoxesToList(world, x, y, z, region, result, entity);
	}

	//To save my poor copy+paste fingers.
	private final AxisAlignedBB getOffsetAABB(double x, double y, double z, double x1, double y1, double z1, double x2, double y2, double z2)
	{
		return AxisAlignedBB.getAABBPool().getAABB(x+x1, y+y1, z+z1, x+x2, y+y2, z+z2);
	}
	//Do the empty frame collision thing.
	private final void addCollisionEmpty(World world, int x, int y, int z, AxisAlignedBB region, List result, Entity entity)
	{
    	double bottom = 0.0D;
        double top = thickness;
        if(isTop(world, x, y, z)) {
        	bottom = 1.0D-thickness;
        	top = 1.0D;
        }
    	//Long sides.
		AxisAlignedBB[] sides = new AxisAlignedBB[4];
		sides[0] = getOffsetAABB(x, y, z,
        		0.0, bottom, 0.0, 
        		sideWidth, top, 1.0D);
        
		sides[1] = getOffsetAABB(x, y, z,
        		1.0D-sideWidth, bottom, 0.0, 
        		1.0D, top, 1.0D);
    	//Short sides.
		sides[2] = getOffsetAABB(x, y, z,
        		sideWidth, bottom, 0.0, 
        		1.0D-sideWidth, top, sideWidth);
        
		sides[3] = getOffsetAABB(x, y, z,
        		sideWidth, bottom, 1.0D-sideWidth, 
        		1.0D-sideWidth, top, 1.0D);
		//Check and add our sides.
		for(int i = 0; i < 4; i++)
		{
			if (sides[i] != null && sides[i].intersectsWith(region))
			{
				result.add(sides[i]);
			}
		}
	}
	/**
	 * Called when a block is placed using its ItemBlock. Args: World, X, Y, Z, side, hitX, hitY, hitZ, block metadata
	 */
	public int onBlockPlaced(World par1World, int par2, int par3, int par4, int par5, float par6, float par7, float par8, int par9)
	{
		return (par5 != 0 && (par5 == 1 || (double)par7 <= 0.5D) ? par9 : par9 | 8);
	}

	public int damageDropped(int meta)
	{
		//Filter out the 4th bit.
		return meta & 7;
	}


	/**
	 * Returns true if the given side of this block type should be rendered, if the adjacent block is at the given
	 * coordinates.  Args: blockAccess, x, y, z, side
	 * 
	 * At some point I've gotta use this with the custom renderer to do optimization things.
	 */
	@SideOnly(Side.CLIENT)
	public boolean filter_shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side)
	{
		if (side != 1 && side != 0 && !super.shouldSideBeRendered(world, x, y, z, side))
		{
			return false;
		}
		else
		{
			boolean flag = isTop(world, x, y, z);
			if(flag && (side == 1))
			{
				return super.shouldSideBeRendered(world, x, y, z, side);
			}
			else if(!flag && (side == 0))
			{
				return super.shouldSideBeRendered(world, x, y, z, side);
			}
			else
			{
				return true;
			}
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side)
	{
		return true;
	}
	
	
	/**
	 * Get the block's damage value (for use with pick block).
	 */
	public int getDamageValue(World par1World, int par2, int par3, int par4)
	{
		return super.getDamageValue(par1World, par2, par3, par4) & 7;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs,
			List par3List)
	{
		//Add our reserved 0 metadata, which is meshless.
        par3List.add(new ItemStack(par1, 1, 0));
        for(int i = 1; i < 8; ++i)
        {
        	//Add our different mesh types to the creative tab.
        	if(subFilters[i] != null) {
        		par3List.add(new ItemStack(par1, 1, i));
        	}
        }
	}
	
    //Right-click
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9)
    {
    	if(world.isRemote)
    		return !player.isSneaking();
    	if(!player.isSneaking())
    	{
    		int metadata = world.getBlockMetadata(x, y, z);
    		boolean setEmpty = false;
	    	ItemStack playerItem = player.inventory.getCurrentItem();
	    	//---Item removal logic.---
	    	if(subFilters[metadata&7] != null)
	    	{
	    		if(subFilters[metadata&7].getAssociatedItem() != null)
	    		{
	    			ItemStack item = subFilters[metadata&7].getAssociatedItem().copy();

	    			EntityItem entityitem = new EntityItem(world, player.posX, player.posY - 1.0D, player.posZ, item);
	    			world.spawnEntityInWorld(entityitem);
	    			if (!(player instanceof FakePlayer))
	    			{
	    				entityitem.onCollideWithPlayer(player);
	    			}
	    			setEmpty = true;
	    		}
	    	}
	    	//---Item insertion logic.---
	    	//Check to see if the player is holding something that can be filled with a fluid.
	    	else if (playerItem != null)
	    	{
	            for(int i = 1; i < 8; ++i)
	            {
	            	//Add our different mesh types to the creative tab.
	            	if(subFilters[i] != null)
	            	{
	            		//Is it the same item?
	            		if(subFilters[i].getAssociatedItem().getItem().getUnlocalizedName().contentEquals(playerItem.getItem().getUnlocalizedName()))
	            		{
	            			//If we care about damage, check it.
	            			if(!subFilters[i].isItemMetaSensitive() || (subFilters[i].getAssociatedItem().getItemDamage() == playerItem.getItemDamage()))
	            			{
	            				//A match has been found.
	            				if(isTop(world, x, y, z))
	            				{
	            					world.setBlockMetadataWithNotify(x, y, z, i|8, 1|2);
	            				}
	            				else
	            				{
	            					world.setBlockMetadataWithNotify(x, y, z, i, 1|2);
	            				}
	            				//Remove the item from the player's inventory if the player is not in creative mode.
	            				if(!player.capabilities.isCreativeMode)
	            				{
	            					--playerItem.stackSize;
	            					if(playerItem.stackSize == 0)
	            					{
	            						playerItem = null;
	            					}
	            				}
	            				//Make sure our new metadata is maintained. 
	            				setEmpty = false;
	            				//Stop looping.
	            				break;
	            			}
	            		}
	            	}
	            }
	    	}
	    	if(setEmpty == true) {
				if(isTop(world, x, y, z))
				{
					world.setBlockMetadataWithNotify(x, y, z, 8, 1|2);
				}
				else
				{
					world.setBlockMetadataWithNotify(x, y, z, 0, 1|2);
				}
	    	}
	    	//If we're not sneaking, the right-click is intercepted, always.
	    	return true;
    	}
    	return false;
    }

	@Override
	public boolean canCollideCheck(int metadata, boolean boats)
	{
		// TODO Auto-generated method stub
		return (metadata != 0) && (metadata != 8);
	}

	@Override
	public boolean isCollidable()
	{
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z,
			Entity entity)
	{
		// TODO Auto-generated method stub
		super.onEntityCollidedWithBlock(world, x, y, z, entity);
		onFallenUpon(world, x, y, z, entity, 0.0F);
	}

	@Override
	public void onFallenUpon(World world, int x, int y, int z,
			Entity entity, float par6)
	{
		super.onFallenUpon(world, x, y, z, entity, par6);
		boolean passable = false;
		if(Block.blocksList[world.getBlockId(x, y-1, z)] == null)
		{
			passable = true;
		}
		else if(!Block.isNormalCube(world.getBlockId(x, y-1, z)))
		{
			passable = true;
		}
		
		if(passable)
		{
			//Does this filter allow the entity in question to pass? If so, return without adding anything to the list.
			if(subFilters[getSubFilter(world, x, y, z)] != null)
			{
				if(subFilters[getSubFilter(world, x, y, z)].canPass(entity))
				{
					entity.motionX = 0.0F;
					entity.motionY = 0.0F;
					//Move our item past the filter.
					entity.setPosition(entity.posX, entity.posY-(entity.height + thickness*2), entity.posZ);
				}
			}
		}
	}
}
