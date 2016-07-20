package slimeknights.tmechworks.common;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class MechRecipes
{
    protected static void register ()
    {
        registerMetalRecipes("Aluminum", MechworksContent.ingotAluminum, MechworksContent.nuggetAluminum, MechworksContent.blockAluminum);
        registerMetalRecipes("Copper", MechworksContent.ingotCopper, MechworksContent.nuggetCopper, MechworksContent.blockCopper);
    }

    private static void registerMetalRecipes (String oreString, ItemStack ingot, ItemStack nugget, ItemStack block)
    {
        if (ingot == null)
            return;

        oredict(ingot, "ingot" + oreString);

        if (nugget != null)
        {
            oredict(nugget, "nugget" + oreString);

            registerCompressionRecipe(nugget, ingot, "nugget" + oreString, "ingot" + oreString);
        }

        if (block != null)
        {
            oredict(block, "block" + oreString);

            registerCompressionRecipe(ingot, block, "ingot" + oreString, "block" + oreString);
        }
    }

    private static void registerCompressionRecipe (ItemStack small, ItemStack big, String oreSmall, String oreBig)
    {
        // Small -> Big
        GameRegistry.addRecipe(new ShapedOreRecipe(big, "###", "###", "###", '#', oreSmall));

        small = small.copy();
        small.stackSize = 9;

        // Big -> Small
        GameRegistry.addRecipe(new ShapelessOreRecipe(small, oreBig));
    }

    public static void oredict (Item item, String... name)
    {
        oredict(item, OreDictionary.WILDCARD_VALUE, name);
    }

    public static void oredict (Block block, String... name)
    {
        oredict(block, OreDictionary.WILDCARD_VALUE, name);
    }

    public static void oredict (Item item, int meta, String... name)
    {
        oredict(new ItemStack(item, 1, meta), name);
    }

    public static void oredict (Block block, int meta, String... name)
    {
        oredict(new ItemStack(block, 1, meta), name);
    }

    public static void oredict (ItemStack stack, String... names)
    {
        if (stack != null && stack.getItem() != null)
        {
            for (String name : names)
            {
                OreDictionary.registerOre(name, stack);
            }
        }
    }
}
