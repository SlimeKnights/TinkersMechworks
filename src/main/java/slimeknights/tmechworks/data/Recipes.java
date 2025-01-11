package slimeknights.tmechworks.data;

import net.minecraft.data.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.tmechworks.library.Util;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

import static slimeknights.tmechworks.common.MechworksContent.Blocks.*;
import static slimeknights.tmechworks.common.MechworksContent.Items.*;
import static slimeknights.tmechworks.common.MechworksTags.Items.*;
import static net.minecraftforge.common.Tags.Items.*;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;

public class Recipes extends RecipeProvider implements IConditionBuilder {
    private static final int standardSmeltingTime = 200;
    private static final int standardBlastingTime = standardSmeltingTime / 2;

    public Recipes(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void buildCraftingRecipes(@Nonnull Consumer<FinishedRecipe> out) {
        // Metals
        registerMetal(out, aluminum_ore, aluminum_nugget, aluminum_ingot, aluminum_block, raw_aluminum);

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
                .pattern("RAR"), aluminum_ingot)
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
                .pattern("ACA"), aluminum_ingot)
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

    private void registerMetal(@Nonnull Consumer<FinishedRecipe> out, ItemLike ore, ItemLike nugget, ItemLike ingot, ItemLike storageBlock, ItemLike raw) {
        String format = Util.prefix("%s_from_%s");

        String nuggetName = getRegistryName(nugget).getPath();
        String ingotName = getRegistryName(ingot).getPath();
        String storageBlockName = getRegistryName(storageBlock).getPath();

        // Smelting
        wrap(SimpleCookingRecipeBuilder.smelting(Ingredient.of(ore), ingot, 1F, standardSmeltingTime), ore).save(out, String.format(format, ingotName, "ore_smelting"));
        wrap(SimpleCookingRecipeBuilder.blasting(Ingredient.of(ore), ingot, 1F, standardBlastingTime), ore).save(out, String.format(format, ingotName, "ore_blasting"));

        // Raw ore smelting
        wrap(SimpleCookingRecipeBuilder.smelting(Ingredient.of(raw), ingot, 1F, standardSmeltingTime), raw).save(out, String.format(format, ingotName, "raw_smelting"));
        wrap(SimpleCookingRecipeBuilder.blasting(Ingredient.of(raw), ingot, 1F, standardBlastingTime), raw).save(out, String.format(format, ingotName, "raw_blasting"));

        // Compression
        compress(ingot, storageBlock).group(Util.prefix(storageBlockName)).save(out, String.format(format, storageBlockName, ingotName));
        decompress(storageBlock, ingot).group(Util.prefix(ingotName)).save(out, String.format(format, ingotName, storageBlockName));
        compress(nugget, ingot).group(Util.prefix(ingotName)).save(out, String.format(format, ingotName, nuggetName));
        decompress(ingot, nugget).group(Util.prefix(nuggetName)).save(out, String.format(format, nuggetName, ingotName));
    }

    private static ShapelessRecipeBuilder wrap(ShapelessRecipeBuilder builder, ItemLike input) {
        return builder.unlockedBy("has_" + getRegistryName(input).getPath(), has(input));
    }

    private static ShapedRecipeBuilder wrap(ShapedRecipeBuilder builder, ItemLike input) {
        return builder.unlockedBy("has_" + getRegistryName(input).getPath(), has(input));
    }

    private static SimpleCookingRecipeBuilder wrap(SimpleCookingRecipeBuilder builder, ItemLike input) {
        return builder.unlockedBy("has_" + getRegistryName(input).getPath(), has(input));
    }

    private static ShapedRecipeBuilder compress(ItemLike input, ItemLike result) {
        return wrap(ShapedRecipeBuilder.shaped(result)
                .define('#', input)
                .pattern("###")
                .pattern("###")
                .pattern("###"), input);
    }

    private static ShapelessRecipeBuilder decompress(ItemLike input, ItemLike result) {
        return wrap(ShapelessRecipeBuilder.shapeless(result, 9).requires(input), input);
    }

    private static ResourceLocation getRegistryName(ItemLike item) {
        return ForgeRegistries.ITEMS.getKey(item.asItem());
    }

    @Nonnull
    @Override
    public String getName() {
        return "Tinkers' Mechworks Recipes";
    }
}
