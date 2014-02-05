package tmechworks.blocks;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import tmechworks.blocks.logic.DynamoLogic;
import tmechworks.lib.TMechworksRegistry;

public class DynamoBlock extends BlockContainer
{
    public DynamoBlock()
    {
        super(Material.iron);
        this.setCreativeTab(TMechworksRegistry.Mechworks);
    }

    public int getRenderType ()
    {
        return -1;
    }

    @Override
    public boolean shouldSideBeRendered (IBlockAccess iblockaccess, int x, int y, int z, int side)
    {
        return true;
    }

    @Override
    public boolean renderAsNormalBlock ()
    {
        return false;
    }

    @Override
    public boolean isOpaqueCube ()
    {
        return false;
    }

    @Override
    public TileEntity createNewTileEntity (World world, int meta)
    {
        return new DynamoLogic();
    }
}
