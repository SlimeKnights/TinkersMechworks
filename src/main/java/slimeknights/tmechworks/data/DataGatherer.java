package slimeknights.tmechworks.data;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import slimeknights.tmechworks.TMechworks;

@Mod.EventBusSubscriber(modid = TMechworks.modId, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGatherer {
    @SubscribeEvent
    protected static void gatherData(final GatherDataEvent event) {
        if(event.includeServer()) {
            DataGenerator gen = event.getGenerator();
            ExistingFileHelper helper = event.getExistingFileHelper();

            BlockTags blockTags = new BlockTags(gen, helper);
            gen.addProvider(blockTags);
            gen.addProvider(new ItemTags(gen, blockTags, helper));
            gen.addProvider(new LootProvider(gen));
            gen.addProvider(new Recipes(gen));
        }
    }
}
