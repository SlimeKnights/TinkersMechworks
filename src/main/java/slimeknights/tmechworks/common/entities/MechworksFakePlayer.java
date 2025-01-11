package slimeknights.tmechworks.common.entities;

import com.mojang.authlib.GameProfile;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.level.LevelEvent;
import slimeknights.tmechworks.TMechworks;

import java.lang.ref.WeakReference;
import java.util.UUID;

@SuppressWarnings("EntityConstructor")
public class MechworksFakePlayer extends FakePlayer {
    public static final String NAME = "MechworksWorker";
    public static final UUID ID = UUID.nameUUIDFromBytes((TMechworks.modId + ".FakePlayer").getBytes());
    public static final GameProfile PROFILE = new GameProfile(ID, NAME);

    private static MechworksFakePlayer instance;

    private MechworksFakePlayer(ServerLevel world, GameProfile name) {
        super(world, name);
    }

    public static WeakReference<FakePlayer> getInstance(ServerLevel world) {
        if (instance == null) {
            instance = new MechworksFakePlayer(world, PROFILE);
        }

        instance.level = world;
        return new WeakReference<>(instance);
    }

    private static void releaseInstance(LevelAccessor world) {
        // If the fake player has a reference to the world getting unloaded,
        // null out the fake player so that the world can unload
        if (instance != null && instance.level == world) {
            instance = null;
        }
    }

    @Override
    public boolean canBeAffected(MobEffectInstance potioneffectIn) {
        return false;
    }

    public static void onWorldUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel) {
            releaseInstance(event.getLevel());
        }
    }
}
