package slimeknights.tmechworks.common.entities;

import com.mojang.authlib.GameProfile;
import net.minecraft.potion.EffectInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.world.WorldEvent;
import slimeknights.tmechworks.TMechworks;

import java.lang.ref.WeakReference;
import java.util.UUID;

@SuppressWarnings("EntityConstructor")
public class MechworksFakePlayer extends FakePlayer {
    public static final String NAME = "MechworksWorker";
    public static final UUID ID = UUID.nameUUIDFromBytes((TMechworks.modId + ".FakePlayer").getBytes());
    public static final GameProfile PROFILE = new GameProfile(ID, NAME);

    private static MechworksFakePlayer instance;

    private MechworksFakePlayer(ServerWorld world, GameProfile name) {
        super(world, name);
    }

    public static WeakReference<FakePlayer> getInstance(ServerWorld world) {
        if (instance == null) {
            instance = new MechworksFakePlayer(world, PROFILE);
        }

        instance.world = world;
        return new WeakReference<>(instance);
    }

    private static void releaseInstance(IWorld world) {
        // If the fake player has a reference to the world getting unloaded,
        // null out the fake player so that the world can unload
        if (instance != null && instance.world == world) {
            instance = null;
        }
    }

    @Override
    public boolean isPotionApplicable(EffectInstance potioneffectIn) {
        return false;
    }

    public static void onWorldUnload(WorldEvent.Unload event) {
        if (event.getWorld() instanceof ServerWorld) {
            releaseInstance(event.getWorld());
        }
    }
}
