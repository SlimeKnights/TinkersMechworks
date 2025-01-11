package slimeknights.tmechworks.data;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.JsonCodecProvider;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.tmechworks.TMechworks;

@Mod.EventBusSubscriber(modid = TMechworks.modId, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGatherer {
    @SubscribeEvent
    protected static void gatherData(final GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        ExistingFileHelper helper = event.getExistingFileHelper();
        RegistryAccess registryAccess = RegistryAccess.builtinCopy();
        RegistryOps<JsonElement> registryOps = RegistryOps.create(JsonOps.INSTANCE, registryAccess);

        BlockTags blockTags = new BlockTags(gen, helper);
        gen.addProvider(event.includeServer(), blockTags);
        gen.addProvider(event.includeServer(), new ItemTags(gen, blockTags, helper));
        gen.addProvider(event.includeServer(), new LootProvider(gen));
        gen.addProvider(event.includeServer(), new Recipes(gen));

        JsonCodecProvider<BiomeModifier> placementFeatures = JsonCodecProvider.forDatapackRegistry(gen, helper, "tmechworks", registryOps, ForgeRegistries.Keys.BIOME_MODIFIERS, WorldGeneration.getModifiers(registryAccess));
        gen.addProvider(event.includeServer(), placementFeatures);
    }
}
