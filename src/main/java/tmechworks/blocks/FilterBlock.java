package tmechworks.blocks;

import java.util.List;

import mantle.world.CoordTuple;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import tmechworks.blocks.logic.FilterLogic;
import tmechworks.blocks.logic.SubFilter;
import tmechworks.client.block.FilterRender;
import tmechworks.lib.TMechworksRegistry;
import tmechworks.lib.blocks.IBlockWithMetadata;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class FilterBlock extends BlockContainer implements IBlockWithMetadata
{
    //For rendering and collision
    public static final double thickness = 0.1875D;
    //Width of the frame pieces.
    public static final double sideWidth = 0.2D;
    //Mapping of metadata to filter logic
    public SubFilter[] subFilters = new SubFilter[8];
    protected IIcon[] subMeshIcons = new IIcon[8];

    public FilterBlock()
    {
        super(Material.iron);
        this.setCreativeTab(TMechworksRegistry.Mechworks);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, (float) thickness, 1.0F);
    }

    @Override
    public boolean isOpaqueCube ()
    {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock ()
    {
        return false;
    }

    //Seems to be used in pathfinding.
    @Override
    public boolean getBlocksMovement (IBlockAccess par1IBlockAccess, int x, int y, int z)
    {
        return true;
    }

    /**
     * The type of render function that is called for this block
     */
    @Override
    public int getRenderType ()
    {
        return FilterRender.renderID;
    }

    public IIcon getMeshIcon (int metadata)
    {
        if (subMeshIcons[metadata & 7] != null)
        {
            return subMeshIcons[metadata & 7];
        }
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons (IIconRegister iconRegister)
    {
        super.registerBlockIcons(iconRegister);
        for (int i = 1; i < 8; i++)
        {
            if (subFilters[i] != null)
            {
                if (subFilters[i].getMeshIconName() != null)
                {
                    subMeshIcons[i] = iconRegister.registerIcon(subFilters[i].getMeshIconName());
                }
            }
        }
    }

    public void setSubFilter (SubFilter setTo, int i)
    {
        //0 is reserved.
        if ((i != 0) && (i < 8))
        {
            subFilters[i] = setTo;
        }
    }

    public int getSubFilter (IBlockAccess world, CoordTuple position)
    {
        //Extract out lowest 3 bits, ignoring 4th (the 8 bit).
        return getSubFilter(world.getBlockMetadata(position.x, position.y, position.z));
    }

    public int getSubFilter (int metadata)
    {
        //Extract out lowest 3 bits, ignoring 4th (the 8 bit).
        return metadata & 7;
    }

    public boolean isTop (IBlockAccess world, CoordTuple position)
    {
        //Check highest-order metadata bit.
        return (world.getBlockMetadata(position.x, position.y, position.z) & 8) != 0;
    }

    /**
     * Updates the blocks bounds based on its current state. Args: world, x, y, z
     */
    @Override
    public void setBlockBoundsBasedOnState (IBlockAccess world, int x, int y, int z)
    {
        if (isTop(world, new CoordTuple(x, y, z)))
        {
            this.setBlockBounds(0.0F, 1.0F - (float) thickness, 0.0F, 1.0F, 1.0F, 1.0F);
        }
        else
        {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, (float) thickness, 1.0F);
        }
    }

    /**
     * Sets the block's bounds for rendering it as an item
     */
    @Override
    public void setBlockBoundsForItemRender ()
    {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, (float) thickness, 1.0F);
    }

    /**
     * Adds all intersecting collision boxes to a list. (Be sure to only add boxes to the list if they intersect the
     * mask.) Parameters: World, X, Y, Z, mask, list, colliding entity
     * 
     * Filter logic goes here.
     */
    @Override
    public void addCollisionBoxesToList (World world, int x, int y, int z, AxisAlignedBB region, List result, Entity entity)
    {
        /*CoordTuple position = new CoordTuple(x, y, z);
        //Does this metadata correspond to a filter? If no, it's empty and entities can pass through the middle.
        if((subFilters[getSubFilter(world, position)] == null) || (getSubFilter(world, position) == 0))
        {
        	this.addCollisionEmpty(world, position, region, result, entity);
        	return;
        }*/
        this.setBlockBoundsBasedOnState(world, x, y, z);
        super.addCollisionBoxesToList(world, x, y, z, region, result, entity);
    }

    //To save my poor copy+paste fingers. Cannot be refactored to use Coord Tuples because this really does need to use doubles
    private final AxisAlignedBB getOffsetAABB (double x, double y, double z, double x1, double y1, double z1, double x2, double y2, double z2)
    {
        return AxisAlignedBB.getAABBPool().getAABB(x + x1, y + y1, z + z1, x + x2, y + y2, z + z2);
    }

    //Do the empty frame collision thing.
    private final void addCollisionEmpty (World world, CoordTuple position, AxisAlignedBB region, List result, Entity entity)
    {
        double bottom = 0.0D;
        double top = thickness;
        if (isTop(world, position))
        {
            bottom = 1.0D - thickness;
            top = 1.0D;
        }
        //Long sides.
        AxisAlignedBB[] sides = new AxisAlignedBB[4];
        sides[0] = getOffsetAABB(position.x, position.y, position.z, 0.0, bottom, 0.0, sideWidth, top, 1.0D);

        sides[1] = getOffsetAABB(position.x, position.y, position.z, 1.0D - sideWidth, bottom, 0.0, 1.0D, top, 1.0D);

        //Short sides.
        sides[2] = getOffsetAABB(position.x, position.y, position.z, sideWidth, bottom, 0.0, 1.0D - sideWidth, top, sideWidth);

        sides[3] = getOffsetAABB(position.x, position.y, position.z, sideWidth, bottom, 1.0D - sideWidth, 1.0D - sideWidth, top, 1.0D);
        //Check and add our sides.
        for (int i = 0; i < 4; i++)
        {
            if (sides[i] != null && sides[i].intersectsWith(region))
            {
                result.add(sides[i]);
            }
        }
    }

    @Override
    public int onBlockPlaced (World par1World, int par2, int par3, int par4, int par5, float par6, float par7, float par8, int par9)
    {
        return (par5 != 0 && (par5 == 1 || (double) par7 <= 0.5D) ? par9 : par9 | 8);
    }

    @Override
    public int damageDropped (int meta)
    {
        //Filter out the 4th bit.
        return meta & 7;
    }

    @SideOnly(Side.CLIENT)
    public boolean filter_shouldSideBeRendered (IBlockAccess world, int x, int y, int z, int side)
    {
        if (side != 1 && side != 0 && !super.shouldSideBeRendered(world, x, y, z, side))
        {
            return false;
        }
        else
        {
            CoordTuple position = new CoordTuple(x, y, z);
            boolean flag = isTop(world, position);
            if (flag && (side == 1))
            {
                return super.shouldSideBeRendered(world, position.x, position.y, position.z, side);
            }
            else if (!flag && (side == 0))
            {
                return super.shouldSideBeRendered(world, position.x, position.y, position.z, side);
            }
            else
            {
                return true;
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered (IBlockAccess world, int x, int y, int z, int side)
    {
        return true;
    }

    /**
     * Get the block's damage value (for use with pick block).
     */
    @Override
    public int getDamageValue (World par1World, int par2, int par3, int par4)
    {
        return super.getDamageValue(par1World, par2, par3, par4) & 7;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks (Item ourItem, CreativeTabs tab, List list)
    {
        //Add our reserved 0 metadata, which is meshless.
        list.add(new ItemStack(ourItem, 1, 0));
        for (int i = 1; i < 8; ++i)
        {
            //Add our different mesh types to the creative tab.
            if (subFilters[i] != null)
            {
                list.add(new ItemStack(ourItem, 1, i));
            }
        }
    }

    //Right-click
    @Override
    public boolean onBlockActivated (World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9)
    {
        if (world.isRemote)
            return !player.isSneaking();
        if (!player.isSneaking())
        {
            CoordTuple position = new CoordTuple(x, y, z);
            int metadata = world.getBlockMetadata(x, y, z);
            boolean setEmpty = false;
            ItemStack playerItem = player.inventory.getCurrentItem();
            //---Item removal logic.---
            if (subFilters[metadata & 7] != null)
            {
                if (subFilters[getSubFilter(metadata)].getAssociatedItem() != null)
                {
                    ItemStack item = subFilters[getSubFilter(metadata)].getAssociatedItem().copy();

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
                for (int i = 1; i < 8; ++i)
                {
                    //Add our different mesh types to the creative tab.
                    if (subFilters[i] != null)
                    {
                        if (subFilters[i].getAssociatedItem() != null)
                        {
                            //Is it the same item?
                            if (subFilters[i].getAssociatedItem().getItem() != null && playerItem.getItem() != null && subFilters[i].getAssociatedItem().getItem() == playerItem.getItem())
                            {
                                //If we care about damage, check it.
                                //if (!subFilters[i].isItemMetaSensitive() || (subFilters[i].getAssociatedItem().getItemDamage() == playerItem.getItemDamage()))
                                //{
                                //A match has been found.
                                if (isTop(world, position))
                                {
                                    world.setBlockMetadataWithNotify(position.x, position.y, position.z, i | 8, 1 | 2);
                                }
                                else
                                {
                                    world.setBlockMetadataWithNotify(position.x, position.y, position.z, i, 1 | 2);
                                }
                                //Remove the item from the player's inventory if the player is not in creative mode.
                                if (!player.capabilities.isCreativeMode)
                                {
                                    --playerItem.stackSize;
                                    if (playerItem.stackSize == 0)
                                    {
                                        playerItem = null;
                                    }
                                }
                                //Make sure our new metadata is maintained. 
                                setEmpty = false;
                                //Stop looping.
                                break;
                                //}
                            }
                        }
                    }
                }
            }
            if (setEmpty == true)
            {
                if (isTop(world, position))
                {
                    world.setBlockMetadataWithNotify(position.x, position.y, position.z, 8, 1 | 2);
                }
                else
                {
                    world.setBlockMetadataWithNotify(position.x, position.y, position.z, 0, 1 | 2);
                }
            }
            //If we're not sneaking, the right-click is intercepted, always.
            return true;
        }
        return false;
    }

    @Override
    public TileEntity createNewTileEntity (World world, int metadata)
    {
        // TODO Optimize away the Tile Entity through a ticking standard block utility or through Forge events.
        return new FilterLogic();
    }

    @Override
    public String getUnlocalizedNameByMetadata (int damageValue)
    {
        if (subFilters[getSubFilter(damageValue)] != null)
        {
            return this.getUnlocalizedName() + "." + subFilters[getSubFilter(damageValue)].getSuffix();
        }
        return this.getUnlocalizedName();
    }

    @Override
    public int getItemCount ()
    {
        return 8;
    }
}
