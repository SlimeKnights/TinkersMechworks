package tmechworks.lib.multiblock;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public abstract class BlockMultiblock extends BlockContainer
{

    protected BlockMultiblock(Material par2Material)
    {
        super(par2Material);
    }

    @Override
    public abstract TileEntity createNewTileEntity (World world, int meta);

}
