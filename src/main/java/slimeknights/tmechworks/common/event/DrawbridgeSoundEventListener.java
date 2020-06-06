package slimeknights.tmechworks.common.event;

import net.minecraft.entity.Entity;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.StringUtils;
import slimeknights.tmechworks.TMechworks;
import slimeknights.tmechworks.common.entities.MechworksFakePlayer;
import slimeknights.tmechworks.library.Util;

@Mod.EventBusSubscriber(modid = TMechworks.modId)
public class DrawbridgeSoundEventListener {

    /**
     * Prevents any sound from playing for the TMechworks fake player, this prevents item use sounds for the drawbridge
     */
    @SubscribeEvent
    public static void onSound(PlaySoundAtEntityEvent event){
        Entity entity = event.getEntity();
        if(entity instanceof FakePlayer && StringUtils.equals(((FakePlayer) entity).getGameProfile().getName(), MechworksFakePlayer.NAME))
            event.setCanceled(true);
    }
}
