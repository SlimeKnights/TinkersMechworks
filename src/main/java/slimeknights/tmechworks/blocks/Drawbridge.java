package slimeknights.tmechworks.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Locale;

import javax.annotation.Nonnull;

import slimeknights.mantle.block.BlockInventory;
import slimeknights.tmechworks.blocks.logic.DrawbridgeLogic;
import slimeknights.tmechworks.blocks.logic.DrawbridgeLogicBase;

public class Drawbridge extends BlockInventory
{

  public static final PropertyDirection FACING = PropertyDirection.create("facing");
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

  @Nonnull @Override public TileEntity createNewTileEntity (@Nonnull World worldIn, int meta)
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

  @Override protected boolean openGui (EntityPlayer player, World world, BlockPos pos)
  {
    return false;
  }

  @Override protected BlockStateContainer createBlockState ()
  {
    return new BlockStateContainer(this, FACING, TYPE);
  }

  @Override public IBlockState getActualState (IBlockState state, IBlockAccess worldIn, BlockPos pos)
  {
    DrawbridgeLogicBase baseLogic = (DrawbridgeLogicBase) worldIn.getTileEntity(pos);

    EnumFacing face = EnumFacing.NORTH;

    if (baseLogic != null)
    {
      face = baseLogic.getFacingDirection();
    }

    return state.withProperty(FACING, face);
    }

  @Override public void onBlockPlacedBy (World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
  {
    DrawbridgeLogicBase baseLogic = (DrawbridgeLogicBase) worldIn.getTileEntity(pos);

    if (baseLogic == null)
    {
      return;
    }

    baseLogic.setFacingDirection(BlockPistonBase.getFacingFromEntity(pos, placer));
    }

  @Override public void neighborChanged (IBlockState state, World worldIn, BlockPos pos, Block blockIn)
  {
    DrawbridgeLogicBase logicBase = (DrawbridgeLogicBase) worldIn.getTileEntity(pos);

    if (logicBase != null)
    {
      logicBase.updateRedstone();
    }
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
