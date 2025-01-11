package slimeknights.tmechworks.common.worldgen;

import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import slimeknights.tmechworks.TMechworks;
import slimeknights.tmechworks.common.MechworksContent;

import java.util.List;
import java.util.function.Supplier;

import static net.minecraft.data.worldgen.features.OreFeatures.DEEPSLATE_ORE_REPLACEABLES;
import static net.minecraft.data.worldgen.features.OreFeatures.STONE_ORE_REPLACEABLES;

public class MechworksWorld {
    private static final DeferredRegister<ConfiguredFeature<?, ?>> FEATURES = DeferredRegister.create(Registry.CONFIGURED_FEATURE_REGISTRY, TMechworks.modId);
    private static final DeferredRegister<PlacedFeature> PLACEMENTS = DeferredRegister.create(Registry.PLACED_FEATURE_REGISTRY, TMechworks.modId);

    public static Supplier<List<OreConfiguration.TargetBlockState>> ORE_ALUMINUM_TARGET_LIST = () -> List.of(
            OreConfiguration.target(STONE_ORE_REPLACEABLES, MechworksContent.Blocks.aluminum_ore.get().defaultBlockState()),
            OreConfiguration.target(DEEPSLATE_ORE_REPLACEABLES, MechworksContent.Blocks.deepslate_aluminum_ore.get().defaultBlockState())
    );

    public static RegistryObject<ConfiguredFeature<?, ?>> ORE_ALUMINUM_CF = FEATURES.register("ore_aluminum_cf", () -> new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(ORE_ALUMINUM_TARGET_LIST.get(), 10)));
    public static RegistryObject<PlacedFeature> ORE_ALUMINUM_UPPER_PF = PLACEMENTS.register("ore_aluminum_upper", () -> new PlacedFeature(ORE_ALUMINUM_CF.getHolder().orElseThrow(), commonOrePlacement(52, HeightRangePlacement.triangle(VerticalAnchor.absolute(70), VerticalAnchor.absolute(120)))));
    public static RegistryObject<PlacedFeature> ORE_ALUMINUM_MIDDLE_PF = PLACEMENTS.register("ore_aluminum_middle", () -> new PlacedFeature(ORE_ALUMINUM_CF.getHolder().orElseThrow(), commonOrePlacement(20, HeightRangePlacement.triangle(VerticalAnchor.absolute(-30), VerticalAnchor.absolute(50)))));

    private MechworksWorld() {}

    public static void initialize() {
        FEATURES.register(FMLJavaModLoadingContext.get().getModEventBus());
        PLACEMENTS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    // Copied from OrePlacements
    private static List<PlacementModifier> orePlacement(PlacementModifier countModifier, PlacementModifier heightModifier) {
        return List.of(countModifier, InSquarePlacement.spread(), heightModifier, BiomeFilter.biome());
    }

    private static List<PlacementModifier> commonOrePlacement(int count, PlacementModifier heightRange) {
        return orePlacement(CountPlacement.of(count), heightRange);
    }
}
