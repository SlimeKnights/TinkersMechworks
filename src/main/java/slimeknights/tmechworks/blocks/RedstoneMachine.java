package slimeknights.tmechworks.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import slimeknights.mantle.block.BlockInventory;
import slimeknights.tmechworks.TMechworks;
import slimeknights.tmechworks.blocks.logic.RedstoneMachineLogicBase;

import javax.annotation.Nonnull;

public abstract class RedstoneMachine extends BlockInventory
{
    public static final PropertyDirection FACING = PropertyDirection.create("facing");

    protected RedstoneMachine (Material material)
    {
        super(material);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
    }

    @Override protected boolean openGui (EntityPlayer player, World world, BlockPos pos)
    {
        player.openGui(TMechworks.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    @Override @Nonnull protected BlockStateContainer createBlockState ()
    {
        return new BlockStateContainer(this, FACING);
    }

    @Override public IBlockState getActualState (@Nonnull IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        RedstoneMachineLogicBase baseLogic = (RedstoneMachineLogicBase) worldIn.getTileEntity(pos);

        EnumFacing face = EnumFacing.NORTH;

        if (baseLogic != null)
        {
            face = baseLogic.getFacingDirection();
        }

        return state.withProperty(FACING, face);
    }

    @Override public void neighborChanged (IBlockState state, World worldIn, BlockPos pos, Block blockIn)
    {
        RedstoneMachineLogicBase logicBase = (RedstoneMachineLogicBase) worldIn.getTileEntity(pos);

        if (logicBase != null)
        {
            logicBase.updateRedstone();
        }
    }

    public boolean hasFacingDirection ()
    {
        return true;
    }

    @Override public void onBlockPlacedBy (World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        if (!hasFacingDirection())
            return;

        RedstoneMachineLogicBase baseLogic = (RedstoneMachineLogicBase) worldIn.getTileEntity(pos);

        if (baseLogic == null)
        {
            return;
        }

        baseLogic.setFacingDirection(BlockPistonBase.getFacingFromEntity(pos, placer));
    }

    @Override public int getMetaFromState (IBlockState state)
    {
        return 0;
    }

    @Override public IBlockState getStateFromMeta (int meta)
    {
        return getDefaultState();
    }
}
