package slimeknights.tmechworks.common.worldgen;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import slimeknights.tmechworks.TMechworks;
import slimeknights.tmechworks.common.MechworksContent;
import slimeknights.tmechworks.common.config.MechworksConfig;
import slimeknights.tmechworks.library.Util;

import java.util.List;

import static net.minecraft.data.worldgen.features.OreFeatures.DEEPSLATE_ORE_REPLACEABLES;
import static net.minecraft.data.worldgen.features.OreFeatures.STONE_ORE_REPLACEABLES;

public class MechworksWorld {
    private static final Logger log = LogManager.getLogger(TMechworks.modId + ".world");

    public static List<OreConfiguration.TargetBlockState> ORE_ALUMINUM_TARGET_LIST;
    public static Holder<ConfiguredFeature<OreConfiguration, ?>> ORE_ALUMINUM_CF;
    public static Holder<PlacedFeature> ORE_ALUMINUM_UPPER_PF;
    public static Holder<PlacedFeature> ORE_ALUMINUM_MIDDLE_PF;

    private MechworksWorld() {}

    public static void initialize() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(MechworksWorld::setup);
        MinecraftForge.EVENT_BUS.addListener(MechworksWorld::biomeModification);
    }

    private static void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            if(!MechworksConfig.COMMON_CONFIG.worldGen.enabled.get()) {
                return;
            }

            ORE_ALUMINUM_TARGET_LIST = List.of(
                    OreConfiguration.target(STONE_ORE_REPLACEABLES, MechworksContent.Blocks.aluminum_ore.get().defaultBlockState()),
                    OreConfiguration.target(DEEPSLATE_ORE_REPLACEABLES, MechworksContent.Blocks.deepslate_aluminum_ore.get().defaultBlockState())
            );

            ORE_ALUMINUM_CF = FeatureUtils.register(Util.prefix("ore_aluminum_cf"), Feature.ORE, new OreConfiguration(ORE_ALUMINUM_TARGET_LIST, 10));

            ORE_ALUMINUM_UPPER_PF = PlacementUtils.register(Util.prefix("ore_iron_upper"), ORE_ALUMINUM_CF, commonOrePlacement(52, HeightRangePlacement.triangle(VerticalAnchor.absolute(70), VerticalAnchor.absolute(120))));
            ORE_ALUMINUM_MIDDLE_PF = PlacementUtils.register(Util.prefix("ore_iron_middle"), ORE_ALUMINUM_CF, commonOrePlacement(20, HeightRangePlacement.triangle(VerticalAnchor.absolute(-30), VerticalAnchor.absolute(50))));
        });
    }

    private static void biomeModification(final BiomeLoadingEvent event) {
        if(!MechworksConfig.COMMON_CONFIG.worldGen.enabled.get()) {
            return;
        }

        Biome.BiomeCategory category = event.getCategory();
        if(category == Biome.BiomeCategory.NETHER || category == Biome.BiomeCategory.THEEND) {
            return;
        }

        BiomeGenerationSettingsBuilder generation = event.getGeneration();
        ResourceLocation biomeName = event.getName();

        if(biomeName == null) {
            log.error("Biome name is null, skipping...");
            return;
        }

        placeOre(MechworksConfig.COMMON_CONFIG.worldGen.aluminum, generation, biomeName.toString());
    }

    private static void placeOre(MechworksConfig.Common.WorldGeneration.Ore ore, BiomeGenerationSettingsBuilder generation, String biomeName) {
        if(ore.enabled.get()) {
            boolean isWhitelist = ore.isWhitelist.get();
            List<? extends String> filter = ore.filter.get();
            boolean matches = filter.stream().anyMatch(biomeName::equals);

            if((isWhitelist && !matches) || (!isWhitelist && matches)) {
                return;
            }

            generation.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ORE_ALUMINUM_UPPER_PF);
            generation.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ORE_ALUMINUM_MIDDLE_PF);
        }
    }

    // Copied from OrePlacements
    private static List<PlacementModifier> orePlacement(PlacementModifier countModifier, PlacementModifier heightModifier) {
        return List.of(countModifier, InSquarePlacement.spread(), heightModifier, BiomeFilter.biome());
    }

    private static List<PlacementModifier> commonOrePlacement(int count, PlacementModifier heightRange) {
        return orePlacement(CountPlacement.of(count), heightRange);
    }
}
