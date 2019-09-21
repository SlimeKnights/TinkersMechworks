package slimeknights.tmechworks.common.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import slimeknights.tmechworks.TMechworks;

@Mod.EventBusSubscriber(modid = TMechworks.modId)
public class TooltipEventListener {
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event){
        if(Screen.hasShiftDown() && event.getItemStack().hasTag()) {
            event.getToolTip().add(new StringTextComponent(event.getItemStack().getTag().toString()));
        }
    }
}
