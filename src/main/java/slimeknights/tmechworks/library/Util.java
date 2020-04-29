package slimeknights.tmechworks.library;

import com.mojang.authlib.GameProfile;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import org.apache.commons.lang3.StringUtils;
import slimeknights.tmechworks.TMechworks;

import java.lang.ref.WeakReference;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

public class Util
{
    public static final String RESOURCE = TMechworks.modId;
    public static final Random rand = new Random();
    public static final String FAKEPLAYER_NAME = "MechworksWorker";

    public static WeakReference<FakePlayer> createFakePlayer (World world)
    {
        if (!(world instanceof ServerWorld))
        {
            return null;
        }

        return new WeakReference<>(FakePlayerFactory.get((ServerWorld) world, new GameProfile(UUID.randomUUID(), FAKEPLAYER_NAME)));
    }

    /**
     * Returns the given Resource prefixed with tinkers resource location. Use this function instead of hardcoding
     * resource locations.
     */
    public static String resource (String res)
    {
        return String.format("%s:%s", RESOURCE, res);
    }

    public static ResourceLocation getResource (String res)
    {
        return new ResourceLocation(RESOURCE, res);
    }

//    public static ResourceLocation getModelResource (String res, String variant)
//    {
//        return new ModelResourceLocation(resource(res), variant);
//    }

    /**
     * Prefixes the given unlocalized name with tinkers prefix. Use this when passing unlocalized names for a uniform
     * namespace.
     */
    public static String prefix (String name)
    {
        return String.format("%s:%s", RESOURCE, name.toLowerCase(Locale.US));
    }

    public static boolean validateResourceName(String resourceName){
        String namespace = "minecraft";
        String path = resourceName;

        int i = resourceName.indexOf(':');
        if (i >= 0) {
            path = resourceName.substring(i + 1);
            if (i >= 1) {
                namespace = resourceName.substring(0, i);
            }
        }

        if(StringUtils.isEmpty(namespace))
            namespace = "minecraft";

        boolean namespaceValid = namespace.chars().allMatch(c -> c == 95 || c == 45 || c >= 97 && c <= 122 || c >= 48 && c <= 57 || c == 46);
        boolean pathValid = path.chars().allMatch(c -> c == 95 || c == 45 || c >= 97 && c <= 122 || c >= 48 && c <= 57 || c == 47 || c == 46);

        return namespaceValid && pathValid;
    }

    // Does nothing
    public static void sink(Object object) { }
}
