package slimeknights.tmechworks.common.worldgen;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.BlockState;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.placement.CountRangeConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.tmechworks.common.MechworksContent;
import slimeknights.tmechworks.common.config.MechworksConfig;

import java.util.List;
import java.util.stream.Collectors;

public class MechworksWorld {
    private static final List<OreProperties> OVERWORLD_ORES = ImmutableList.of(
            new OreProperties(MechworksContent.Blocks.copper_ore.get().getDefaultState(), new CountRangeConfig(8, 40, 0, 75), 8, MechworksConfig.WORLD_GENERATION.COPPER),
            new OreProperties(MechworksContent.Blocks.aluminum_ore.get().getDefaultState(), new CountRangeConfig(8, 40, 0, 75), 8, MechworksConfig.WORLD_GENERATION.ALUMINUM)
    );

    public static void registerWorldGeneration() {
        if (!MechworksConfig.WORLD_GENERATION.enabled.get()) {
            return;
        }

        for (Biome biome : BiomeDictionary.getBiomes(BiomeDictionary.Type.OVERWORLD)) {
            addOresTo(biome);
        }
    }

    private static void addOresTo(Biome biome) {
        OVERWORLD_ORES.forEach(ore -> ore.add(biome));
    }

    private static class OreProperties {
        private final BlockState state;
        private final CountRangeConfig countRange;
        private final int frequency;
        private final MechworksConfig.WorldGeneration.Ore config;

        OreProperties(BlockState state, CountRangeConfig countRange, int frequency, MechworksConfig.WorldGeneration.Ore config) {
            this.state = state;
            this.countRange = countRange;
            this.frequency = frequency;
            this.config = config;
        }

        private void add(Biome biome){
            if(!config.enabled.get())
                return;

            boolean isWhitelist = config.isWhitelist.get();
            List<? extends String> filter = config.filter.get();
            boolean matches = filter.stream().anyMatch(val -> biome.getRegistryName().toString().equals(val));

            if((isWhitelist && !matches) || (!isWhitelist && matches)) {
                return;
            }

            biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.ORE.withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, state, frequency)).withPlacement(Placement.COUNT_RANGE.configure(countRange)));
        }
    }
}
