package tmechworks.items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import tmechworks.blocks.logic.SignalBusLogic;
import tmechworks.blocks.logic.SignalTerminalLogic;
import tmechworks.lib.TMechworksRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SpoolOfWire extends Item
{
    public String textureName = "spoolwire";
    public String unlocalizedName = "spoolwire";
    public String folder = "logic/";
    public IIcon icon;

    public SpoolOfWire()
    {
        super();
        this.setCreativeTab(TMechworksRegistry.Mechworks);
        this.maxStackSize = 1;
        this.setMaxDamage(256);
        this.setHasSubtypes(true);
    }

    @Override
    public ItemStack getContainerItem (ItemStack itemStack)
    {
        return new ItemStack(this, 1, this.getMaxDamage());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        switch (stack.getItemDamage())
        {
        case 0:
            list.add(StatCollector.translateToLocal("tooltip.spoolofwire"));
            break;
        }
    }

    public String getUnlocalizedName (ItemStack stack)
    {
        return "item." + unlocalizedName;
    }

    @Override
    public boolean onItemUse (ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int par7, float par8, float par9, float par10)
    {
        TileEntity te = world.getTileEntity(x, y, z);
        NBTTagCompound data = itemstack.stackTagCompound;
        NBTTagCompound spoolData = null;

        if (world.isRemote)
        {
            return false;
        }

        if (data == null)
        {
            data = new NBTTagCompound();
            itemstack.stackTagCompound = data;
        }
        if (te != null && te instanceof SignalBusLogic)
        {
            if (data.hasKey("spoolWireData"))
            {
                spoolData = data.getCompoundTag("spoolWireData");

                int targetDim = spoolData.getInteger("targetDim");
                int targetX = spoolData.getInteger("targetX");
                int targetY = spoolData.getInteger("targetY");
                int targetZ = spoolData.getInteger("targetZ");

                int calc = Math.abs(targetX - x) + Math.abs(targetY - y) + Math.abs(targetZ - z);
                if ((itemstack.getMaxDamage() - itemstack.getItemDamage()) < calc)
                {
                    return false;
                }
                if (targetDim == world.provider.dimensionId && calc < 16)
                {
                    ((SignalBusLogic) te).registerTerminal(world, targetX, targetY, targetZ, true);
                    data.removeTag("spoolWireData");

                    itemstack.damageItem(calc, player);
                    return true;
                }
            }

            return false;
        }
        if (te != null && te instanceof SignalTerminalLogic)
        {
            data = itemstack.stackTagCompound;
            spoolData = null;
            if (data.hasKey("spoolWireData"))
            {
                data.removeTag("spoolWireData");
            }
            spoolData = new NBTTagCompound();

            spoolData.setInteger("targetDim", world.provider.dimensionId);
            spoolData.setInteger("targetX", x);
            spoolData.setInteger("targetY", y);
            spoolData.setInteger("targetZ", z);

            data.setTag("spoolWireData", spoolData);

            return true;
        }

        return false;
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage (int meta)
    {
        return icon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons (IIconRegister iconRegister)
    {
        this.icon = iconRegister.registerIcon("tmechworks:" + folder + textureName);
    }

    public void getSubItems (Item b, CreativeTabs tab, List list)
    {
        list.add(new ItemStack(b, 1, 0));
    }
}
