package tmechworks.common;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import cpw.mods.fml.common.registry.GameRegistry;

public class MechRecipes
{
    public static void registerAllTheThings ()
    {
        // Drawbridge Recipes
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MechContent.redstoneMachine, 1, 0), "aca", "#d#", "#r#", '#', "ingotBronze", 'a', "ingotAluminumBrass", 'c', new ItemStack(
                MechContent.proxyItem_blankPattern, 1, 1), 'r', new ItemStack(Items.redstone), 'd', new ItemStack(Blocks.dispenser)));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MechContent.redstoneMachine, 1, 0), "aca", "#d#", "#r#", '#', "ingotBronze", 'a', "ingotAluminumBrass", 'c', new ItemStack(
                MechContent.proxyItem_blankPattern, 1, 2), 'r', new ItemStack(Items.redstone), 'd', new ItemStack(Blocks.dispenser)));

        // Igniter Recipes
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MechContent.redstoneMachine, 1, 1), "aca", "#d#", "#r#", '#', "ingotBronze", 'a', "ingotAluminumBrass", 'c', new ItemStack(
                MechContent.proxyItem_largePlate, 1, 7), 'r', new ItemStack(Items.redstone), 'd', new ItemStack(Items.flint_and_steel)));

        // Dynamo Recipes

        // Advanced Drawbridge Recipes
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MechContent.redstoneMachine, 1, 2), " c ", "rdr", " a ", 'a', "ingotAluminumBrass", 'c', new ItemStack(
                MechContent.proxyItem_blankPattern, 1, 1), 'r', new ItemStack(Items.redstone), 'd', new ItemStack(MechContent.redstoneMachine, 1, 0)));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MechContent.redstoneMachine, 1, 2), " c ", "rdr", " a ", 'a', "ingotAluminumBrass", 'c', new ItemStack(
                MechContent.proxyItem_blankPattern, 1, 2), 'r', new ItemStack(Items.redstone), 'd', new ItemStack(MechContent.redstoneMachine, 1, 0)));
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(MechContent.redstoneMachine, 1, 3), "blockCobalt", "blockArdite", new ItemStack(Blocks.redstone_block), new ItemStack(
                MechContent.redstoneMachine, 1, 0)));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MechContent.signalBus, 1, 0), "www", "sss", 'w', MechContent.lengthWire, 's', new ItemStack(Blocks.stone_slab, 1,
                OreDictionary.WILDCARD_VALUE)));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MechContent.signalTerminal, 1, 0), "b", "g", "b", 'b', new ItemStack(MechContent.signalBus, 1, 0), 'g', new ItemStack(Blocks.glass, 1,
                OreDictionary.WILDCARD_VALUE)));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MechContent.lengthWire, 8), "a", "a", "a", 'a', "ingotAluminumBrass"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MechContent.spoolWire, 1, 256 - 8), "www", "wrw", "www", 'w', MechContent.lengthWire, 'r', "stoneRod"));
        GameRegistry.addRecipe(new SpoolRepairRecipe(new ItemStack(MechContent.spoolWire, 1, 256), new ItemStack(MechContent.lengthWire, 1)));

    }
}
