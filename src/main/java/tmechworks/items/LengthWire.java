package tmechworks.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import tmechworks.lib.TMechworksRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class LengthWire extends Item
{
    public String[] textureNames = new String[] { "lengthwire" };
    public String[] unlocalizedNames = new String[] { "lengthwire" };
    public String folder = "logic/";
    public IIcon[] icons;

    public LengthWire()
    {
        super();
        this.setCreativeTab(TMechworksRegistry.Mechworks);
        this.maxStackSize = 64;
        this.setHasSubtypes(false);
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage (int meta)
    {
        int arr = MathHelper.clamp_int(meta, 0, unlocalizedNames.length);
        return icons[arr];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons (IIconRegister iconRegister)
    {
        this.icons = new IIcon[textureNames.length];

        for (int i = 0; i < this.icons.length; ++i)
        {
            if (!(textureNames[i].equals("")))
                this.icons[i] = iconRegister.registerIcon("tmechworks:" + folder + textureNames[i]);
        }
    }

    public void getSubItems (Item b, CreativeTabs tab, List list)
    {
        for (int i = 0; i < unlocalizedNames.length; i++)
            if (!(textureNames[i].equals("")))
                list.add(new ItemStack(b, 1, i));
    }
}
