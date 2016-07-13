package slimeknights.tmechworks.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import slimeknights.tmechworks.blocks.logic.FirestarterLogic;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Firestarter extends RedstoneMachine
{
    public static final PropertyBool SHOULD_EXTENGUISH = PropertyBool.create("extinguish");

    public Firestarter ()
    {
        super(Material.IRON);
    }

    @Override public IBlockState getActualState (@Nonnull IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        FirestarterLogic firestarter = (FirestarterLogic) worldIn.getTileEntity(pos);

        boolean extenguishFire = true;

        if (firestarter != null)
        {
            extenguishFire = firestarter.shouldExtenguish;
        }

        return super.getActualState(state, worldIn, pos).withProperty(SHOULD_EXTENGUISH, extenguishFire);
    }

    @Nonnull @Override protected BlockStateContainer createBlockState ()
    {
        return new BlockStateContainer(this, FACING, SHOULD_EXTENGUISH);
    }

    @Nonnull @Override public TileEntity createNewTileEntity (@Nonnull World worldIn, int meta)
    {
        return new FirestarterLogic();
    }

    @Override
    public boolean onBlockActivated (World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY,
            float hitZ)
    {
        FirestarterLogic logic = (FirestarterLogic) worldIn.getTileEntity(pos);

        if (logic != null)
        {
            logic.shouldExtenguish = !logic.shouldExtenguish;
            worldIn.playSound(playerIn, pos, SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCKS, 0.3F, 0.55F);
            logic.sync();
        }

        return true;
    }
}
