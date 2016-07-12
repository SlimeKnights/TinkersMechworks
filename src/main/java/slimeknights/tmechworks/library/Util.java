package slimeknights.tmechworks.library;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

import java.util.Locale;
import java.util.Random;
import java.util.UUID;

public class Util
{

    public static final String RESOURCE = "tmechworks";
    public static final Random rand = new Random();

    public static FakePlayer createFakePlayer (World world)
    {
        if (!(world instanceof WorldServer))
        {
            return null;
        }

        return FakePlayerFactory.get((WorldServer) world, new GameProfile(UUID.randomUUID(), "MechworksWorker"));
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

    public static ModelResourceLocation getModelResource (String res, String variant)
    {
        return new ModelResourceLocation(resource(res), variant);
    }

    /**
     * Prefixes the given unlocalized name with tinkers prefix. Use this when passing unlocalized names for a uniform
     * namespace.
     */
    public static String prefix (String name)
    {
        return String.format("%s.%s", RESOURCE, name.toLowerCase(Locale.US));
    }

}
