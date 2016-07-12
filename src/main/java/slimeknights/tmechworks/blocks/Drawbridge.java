package slimeknights.tmechworks.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.World;
import slimeknights.tmechworks.blocks.logic.DrawbridgeLogic;
import slimeknights.tmechworks.blocks.logic.DrawbridgeLogicBase;

import javax.annotation.Nonnull;
import java.util.Locale;

public class Drawbridge extends RedstoneMachine
{
    public static final PropertyEnum<DrawbridgeType> TYPE = PropertyEnum.create("type", DrawbridgeType.class, DrawbridgeType.values());

    public Drawbridge ()
    {
        super(Material.IRON);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(TYPE, DrawbridgeType.NORMAL));
    }

    @Override public int getMetaFromState (IBlockState state)
    {
        return ((DrawbridgeType) state.getValue(TYPE)).ordinal();
    }

    @Override public IBlockState getStateFromMeta (int meta)
    {
        return getDefaultState().withProperty(TYPE, DrawbridgeType.values()[meta]);
    }

    @Override public TileEntity createNewTileEntity (@Nonnull World worldIn, int meta)
    {
        try
        {
            return DrawbridgeType.values()[meta].tileEntityClass.newInstance();
        }
        catch (InstantiationException | IllegalAccessException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    @Nonnull @Override protected BlockStateContainer createBlockState ()
    {
        return new BlockStateContainer(this, FACING, TYPE);
    }

    public enum DrawbridgeType implements IStringSerializable
    {
        NORMAL(DrawbridgeLogic.class),
        ADVANCED(DrawbridgeLogicBase.class),
        EXTENDED(DrawbridgeLogicBase.class);

        public final Class<? extends DrawbridgeLogicBase> tileEntityClass;

        DrawbridgeType (Class<? extends DrawbridgeLogicBase> te)
        {
            tileEntityClass = te;
        }

        @Override public String getName ()
        {
            return this.toString().toLowerCase(Locale.US);
        }
    }
}
