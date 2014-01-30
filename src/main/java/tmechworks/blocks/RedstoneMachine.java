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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import mantle.blocks.BlockUtils;
import mantle.blocks.iface.IFacingLogic;
import mantle.blocks.abstracts.InventoryBlock;
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
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class RedstoneMachine extends InventoryBlock
{
    public RedstoneMachine()
    {
        super(Material.field_151573_f);
        this.func_149647_a(TMechworksRegistry.Mechworks);
        func_149711_c(12);
        func_149672_a(field_149777_j);
    }

    @Override
    public int getLightValue (IBlockAccess world, int x, int y, int z)
    {
        if (world.getBlockMetadata(x, y, z) == 0 || world.getBlockMetadata(x, y, z) == 2)
        {
            TileEntity logic = world.func_147438_o(x, y, z);

            if (logic != null && logic instanceof DrawbridgeLogic)
            {
                if (((DrawbridgeLogic) logic).getStackInSlot(1) != null)
                {
                    ItemStack stack = ((DrawbridgeLogic) logic).getStackInSlot(1);
                    if (BlockUtils.getBlockFromItem(stack.getItem()) != null)
                    	return (BlockUtils.getBlockFromItem(((DrawbridgeLogic)logic).getStackInSlot(3).getItem())).func_149750_m();
                }
            }

            if (logic != null && logic instanceof AdvancedDrawbridgeLogic)
            {
                if (((AdvancedDrawbridgeLogic) logic).camoInventory.getCamoStack() != null)
                {
                    ItemStack stack = ((AdvancedDrawbridgeLogic) logic).camoInventory.getCamoStack();
                    if (BlockUtils.getBlockFromItem(stack.getItem()) != null)
                    	return (BlockUtils.getBlockFromItem(((AdvancedDrawbridgeLogic)logic).getStackInSlot(3).getItem())).func_149750_m();
                }
            }
        }
        return super.getLightValue(world, x, y, z);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int func_149720_d (IBlockAccess world, int x, int y, int z)
    {
        if (world.getBlockMetadata(x, y, z) == 0 && world.getBlockMetadata(x, y, z) == 2)
        {
            TileEntity logic = world.func_147438_o(x, y, z);

            if (logic != null && logic instanceof DrawbridgeLogic)
            {
                ItemStack stack = ((DrawbridgeLogic) logic).getStackInSlot(1);
                if (stack != null && BlockUtils.getBlockFromItem(stack.getItem()) != null && !ComparisonHelper.areEquivalent(stack.getItem(), this))
                    return BlockUtils.getBlockFromItem(stack.getItem()).func_149720_d(world, x, y, z);
            }
            else if (logic != null && logic instanceof AdvancedDrawbridgeLogic)
            {
                ItemStack stack = ((AdvancedDrawbridgeLogic) logic).camoInventory.getCamoStack();
                if (stack != null && BlockUtils.getBlockFromItem(stack.getItem()) != null &&! ComparisonHelper.areEquivalent(stack.getItem(),this))
                    return BlockUtils.getBlockFromItem(stack.getItem()).func_149720_d(world, x, y, z);
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
    public void func_149651_a (IIconRegister iconRegister)
    {
        String[] textureNames = getTextureNames();
        this.icons = new IIcon[textureNames.length];

        for (int i = 0; i < this.icons.length; ++i)
        {
            this.icons[i] = iconRegister.registerIcon("tmechworks:machines/" + textureNames[i]);
        }
    }

    @Override
    public IIcon func_149691_a (int side, int meta)
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
        TileEntity logic = world.func_147438_o(x, y, z);
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
                if (block != null && block.func_149686_d())
                    return block.func_149691_a(side, stack.getItemDamage());
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
                if (block != null && block.func_149686_d())
                    return block.func_149691_a(side, stack.getItemDamage());
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
    public void func_149666_a (Item b, CreativeTabs tab, List list)
    {
        for (int iter = 0; iter < 4; iter++)
        {
            list.add(new ItemStack(b, 1, iter));
        }
    }

    /* Redstone */
    public void onNeighborBlockChange (World world, int x, int y, int z, int neighborBlockID)
    {
        IActiveLogic logic = (IActiveLogic) world.func_147438_o(x, y, z);
        IFacingLogic facing = (IFacingLogic) logic;
        int direction = facing.getRenderDirection();
        int maxStrength = 0, tmpStrength = 0;
        boolean active = false;
        CoordTuple coord;

        for (int i = 0; i < 6; i++)
        {
            if (direction == i)
                continue;

            coord = directions.get(i);
            tmpStrength = world.getIndirectPowerLevelTo(x + coord.x, y + coord.y, z + coord.z, i);
            if (tmpStrength > maxStrength)
            {
                maxStrength = tmpStrength;
            }
        }
        if (maxStrength > 0)
        {
            active = true;
        }
        logic.setActive(active);
        if (logic instanceof DrawbridgeLogic)
            ((DrawbridgeLogic) logic).setMaximumExtension((byte) maxStrength);
    }

    /* Keep inventory */
    @Override
    public boolean removedByPlayer (World world, EntityPlayer player, int x, int y, int z)
    {
        player.addExhaustion(0.025F);
        int meta = world.getBlockMetadata(x, y, z);
        if (meta == 0)
        {
            ItemStack stack = new ItemStack(this, 1, meta);
            DrawbridgeLogic logic = (DrawbridgeLogic) world.func_147438_o(x, y, z);
            NBTTagCompound tag = new NBTTagCompound();

            boolean hasTag = false;
            ItemStack contents = logic.getStackInSlot(0);
            if (contents != null)
            {
                NBTTagCompound contentTag = new NBTTagCompound();
                contents.writeToNBT(contentTag);
                tag.setTag("Contents", contentTag);
                hasTag = true;
            }

            ItemStack camo = logic.getStackInSlot(1);
            if (camo != null)
            {
                NBTTagCompound camoTag = new NBTTagCompound();
                camo.writeToNBT(camoTag);
                tag.setTag("Camoflauge", camoTag);
                hasTag = true;
            }

            if (logic.getPlacementDirection() != 4)
            {
                tag.setByte("Placement", logic.getPlacementDirection());
                hasTag = true;
            }
            if (hasTag == true)
                stack.setTagCompound(tag);

            dropDrawbridgeLogic(world, x, y, z, stack);
        }

        return WorldHelper.setBlockToAirBool(world, x, y, z);
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
            entityitem.field_145804_b = 10;
            world.spawnEntityInWorld(entityitem);
        }
    }

    @Override
    public void func_149636_a (World world, EntityPlayer player, int x, int y, int z, int meta)
    {
        if (meta != 0)
            super.func_149636_a(world, player, x, y, z, meta);
    }

    @Override
    public void func_149689_a (World world, int x, int y, int z, EntityLivingBase living, ItemStack stack)
    {
        super.func_149689_a(world, x, y, z, living, stack);
        if (stack.hasTagCompound())
        {
            DrawbridgeLogic logic = (DrawbridgeLogic) world.func_147438_o(x, y, z);
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
    public TileEntity func_149915_a (World var1, int metadata)
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
