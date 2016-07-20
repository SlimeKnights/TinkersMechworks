package slimeknights.tmechworks.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.IStringSerializable;
import slimeknights.mantle.block.EnumBlock;
import slimeknights.tmechworks.common.MechworksContent;

import java.util.Locale;

public class Metal extends EnumBlock<Metal.MetalTypes>
{
    public static final PropertyEnum<MetalTypes> TYPE = PropertyEnum.create("type", MetalTypes.class);

    public Metal ()
    {
        super(Material.IRON, TYPE, MetalTypes.class);

        setHardness(5F);
        setResistance(10F);
        setHarvestLevel("pickaxe", -1);
        setCreativeTab(MechworksContent.tabMechworks);
    }

    public enum MetalTypes implements IStringSerializable, EnumBlock.IEnumMeta
    {
        ALUMINUM, COPPER;

        @Override public String getName ()
        {
            return this.toString().toLowerCase(Locale.US);
        }

        @Override public int getMeta ()
        {
            return ordinal();
        }
    }
}
