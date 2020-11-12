package slimeknights.tmechworks.data;

import net.minecraft.data.*;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import slimeknights.tmechworks.library.Util;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

import static slimeknights.tmechworks.common.MechworksContent.Blocks.*;
import static slimeknights.tmechworks.common.MechworksContent.Items.*;
import static slimeknights.tmechworks.common.MechworksTags.Items.*;
import static net.minecraftforge.common.Tags.Items.*;

public class Recipes extends RecipeProvider implements IConditionBuilder {
    private static final int standardSmeltingTime = 200;
    private static final int standardBlastingTime = standardSmeltingTime / 2;

    public Recipes(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void registerRecipes(@Nonnull Consumer<IFinishedRecipe> out) {
        // Metals
        registerMetal(out, aluminum_ore, aluminum_nugget, aluminum_ingot, aluminum_block);
        registerMetal(out, copper_ore, copper_nugget, copper_ingot, copper_block);

        wrap(ShapelessRecipeBuilder.shapelessRecipe(book).addIngredient(Items.BOOK).addIngredient(upgrade_blank), Items.BOOK).build(out);

        // Machines
        wrap(ShapedRecipeBuilder.shapedRecipe(drawbridge)
                .setGroup(Util.prefix("machines"))
                .key('R', DUSTS_REDSTONE)
                .key('A', INGOTS_ALUMINUM)
                .key('U', upgrade_drawbridge_distance)
                .key('P', Items.PISTON)
                .patternLine("RAR")
                .patternLine("PUP")
                .patternLine("RAR"), copper_ingot)
                .build(out);

        wrap(ShapedRecipeBuilder.shapedRecipe(firestarter)
                .setGroup(Util.prefix("machines"))
                .key('C', INGOTS_COPPER)
                .key('R', DUSTS_REDSTONE)
                .key('A', INGOTS_ALUMINUM)
                .key('B', upgrade_blank)
                .key('F', Items.FLINT_AND_STEEL)
                .patternLine("RAR")
                .patternLine("FBF")
                .patternLine("ACA"), copper_ingot)
                .build(out);

        wrap(ShapedRecipeBuilder.shapedRecipe(upgrade_blank)
                .setGroup(Util.prefix("upgrades"))
                .key('I', INGOTS_IRON)
                .key('C', INGOTS_COPPER)
                .key('R', DUSTS_REDSTONE)
                .key('A', INGOTS_ALUMINUM)
                .patternLine("IAI")
                .patternLine("RIR")
                .patternLine("ACA"), Items.REDSTONE)
                .build(out);

        wrap(ShapedRecipeBuilder.shapedRecipe(upgrade_drawbridge_advanced)
                .setGroup(Util.prefix("upgrades"))
                .key('B', upgrade_blank)
                .key('H', Items.HOPPER)
                .key('C', INGOTS_COPPER)
                .key('A', INGOTS_ALUMINUM)
                .key('L', STORAGE_BLOCKS_LAPIS)
                .key('S', CHESTS_WOODEN)
                .patternLine("CHC")
                .patternLine("LBL")
                .patternLine("ASA"), upgrade_blank)
                .build(out);

        wrap(ShapedRecipeBuilder.shapedRecipe(upgrade_drawbridge_distance)
                .setGroup(Util.prefix("upgrades"))
                .key('B', upgrade_blank)
                .key('P', Items.PISTON)
                .key('C', INGOTS_COPPER)
                .key('A', INGOTS_ALUMINUM)
                .patternLine("ACA")
                .patternLine("PBP")
                .patternLine(" A "), upgrade_blank)
                .build(out);

        wrap(ShapedRecipeBuilder.shapedRecipe(upgrade_speed)
                .setGroup(Util.prefix("upgrades"))
                .key('B', upgrade_blank)
                .key('S', Items.SUGAR)
                .patternLine(" S ")
                .patternLine("SBS")
                .patternLine(" S "), upgrade_blank)
                .build(out);
    }

    private void registerMetal(@Nonnull Consumer<IFinishedRecipe> out, IItemProvider ore, IItemProvider nugget, IItemProvider ingot, IItemProvider storageBlock) {
        String format = Util.prefix("%s_from_%s");

        String nuggetName = nugget.asItem().getRegistryName().getPath();
        String ingotName = ingot.asItem().getRegistryName().getPath();
        String storageBlockName = storageBlock.asItem().getRegistryName().getPath();

        // Smelting
        wrap(CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(ore), ingot, 1F, standardSmeltingTime), ore).build(out, String.format(format, ingotName, "smelting"));
        wrap(CookingRecipeBuilder.blastingRecipe(Ingredient.fromItems(ore), ingot, 1F, standardBlastingTime), ore).build(out, String.format(format, ingotName, "blasting"));

        // Compression
        compress(ingot, storageBlock).setGroup(Util.prefix(storageBlockName)).build(out, String.format(format, storageBlockName, ingotName));
        decompress(storageBlock, ingot).setGroup(Util.prefix(ingotName)).build(out, String.format(format, ingotName, storageBlockName));
        compress(nugget, ingot).setGroup(Util.prefix(ingotName)).build(out, String.format(format, ingotName, nuggetName));
        decompress(ingot, nugget).setGroup(Util.prefix(nuggetName)).build(out, String.format(format, nuggetName, ingotName));
    }

    private static ShapelessRecipeBuilder wrap(ShapelessRecipeBuilder builder, IItemProvider input) {
        return builder.addCriterion("has_" + input.asItem().getRegistryName().getPath(), hasItem(input));
    }

    private static ShapedRecipeBuilder wrap(ShapedRecipeBuilder builder, IItemProvider input) {
        return builder.addCriterion("has_" + input.asItem().getRegistryName().getPath(), hasItem(input));
    }

    private static CookingRecipeBuilder wrap(CookingRecipeBuilder builder, IItemProvider input) {
        return builder.addCriterion("has_" + input.asItem().getRegistryName().getPath(), hasItem(input));
    }

    private static ShapedRecipeBuilder compress(IItemProvider input, IItemProvider result) {
        return wrap(ShapedRecipeBuilder.shapedRecipe(result)
                .key('#', input)
                .patternLine("###")
                .patternLine("###")
                .patternLine("###"), input);
    }

    private static ShapelessRecipeBuilder decompress(IItemProvider input, IItemProvider result) {
        return wrap(ShapelessRecipeBuilder.shapelessRecipe(result, 9).addIngredient(input), input);
    }

    @Nonnull
    @Override
    public String getName() {
        return "Tinkers' Mechworks Recipes";
    }
}
