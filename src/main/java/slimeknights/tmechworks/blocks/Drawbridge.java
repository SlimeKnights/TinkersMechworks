package slimeknights.tmechworks.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.mantle.block.EnumBlock;
import slimeknights.tmechworks.blocks.logic.drawbridge.AdvancedDrawbridgeLogic;
import slimeknights.tmechworks.blocks.logic.drawbridge.DrawbridgeLogic;
import slimeknights.tmechworks.blocks.logic.drawbridge.DrawbridgeLogicBase;
import slimeknights.tmechworks.blocks.logic.drawbridge.ExtendedDrawbridgeLogic;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Locale;

public class Drawbridge extends RedstoneMachine<Drawbridge.DrawbridgeType>
{
    public static final PropertyEnum<DrawbridgeType> TYPE = PropertyEnum.create("type", DrawbridgeType.class, DrawbridgeType.values());

    public Drawbridge ()
    {
        super(Material.IRON, TYPE, DrawbridgeType.class);
        this.setDefaultState(this.getDefaultState().withProperty(TYPE, DrawbridgeType.NORMAL));
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

    @SideOnly(Side.CLIENT) public void getSubBlocks (Item itemIn, CreativeTabs tab, List<ItemStack> list)
    {
        list.add(new ItemStack(itemIn, 1, 0));
        list.add(new ItemStack(itemIn, 1, 1));
        list.add(new ItemStack(itemIn, 1, 2));
    }

    public enum DrawbridgeType implements IStringSerializable, EnumBlock.IEnumMeta
    {
        NORMAL(DrawbridgeLogic.class),
        ADVANCED(AdvancedDrawbridgeLogic.class),
        EXTENDED(ExtendedDrawbridgeLogic.class);

        public final Class<? extends DrawbridgeLogicBase> tileEntityClass;

        DrawbridgeType (Class<? extends DrawbridgeLogicBase> te)
        {
            tileEntityClass = te;
        }

        @Override public String getName ()
        {
            return this.toString().toLowerCase(Locale.US);
        }

        @Override
        public int getMeta() {
            return ordinal();
        }
    }

    public static NonNullList<ItemStack> dropCapture(boolean start)
    {
        if (start)
        {
            captureDrops.set(true);
            capturedDrops.get().clear();
            return NonNullList.create();
        }
        else
        {
            captureDrops.set(false);
            return capturedDrops.get();
        }
    }
}
