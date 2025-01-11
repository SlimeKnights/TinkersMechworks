package slimeknights.tmechworks.data;

import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import slimeknights.tmechworks.common.worldgen.MechworksWorld;
import slimeknights.tmechworks.library.Util;

import java.util.Map;

public class WorldGeneration {
    public static Map<ResourceLocation, BiomeModifier> getModifiers(RegistryAccess registryAccess) {
        HolderSet<Biome> overworld = new HolderSet.Named<>(registryAccess.registryOrThrow(Registry.BIOME_REGISTRY), BiomeTags.IS_OVERWORLD);

        Registry<PlacedFeature> pfRegistry = registryAccess.registryOrThrow(Registry.PLACED_FEATURE_REGISTRY);
        HolderSet<PlacedFeature> aluminum = HolderSet.direct(pfRegistry.getHolderOrThrow(MechworksWorld.ORE_ALUMINUM_UPPER_PF.getKey()), pfRegistry.getHolderOrThrow(MechworksWorld.ORE_ALUMINUM_MIDDLE_PF.getKey()));

        return Map.of(
                Util.getResource("aluminum"), new ForgeBiomeModifiers.AddFeaturesBiomeModifier(overworld, aluminum, GenerationStep.Decoration.UNDERGROUND_ORES)
        );
    }
}
