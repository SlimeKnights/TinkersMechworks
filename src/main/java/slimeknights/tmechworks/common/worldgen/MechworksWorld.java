package slimeknights.tmechworks.common.worldgen;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.BlockState;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import slimeknights.tmechworks.TMechworks;
import slimeknights.tmechworks.common.MechworksContent;
import slimeknights.tmechworks.common.config.MechworksConfig;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public class MechworksWorld {
    private static MechworksWorld INSTANCE;

    private static final Logger log = LogManager.getLogger(TMechworks.modId + ".world");

    private static final List<OreProperties> OVERWORLD_ORES = ImmutableList.of(
            new OreProperties(() -> MechworksContent.Blocks.copper_ore.get().getDefaultState(), 8, ore -> ore.range(64).square().count(20), MechworksConfig.COMMON_CONFIG.worldGen.copper),
            new OreProperties(() -> MechworksContent.Blocks.aluminum_ore.get().getDefaultState(), 8, ore -> ore.range(64).square().count(20), MechworksConfig.COMMON_CONFIG.worldGen.aluminum)
    );

    public MechworksWorld() {
        INSTANCE = this;
    }

    public static MechworksWorld getInstance() {
        return INSTANCE;
    }

    public void setupWorldGeneration() {
        Registry<ConfiguredFeature<?, ?>> registry = WorldGenRegistries.CONFIGURED_FEATURE;

        for (OreProperties ore : OVERWORLD_ORES) {
            ore.preconfigureFeature();

            Registry.register(registry, Objects.requireNonNull(ore.state.get().getBlock().getRegistryName()), ore.preconfiguredFeature);
        }
    }

    @SubscribeEvent
    public void onBiomeLoad(BiomeLoadingEvent ev) {
        if (!MechworksConfig.COMMON_CONFIG.worldGen.enabled.get()) {
            return;
        }

        // TODO: biome dictionary check when forge updates
        OVERWORLD_ORES.forEach(ore -> ore.addToBiome(ev.getName().toString(), ev.getGeneration()));
    }

    private static class OreProperties {
        private final Supplier<BlockState> state;
        private final Function<ConfiguredFeature<?, ?>, ConfiguredFeature<?, ?>> processor;
        private final int frequency;
        private final MechworksConfig.Common.WorldGeneration.Ore config;

        private ConfiguredFeature<?, ?> preconfiguredFeature;

        OreProperties(Supplier<BlockState> state, int frequency, Function<ConfiguredFeature<?, ?>, ConfiguredFeature<?, ?>> processor, MechworksConfig.Common.WorldGeneration.Ore config) {
            this.state = state;
            this.frequency = frequency;
            this.processor = processor;
            this.config = config;
        }

        private void preconfigureFeature() {
            preconfiguredFeature = Feature.ORE.withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.BASE_STONE_OVERWORLD, state.get(), frequency));
            preconfiguredFeature = processor.apply(preconfiguredFeature);
        }

        private void addToBiome(String biome, BiomeGenerationSettingsBuilder generation){
            if(!config.enabled.get())
                return;

            if(preconfiguredFeature == null) {
                log.error("Preconfigured feature for " + state.get().getBlock().getRegistryName() + " is null, skipping...");
                return;
            }

            boolean isWhitelist = config.isWhitelist.get();
            List<? extends String> filter = config.filter.get();
            boolean matches = filter.stream().anyMatch(biome::equals);

            if((isWhitelist && !matches) || (!isWhitelist && matches)) {
                return;
            }

            generation.withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, preconfiguredFeature);
        }
    }
}
