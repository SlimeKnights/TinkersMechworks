package slimeknights.tmechworks.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import slimeknights.mantle.block.EnumBlock;
import slimeknights.mantle.tileentity.TileInventory;
import slimeknights.tmechworks.TMechworks;
import slimeknights.tmechworks.blocks.logic.RedstoneMachineLogicBase;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;

public abstract class RedstoneMachine<E extends Enum<E> & EnumBlock.IEnumMeta & IStringSerializable> extends EnumBlock<E> implements ITileEntityProvider {
    public static final PropertyEnum<DefaultTypes> DEF_TYPE = PropertyEnum.create("type", DefaultTypes.class);
    public static final PropertyDirection FACING = PropertyDirection.create("facing");

    public boolean dropState = true;

    private TileEntity cachedTE;

    protected RedstoneMachine(Material material, PropertyEnum<E> prop, Class<E> clazz) {
        super(material, prop, clazz);
        this.hasTileEntity = true;
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
    }

    protected boolean openGui(EntityPlayer player, World world, BlockPos pos) {
        player.openGui(TMechworks.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, prop, FACING);
    }

    @Override
    public IBlockState getActualState(@Nonnull IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        RedstoneMachineLogicBase baseLogic = (RedstoneMachineLogicBase) worldIn.getTileEntity(pos);

        EnumFacing face = EnumFacing.NORTH;

        if (baseLogic != null) {
            face = baseLogic.getFacingDirection();
        }

        return state.withProperty(FACING, face);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos from) {
        RedstoneMachineLogicBase logicBase = (RedstoneMachineLogicBase) worldIn.getTileEntity(pos);

        if (logicBase != null) {
            logicBase.updateRedstone();
        }
    }

    @Override
    public boolean shouldCheckWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side)
    {
        return true;
    }

    public boolean hasFacingDirection() {
        return true;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);

        // set custom name from named stack
        if (stack.hasDisplayName()) {
            TileEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof TileInventory) {
                ((TileInventory) tileentity).setCustomName(stack.getDisplayName());
            }
        }

        if (!hasFacingDirection())
            return;

        RedstoneMachineLogicBase baseLogic = (RedstoneMachineLogicBase) worldIn.getTileEntity(pos);

        if (baseLogic == null) {
            return;
        }

        baseLogic.setFacingDirection(EnumFacing.getDirectionFromEntityLiving(pos, placer));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState();
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        if (!dropState)
            return super.getDrops(world, pos, state, fortune);

        RedstoneMachineLogicBase tile = (RedstoneMachineLogicBase) cachedTE;

        if (tile == null)
            return super.getDrops(world, pos, state, fortune);

        List<ItemStack> ret = new java.util.ArrayList<ItemStack>();

        Random rand = world instanceof World ? ((World) world).rand : RANDOM;

        Item item = this.getItemDropped(state, rand, fortune);
        if (item != null) {
            ret.add(tile.storeTileData(new ItemStack(item, 1, this.damageDropped(state))));
        }

        return ret;
    }

    /////////////////////////
    // BlockContainer Code //
    /////////////////////////

    protected boolean isInvalidNeighbor(World worldIn, BlockPos pos, EnumFacing facing) {
        return worldIn.getBlockState(pos.offset(facing)).getMaterial() == Material.CACTUS;
    }

    protected boolean hasInvalidNeighbor(World worldIn, BlockPos pos) {
        return this.isInvalidNeighbor(worldIn, pos, EnumFacing.NORTH) || this.isInvalidNeighbor(worldIn, pos, EnumFacing.SOUTH) || this.isInvalidNeighbor(worldIn, pos, EnumFacing.WEST) || this.isInvalidNeighbor(worldIn, pos, EnumFacing.EAST);
    }

    /**
     * Called on both Client and Server when World#addBlockEvent is called. On the Server, this may perform additional
     * changes to the world, like pistons replacing the block with an extended base. On the client, the update may
     * involve replacing tile entities, playing sounds, or performing other visual actions to reflect the server side
     * changes.
     */
    public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param) {
        super.eventReceived(state, worldIn, pos, id, param);
        TileEntity tileentity = worldIn.getTileEntity(pos);
        return tileentity != null && tileentity.receiveClientEvent(id, param);
    }

    /////////////////////////
    // BlockInventory Code //
    /////////////////////////

    // inventories usually need a tileEntity
    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nonnull
    @Override
    public abstract TileEntity createNewTileEntity(@Nonnull World worldIn, int meta);

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float clickX, float clickY, float clickZ) {
        if (player.isSneaking()) {
            return false;
        }

        if (!world.isRemote) {
            return this.openGui(player, world, pos);
        }

        return true;
    }

    @Override
    public void breakBlock(World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        TileEntity tileentity = worldIn.getTileEntity(pos);

        cachedTE = tileentity;

        if (tileentity instanceof TileInventory) {
            if (!dropState)
                InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory) tileentity);
            worldIn.updateComparatorOutputLevel(pos, this);
        }

        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }

    public enum DefaultTypes implements IEnumMeta, IStringSerializable {
        NORMAL;

        @Override
        public String getName() {
            return toString().toLowerCase();
        }

        @Override
        public int getMeta() {
            return ordinal();
        }
    }
}
