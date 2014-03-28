package tmechworks.common;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
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
                MechContent.proxyItem_blankPattern, 1, 1), 'r', new ItemStack(Item.redstone), 'd', new ItemStack(Block.dispenser)));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MechContent.redstoneMachine, 1, 0), "aca", "#d#", "#r#", '#', "ingotBronze", 'a', "ingotAluminumBrass", 'c', new ItemStack(
                MechContent.proxyItem_blankPattern, 1, 2), 'r', new ItemStack(Item.redstone), 'd', new ItemStack(Block.dispenser)));

        // Igniter Recipes
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MechContent.redstoneMachine, 1, 1), "aca", "#d#", "#r#", '#', "ingotBronze", 'a', "ingotAluminumBrass", 'c', new ItemStack(
                MechContent.proxyItem_largePlate, 1, 7), 'r', new ItemStack(Item.redstone), 'd', new ItemStack(Item.flintAndSteel)));

        // Dynamo Recipes

        // Advanced Drawbridge Recipes
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MechContent.redstoneMachine, 1, 2), " c ", "rdr", " a ", 'a', "ingotAluminumBrass", 'c', new ItemStack(
                MechContent.proxyItem_blankPattern, 1, 1), 'r', new ItemStack(Item.redstone), 'd', new ItemStack(MechContent.redstoneMachine, 1, 0)));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MechContent.redstoneMachine, 1, 2), " c ", "rdr", " a ", 'a', "ingotAluminumBrass", 'c', new ItemStack(
                MechContent.proxyItem_blankPattern, 1, 2), 'r', new ItemStack(Item.redstone), 'd', new ItemStack(MechContent.redstoneMachine, 1, 0)));
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(MechContent.redstoneMachine, 1, 3), "blockCobalt", "blockArdite", new ItemStack(Block.blockRedstone), new ItemStack(
                MechContent.redstoneMachine, 1, 0)));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MechContent.signalBus.blockID, 1, 0), "www", "sss", 'w', MechContent.lengthWire, 's', new ItemStack(Block.stoneSingleSlab, 1,
                OreDictionary.WILDCARD_VALUE)));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MechContent.signalTerminal.blockID, 1, 0), "b", "g", "b", 'b', new ItemStack(MechContent.signalBus.blockID, 1, 0), 'g', new ItemStack(
                Block.glass, 1, OreDictionary.WILDCARD_VALUE)));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MechContent.lengthWire, 8), "a", "a", "a", 'a', "ingotAluminumBrass"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MechContent.spoolWire, 1, 256 - 8), "www", "wrw", "www", 'w', MechContent.lengthWire, 'r', "stoneRod"));
        GameRegistry.addRecipe(new SpoolRepairRecipe(new ItemStack(MechContent.spoolWire, 1, 256), new ItemStack(MechContent.lengthWire, 1)));
        
        //Filter frame recipes 
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(MechContent.filter, 1, 0), "xxx", "x x", "xxx", 'x', "stickWood"));
        //Auto-populate crafting recipes for mesh filters.
        for(int i = 0; i < 8; i ++)
        {
        	if(MechContent.filter.subFilters[i] != null) {
            	if(MechContent.filter.subFilters[i].getAssociatedItem() != null) {
            		//Crafting
                    GameRegistry.addShapelessRecipe(new ItemStack(MechContent.filter, 1, i),
                    		new ItemStack(MechContent.filter, 1, 0), MechContent.filter.subFilters[i].getAssociatedItem().copy());
            	}
        	}
        }

    }
}
