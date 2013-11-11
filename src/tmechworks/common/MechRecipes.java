package tmechworks.common;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import tmechworks.common.SpoolRepairRecipe;
import cpw.mods.fml.common.registry.GameRegistry;

public class MechRecipes {

	public static void registerAllTheThings ()
	{
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MechContent.signalBus.blockID, 1, 0), "www", "sss", 'w', MechContent.lengthWire, 's', new ItemStack(Block.stoneSingleSlab, 1, OreDictionary.WILDCARD_VALUE)));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MechContent.signalTerminal.blockID, 1, 0), "b", "g", "b", 'b', new ItemStack(MechContent.signalBus.blockID, 1, 0), 'g', new ItemStack(Block.glass, 1, OreDictionary.WILDCARD_VALUE)));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MechContent.lengthWire, 8), "a", "a", "a", 'a', "ingotAluminumBrass"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MechContent.spoolWire, 1, 256 - 8), "www", "wrw", "www", 'w', MechContent.lengthWire, 'r', "stoneRod"));
        GameRegistry.addRecipe(new SpoolRepairRecipe(new ItemStack(MechContent.spoolWire, 1, 256), new ItemStack(MechContent.lengthWire, 1)));

	}
}
