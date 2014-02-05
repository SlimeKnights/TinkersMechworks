package tmechworks.blocks;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import mantle.blocks.BlockUtils;
import mantle.blocks.iface.IFacingLogic;
import mantle.blocks.abstracts.InventoryBlock;
import mantle.blocks.abstracts.InventoryLogic;
import mantle.blocks.iface.IActiveLogic;
import mantle.common.ComparisonHelper;
import mantle.world.CoordTuple;
import mantle.world.WorldHelper;
import tmechworks.TMechworks;
import tmechworks.blocks.logic.AdvancedDrawbridgeLogic;
import tmechworks.blocks.logic.DrawbridgeLogic;
import tmechworks.blocks.logic.FirestarterLogic;
import tmechworks.client.block.MachineRender;
import tmechworks.lib.TMechworksRegistry;
import tmechworks.lib.blocks.IDrawbridgeLogicBase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class RedstoneMachine extends InventoryBlock
{
    public RedstoneMachine()
    {
        super(Material.iron);
        this.setCreativeTab(TMechworksRegistry.Mechworks);
        setHardness(12);
        setStepSound(soundTypeMetal);
    }

    @Override
    public int getLightValue (IBlockAccess world, int x, int y, int z)
    {
        if (world.getBlockMetadata(x, y, z) == 0 || world.getBlockMetadata(x, y, z) == 2)
        {
            TileEntity logic = world.getTileEntity(x, y, z);

            if (logic != null && logic instanceof DrawbridgeLogic)
            {
                if (((DrawbridgeLogic) logic).getStackInSlot(1) != null)
                {
                    ItemStack stack = ((DrawbridgeLogic) logic).getStackInSlot(1);
                    if (BlockUtils.getBlockFromItem(stack.getItem()) != null)
                        return (BlockUtils.getBlockFromItem(((DrawbridgeLogic) logic).getStackInSlot(3).getItem())).getLightValue();
                }
            }

            if (logic != null && logic instanceof AdvancedDrawbridgeLogic)
            {
                if (((AdvancedDrawbridgeLogic) logic).camoInventory.getCamoStack() != null)
                {
                    ItemStack stack = ((AdvancedDrawbridgeLogic) logic).camoInventory.getCamoStack();
                    if (BlockUtils.getBlockFromItem(stack.getItem()) != null)
                        return (BlockUtils.getBlockFromItem(((AdvancedDrawbridgeLogic) logic).getStackInSlot(3).getItem())).getLightValue();
                }
            }
        }
        return super.getLightValue(world, x, y, z);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int colorMultiplier (IBlockAccess world, int x, int y, int z)
    {
        if (world.getBlockMetadata(x, y, z) == 0 && world.getBlockMetadata(x, y, z) == 2)
        {
            TileEntity logic = world.getTileEntity(x, y, z);

            if (logic != null && logic instanceof DrawbridgeLogic)
            {
                ItemStack stack = ((DrawbridgeLogic) logic).getStackInSlot(1);
                if (stack != null && BlockUtils.getBlockFromItem(stack.getItem()) != null && !ComparisonHelper.areEquivalent(stack.getItem(), this))
                    return BlockUtils.getBlockFromItem(stack.getItem()).colorMultiplier(world, x, y, z);
            }
            else if (logic != null && logic instanceof AdvancedDrawbridgeLogic)
            {
                ItemStack stack = ((AdvancedDrawbridgeLogic) logic).camoInventory.getCamoStack();
                if (stack != null && BlockUtils.getBlockFromItem(stack.getItem()) != null && !ComparisonHelper.areEquivalent(stack.getItem(), this))
                    return BlockUtils.getBlockFromItem(stack.getItem()).colorMultiplier(world, x, y, z);
            }
        }

        return 0xffffff;
    }

    @Override
    public TileEntity createTileEntity (World world, int metadata)
    {
        switch (metadata)
        {
        case 0:
            return new DrawbridgeLogic();
        case 1:
            return new FirestarterLogic();
        case 2:
            return new AdvancedDrawbridgeLogic();
        case 3:
            DrawbridgeLogic logic = new DrawbridgeLogic();
            logic.setMaximumExtension((byte) 64);
            return logic;
        default:
            return null;
        }
    }

    @Override
    public Integer getGui (World world, int x, int y, int z, EntityPlayer entityplayer)
    {
        int meta = world.getBlockMetadata(x, y, z);
        switch (meta)
        {
        case 0:
        case 3:
            return TMechworks.proxy.drawbridgeID;
        case 2:
            return TMechworks.proxy.advDrawbridgeID;
        }
        return null;
    }

    @Override
    public Object getModInstance ()
    {
        return TMechworks.instance;
    }

    /* Rendering */

    @Override
    public String[] getTextureNames ()
    {
        String[] textureNames = { "drawbridge_top", "drawbridge_side", "drawbridge_bottom", "drawbridge_top_face", "drawbridge_side_face", "drawbridge_bottom_face", "firestarter_top",
                "firestarter_side", "firestarter_bottom", "drawbridge_side_advanced", "drawbridge_top_extended", "drawbridge_side_extended", "drawbridge_bottom_extended",
                "drawbridge_top_face_extended", "drawbridge_side_face_extended", "drawbridge_bottom_face_extended" };

        return textureNames;
    }

    @Override
    public void registerBlockIcons (IIconRegister iconRegister)
    {
        String[] textureNames = getTextureNames();
        this.icons = new IIcon[textureNames.length];

        for (int i = 0; i < this.icons.length; ++i)
        {
            this.icons[i] = iconRegister.registerIcon("tmechworks:machines/" + textureNames[i]);
        }
    }

    @Override
    public IIcon getIcon (int side, int meta)
    {
        if (meta == 0)
        {
            if (side == 5)
                return icons[5];
            return icons[getTextureIndex(side)];
        }
        if (meta == 1)
        {
            return icons[getTextureIndex(side) + 6];
        }
        if (meta == 2)
        {
            if (side == 5)
                return icons[5];
            return icons[getTextureIndex(side, true)];
        }
        if (meta == 3)
        {
            if (side == 5)
                return icons[13];
            return icons[getTextureIndex(side) + 10];
        }
        return icons[0];
    }

    public IIcon getBlockTexture (IBlockAccess world, int x, int y, int z, int side)
    {
        TileEntity logic = world.getTileEntity(x, y, z);
        short direction = (logic instanceof IFacingLogic) ? ((IFacingLogic) logic).getRenderDirection() : 0;
        int meta = world.getBlockMetadata(x, y, z);
        if (meta == 0 || meta == 3)
        {
            int offset = meta == 0 ? 0 : 10;
            DrawbridgeLogic drawbridge = (DrawbridgeLogic) logic;
            ItemStack stack = drawbridge.getStackInSlot(1);
            if (stack != null)
            {
                Block block = BlockUtils.getBlockFromItem(stack.getItem());
                if (block != null && block.renderAsNormalBlock())
                    return block.getIcon(side, stack.getItemDamage());
            }
            if (side == direction)
            {
                return icons[getTextureIndex(side) + 3 + offset];
            }
            else
            {
                return icons[getTextureIndex(side) + offset];
            }
        }

        if (meta == 2)
        {
            AdvancedDrawbridgeLogic drawbridge = (AdvancedDrawbridgeLogic) logic;
            ItemStack stack = drawbridge.camoInventory.getCamoStack();
            if (stack != null)
            {
                Block block = BlockUtils.getBlockFromItem(stack.getItem());
                if (block != null && block.renderAsNormalBlock())
                    return block.getIcon(side, stack.getItemDamage());
            }
            if (side == direction)
            {
                return icons[getTextureIndex(side) + 3];
            }
            else
            {
                return icons[getTextureIndex(side, true)];
            }
        }

        if (meta == 1)
        {
            if (side == direction)
            {
                return icons[6];
            }
            else if (side / 2 == direction / 2)
            {
                return icons[8];
            }
            return icons[7];
        }
        return icons[0];
    }

    public int getTextureIndex (int side)
    {
        return getTextureIndex(side, false);
    }

    public int getTextureIndex (int side, boolean alt)
    {
        if (side == 0)
            return 2;
        if (side == 1)
            return 0;

        return alt ? 9 : 1;
    }

    public int getRenderType ()
    {
        return MachineRender.model;
    }

    public boolean isFireSource (World world, int x, int y, int z, int metadata, ForgeDirection side)
    {
        if (metadata == 1)
            return side == ForgeDirection.UP;
        return false;
    }

    @Override
    public void getSubBlocks (Item b, CreativeTabs tab, List list)
    {
        for (int iter = 0; iter < 4; iter++)
        {
            list.add(new ItemStack(b, 1, iter));
        }
    }

    /* Redstone */
    public void onNeighborBlockChange (World world, int x, int y, int z, int neighborBlockID)
    {
        IActiveLogic logic = (IActiveLogic) world.getTileEntity(x, y, z);
        IFacingLogic facing = (IFacingLogic) logic;
        int direction = facing.getRenderDirection();
        boolean active = false;
        CoordTuple coord;
        for (int i = 0; i < 6; i++)
        {
            if (direction == i)
                continue;

            CoordTuple coord2 = directions.get(i);
            if (this.getIndirectPowerLevelTo(world, x + coord2.x, y + coord2.y, z + coord2.z, i) > 0 || activeRedstone(world, coord2.x, y + coord2.y, z + coord2.z))
            {
                active = true;
                break;
            }
        }
        logic.setActive(active);
    }

    public int getIndirectPowerLevelTo (World world, int x, int y, int z, int side)
    {
        if (world.getBlock(x, y, z).isBlockNormalCube())
        {
            return world.getBlockPowerInput(x, y, z);
        }
        else
        {
            Block i1 = world.getBlock(x, y, z);
            return i1 == null ? null : i1.isProvidingWeakPower(world, x, y, z, side);
        }
    }

    boolean activeRedstone (World world, int x, int y, int z)
    {
        Block wire = world.getBlock(x, y, z);
        if (wire != null && wire == Blocks.redstone_wire)
            return world.getBlockMetadata(x, y, z) > 0;

        return false;
    }

    /* Keep inventory */
    @Override
    public boolean removedByPlayer (World world, EntityPlayer player, int x, int y, int z)
    {
        player.addExhaustion(0.025F);
        int meta = world.getBlockMetadata(x, y, z);
        if (meta < 4 && meta != 1)
        {
            ItemStack stack = getDrawbridgeBlock(world, x, y, z, meta);
            dropDrawbridgeLogic(world, x, y, z, stack);
        }

        return WorldHelper.setBlockToAirBool(world, x, y, z);
    }

    private ItemStack getDrawbridgeBlock (World world, int x, int y, int z, int meta)
    {
        ItemStack stack = new ItemStack(this, 1, meta);
        InventoryLogic logic = (InventoryLogic) world.getTileEntity(x, y, z);
        NBTTagCompound tag = new NBTTagCompound();
        ItemStack camo = null;

        boolean hasTag = false;
        if (meta == 0 || meta == 3)
        {
            ItemStack contents = logic.getStackInSlot(0);
            if (contents != null)
            {
                NBTTagCompound contentTag = new NBTTagCompound();
                contents.writeToNBT(contentTag);
                tag.setTag("Contents", contentTag);
                hasTag = true;
            }

            camo = logic.getStackInSlot(1);
        }
        else if (meta == 2)
        {
            AdvancedDrawbridgeLogic advDrawbridge = (AdvancedDrawbridgeLogic) logic;
            camo = advDrawbridge.camoInventory.getCamoStack();
            for (int i = 1; i <= 16; i++)
            {
                ItemStack slot = logic.getStackInSlot(i - 1);
                if (slot != null)
                {
                    NBTTagCompound contentTag = new NBTTagCompound();
                    slot.writeToNBT(contentTag);
                    tag.setTag("Slot" + i, contentTag);
                    hasTag = true;
                }
            }
        }

        if (camo != null)
        {
            NBTTagCompound camoTag = new NBTTagCompound();
            camo.writeToNBT(camoTag);
            tag.setTag("Camoflauge", camoTag);
            hasTag = true;
        }

        IDrawbridgeLogicBase drawbridge = (IDrawbridgeLogicBase) logic;
        if (drawbridge.getPlacementDirection() != 4)
        {
            tag.setByte("Placement", drawbridge.getPlacementDirection());
            hasTag = true;
        }
        if (hasTag == true)
            stack.setTagCompound(tag);

        return stack;
    }

    protected void dropDrawbridgeLogic (World world, int x, int y, int z, ItemStack stack)
    {
        if (!world.isRemote && world.getGameRules().getGameRuleBooleanValue("doTileDrops"))
        {
            float f = 0.7F;
            double d0 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
            double d1 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
            double d2 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
            EntityItem entityitem = new EntityItem(world, (double) x + d0, (double) y + d1, (double) z + d2, stack);
            entityitem.delayBeforeCanPickup = 10;
            world.spawnEntityInWorld(entityitem);
        }
    }

    @Override
    public ItemStack getPickBlock (MovingObjectPosition target, World world, int x, int y, int z)
    {
        Item id = getItem(world, x, y, z);

        int meta = getDamageValue(world, x, y, z);
        if (meta != 1 && meta < 4)
        {
            return getDrawbridgeBlock(world, x, y, z, meta);
        }
        return new ItemStack(id, 1, meta);
    }

    @Override
    public void onBlockPlacedBy (World world, int x, int y, int z, EntityLivingBase living, ItemStack stack)
    {
        super.onBlockPlacedBy(world, x, y, z, living, stack);
        if (stack.hasTagCompound())
        {
            int meta = stack.getItemDamage();
            if (meta == 0 || meta == 3)
            {
                DrawbridgeLogic logic = (DrawbridgeLogic) world.getTileEntity(x, y, z);
                NBTTagCompound contentTag = stack.getTagCompound().getCompoundTag("Contents");
                if (contentTag != null)
                {
                    ItemStack contents = ItemStack.loadItemStackFromNBT(contentTag);
                    logic.setInventorySlotContents(0, contents);
                }

                NBTTagCompound camoTag = stack.getTagCompound().getCompoundTag("Camoflauge");
                if (camoTag != null)
                {
                    ItemStack camoflauge = ItemStack.loadItemStackFromNBT(camoTag);
                    logic.setInventorySlotContents(1, camoflauge);
                }

                if (stack.getTagCompound().hasKey("Placement"))
                {
                    logic.setPlacementDirection(stack.getTagCompound().getByte("Placement"));
                }
            }
            else if (meta == 2)
            {
                AdvancedDrawbridgeLogic logic = (AdvancedDrawbridgeLogic) world.getTileEntity(x, y, z);
                for (int i = 1; i <= 16; i++)
                {
                    NBTTagCompound contentTag = stack.getTagCompound().getCompoundTag("Slot" + i);
                    if (contentTag != null)
                    {
                        ItemStack contents = ItemStack.loadItemStackFromNBT(contentTag);
                        logic.setInventorySlotContents(i - 1, contents);
                    }
                }

                NBTTagCompound camoTag = stack.getTagCompound().getCompoundTag("Camoflauge");
                if (camoTag != null)
                {
                    ItemStack camoflauge = ItemStack.loadItemStackFromNBT(camoTag);
                    logic.camoInventory.setInventorySlotContents(0, camoflauge);
                }

                if (stack.getTagCompound().hasKey("Placement"))
                {
                    logic.setPlacementDirection(stack.getTagCompound().getByte("Placement"));
                }
            }
        }
    }

    @Override
    public void harvestBlock (World world, EntityPlayer player, int x, int y, int z, int meta)
    {
        if (meta != 0)
            super.harvestBlock(world, player, x, y, z, meta);
    }

    /* Redstone connections */

    public boolean canConnectRedstone (IBlockAccess world, int x, int y, int z, int side)
    {
        return false;
    }

    static ArrayList<CoordTuple> directions = new ArrayList<CoordTuple>(6);

    static
    {
        directions.add(new CoordTuple(0, -1, 0));
        directions.add(new CoordTuple(0, 1, 0));
        directions.add(new CoordTuple(0, 0, -1));
        directions.add(new CoordTuple(0, 0, 1));
        directions.add(new CoordTuple(-1, 0, 0));
        directions.add(new CoordTuple(1, 0, 0));
    }

    @Override
    public TileEntity createNewTileEntity (World var1, int metadata)
    {
        switch (metadata)
        {
        case 0:
            return new DrawbridgeLogic();
        case 1:
            return new FirestarterLogic();
        case 2:
            return new AdvancedDrawbridgeLogic();
        case 3:
            DrawbridgeLogic logic = new DrawbridgeLogic();
            logic.setMaximumExtension((byte) 64);
            return logic;
        default:
            return null;
        }
    }
}
