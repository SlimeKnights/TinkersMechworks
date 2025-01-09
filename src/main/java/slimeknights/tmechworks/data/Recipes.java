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
    protected void buildShapelessRecipes(@Nonnull Consumer<IFinishedRecipe> out) {
        // Metals
        registerMetal(out, aluminum_ore, aluminum_nugget, aluminum_ingot, aluminum_block);
        registerMetal(out, copper_ore, copper_nugget, copper_ingot, copper_block);

        wrap(ShapelessRecipeBuilder.shapeless(book).requires(Items.BOOK).requires(upgrade_blank), Items.BOOK).save(out);

        // Machines
        wrap(ShapedRecipeBuilder.shaped(drawbridge)
                .group(Util.prefix("machines"))
                .define('R', DUSTS_REDSTONE)
                .define('A', INGOTS_ALUMINUM)
                .define('U', upgrade_drawbridge_distance)
                .define('P', Items.PISTON)
                .pattern("RAR")
                .pattern("PUP")
                .pattern("RAR"), copper_ingot)
                .save(out);

        wrap(ShapedRecipeBuilder.shaped(firestarter)
                .group(Util.prefix("machines"))
                .define('C', INGOTS_COPPER)
                .define('R', DUSTS_REDSTONE)
                .define('A', INGOTS_ALUMINUM)
                .define('B', upgrade_blank)
                .define('F', Items.FLINT_AND_STEEL)
                .pattern("RAR")
                .pattern("FBF")
                .pattern("ACA"), copper_ingot)
                .save(out);

        wrap(ShapedRecipeBuilder.shaped(upgrade_blank)
                .group(Util.prefix("upgrades"))
                .define('I', INGOTS_IRON)
                .define('C', INGOTS_COPPER)
                .define('R', DUSTS_REDSTONE)
                .define('A', INGOTS_ALUMINUM)
                .pattern("IAI")
                .pattern("RIR")
                .pattern("ACA"), Items.REDSTONE)
                .save(out);

        wrap(ShapedRecipeBuilder.shaped(upgrade_drawbridge_advanced)
                .group(Util.prefix("upgrades"))
                .define('B', upgrade_blank)
                .define('H', Items.HOPPER)
                .define('C', INGOTS_COPPER)
                .define('A', INGOTS_ALUMINUM)
                .define('L', STORAGE_BLOCKS_LAPIS)
                .define('S', CHESTS_WOODEN)
                .pattern("CHC")
                .pattern("LBL")
                .pattern("ASA"), upgrade_blank)
                .save(out);

        wrap(ShapedRecipeBuilder.shaped(upgrade_drawbridge_distance)
                .group(Util.prefix("upgrades"))
                .define('B', upgrade_blank)
                .define('P', Items.PISTON)
                .define('C', INGOTS_COPPER)
                .define('A', INGOTS_ALUMINUM)
                .pattern("ACA")
                .pattern("PBP")
                .pattern(" A "), upgrade_blank)
                .save(out);

        wrap(ShapedRecipeBuilder.shaped(upgrade_speed)
                .group(Util.prefix("upgrades"))
                .define('B', upgrade_blank)
                .define('S', Items.SUGAR)
                .pattern(" S ")
                .pattern("SBS")
                .pattern(" S "), upgrade_blank)
                .save(out);
    }

    private void registerMetal(@Nonnull Consumer<IFinishedRecipe> out, IItemProvider ore, IItemProvider nugget, IItemProvider ingot, IItemProvider storageBlock) {
        String format = Util.prefix("%s_from_%s");

        String nuggetName = nugget.asItem().getRegistryName().getPath();
        String ingotName = ingot.asItem().getRegistryName().getPath();
        String storageBlockName = storageBlock.asItem().getRegistryName().getPath();

        // Smelting
        wrap(CookingRecipeBuilder.smelting(Ingredient.of(ore), ingot, 1F, standardSmeltingTime), ore).save(out, String.format(format, ingotName, "smelting"));
        wrap(CookingRecipeBuilder.blasting(Ingredient.of(ore), ingot, 1F, standardBlastingTime), ore).save(out, String.format(format, ingotName, "blasting"));

        // Compression
        compress(ingot, storageBlock).group(Util.prefix(storageBlockName)).save(out, String.format(format, storageBlockName, ingotName));
        decompress(storageBlock, ingot).group(Util.prefix(ingotName)).save(out, String.format(format, ingotName, storageBlockName));
        compress(nugget, ingot).group(Util.prefix(ingotName)).save(out, String.format(format, ingotName, nuggetName));
        decompress(ingot, nugget).group(Util.prefix(nuggetName)).save(out, String.format(format, nuggetName, ingotName));
    }

    private static ShapelessRecipeBuilder wrap(ShapelessRecipeBuilder builder, IItemProvider input) {
        return builder.unlockedBy("has_" + input.asItem().getRegistryName().getPath(), has(input));
    }

    private static ShapedRecipeBuilder wrap(ShapedRecipeBuilder builder, IItemProvider input) {
        return builder.unlockedBy("has_" + input.asItem().getRegistryName().getPath(), has(input));
    }

    private static CookingRecipeBuilder wrap(CookingRecipeBuilder builder, IItemProvider input) {
        return builder.unlockedBy("has_" + input.asItem().getRegistryName().getPath(), has(input));
    }

    private static ShapedRecipeBuilder compress(IItemProvider input, IItemProvider result) {
        return wrap(ShapedRecipeBuilder.shaped(result)
                .define('#', input)
                .pattern("###")
                .pattern("###")
                .pattern("###"), input);
    }

    private static ShapelessRecipeBuilder decompress(IItemProvider input, IItemProvider result) {
        return wrap(ShapelessRecipeBuilder.shapeless(result, 9).requires(input), input);
    }

    @Nonnull
    @Override
    public String getName() {
        return "Tinkers' Mechworks Recipes";
    }
}
