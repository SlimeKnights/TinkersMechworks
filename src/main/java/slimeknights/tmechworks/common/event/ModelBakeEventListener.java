package slimeknights.tmechworks.common.event;

import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import slimeknights.tmechworks.TMechworks;
import slimeknights.tmechworks.client.model.DisguiseBakedModel;

import java.util.*;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = TMechworks.modId, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
@OnlyIn(Dist.CLIENT)
public class ModelBakeEventListener {
    private static Logger log = LogManager.getLogger(TMechworks.modId + ".model");

    private static final Set<String> disguiseables = new HashSet<>();

    public static void registerDisguiseBlock(ResourceLocation location) {
        disguiseables.add(location.toString());
    }

    @SubscribeEvent
    public static void onModelBake(ModelBakeEvent event){
        float time = System.nanoTime();
        log.info("Starting model bake at " + time);

        List<Map.Entry<ResourceLocation, IBakedModel>> registry = new ArrayList<>(event.getModelRegistry().entrySet());

        for(int i = 0; i < registry.size(); i++){
            Map.Entry<ResourceLocation, IBakedModel> model = registry.get(i);

            String modelId = model.getKey().toString();

            if(model.getKey() instanceof ModelResourceLocation) {
                ModelResourceLocation modelRes = (ModelResourceLocation)model.getKey();
                modelId = modelRes.getNamespace() + ":" + modelRes.getPath();
            }

            if(disguiseables.contains(modelId)) {
                event.getModelRegistry().put(model.getKey(), new DisguiseBakedModel(model.getValue()));
            }
        }

        log.info("Model bake finished in " + (System.nanoTime() - time) / 1000000000F + " seconds");
    }
}
