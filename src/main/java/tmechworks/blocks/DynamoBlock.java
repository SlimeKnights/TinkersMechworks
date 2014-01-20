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
        super(Material.field_151573_f);
        this.func_149647_a(TMechworksRegistry.Mechworks);
    }
    
    public int getRenderType ()
    {
        return -1;
    }
    
    @Override
    public boolean func_149646_a (IBlockAccess iblockaccess, int x, int y, int z, int side)
    {
        return true;
    }

    @Override
    public boolean func_149686_d ()
    {
        return false;
    }

    @Override
    public boolean func_149662_c ()
    {
        return false;
    }

    @Override
    public TileEntity func_149915_a (World world, int meta)
    {
        return new DynamoLogic();
    }
}
