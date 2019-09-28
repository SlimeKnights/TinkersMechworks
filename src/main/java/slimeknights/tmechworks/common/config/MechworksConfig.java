package slimeknights.tmechworks.common.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import slimeknights.tmechworks.TMechworks;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

public class MechworksConfig {
    public static final Path CONFIG_PATH = Paths.get(TMechworks.CONFIG_ROOT.toString(), "Config.toml");

    public static void load() {
        SPEC.setConfig(CommentedFileConfig.builder(CONFIG_PATH).build());
    }

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final WorldGeneration WORLD_GENERATION = new WorldGeneration();
    public static final class WorldGeneration {
        public final ForgeConfigSpec.BooleanValue enabled;

        public final Ore COPPER = new Ore("copper");
        public final Ore ALUMINUM = new Ore("aluminum");

        WorldGeneration() {
            BUILDER.push("world");

            enabled = BUILDER
                    .comment("Whether world generation is enabled as a whole")
                    .define("enabled", true);

            BUILDER.pop();
        }

        public static class Ore {
            public ForgeConfigSpec.BooleanValue enabled;

            public ForgeConfigSpec.BooleanValue isWhitelist;
            public ForgeConfigSpec.ConfigValue<List<? extends String>> filter;

            Ore(String name) {
                this(name, true);
            }

            Ore(String name, boolean onByDefault) {
                this(name, onByDefault, false, Collections.emptyList());
            }

            Ore(String name, boolean onByDefault, boolean isWhitelist, List<String> filter) {
                BUILDER.push("world." + name);

                enabled = BUILDER
                        .comment("Whether or not this ore is generated")
                        .define("enabled", onByDefault);
                this.isWhitelist = BUILDER
                        .comment("If true, the filter will act as a whitelist, otherwise, blacklist")
                        .define("isWhitelist", isWhitelist);
                this.filter = BUILDER
                        .comment("A list of fully qualified biome names, for example \"minecraft:river\"")
                        .defineList("filter", filter, (obj) -> obj != null && ResourceLocation.isResouceNameValid(obj.toString()));

                BUILDER.pop(2);
            }
        }
    }

    public static final Drawbridge DRAWBRIDGE = new Drawbridge();

    public static final class Drawbridge {
        public final ForgeConfigSpec.IntValue extendLength;
        public final ForgeConfigSpec.IntValue extendUpgradeValue;
        public final ForgeConfigSpec.DoubleValue delay;
        public final ForgeConfigSpec.DoubleValue speedUpgradeValue;

        Drawbridge() {
            BUILDER.push("drawbridge");

            extendLength = BUILDER
                    .comment("Total drawbridge distance (with upgrades) going above 66 in an advanced drawbridge may cause slots to overlap with player inventory slots")
                    .comment("The distance that the base drawbridge can extend")
                    .defineInRange("extendLength", 16, 1, 64);
            extendUpgradeValue = BUILDER
                    .comment("How much each distance upgrade increases the max distance by")
                    .defineInRange("extendUpgradeValue", 16, 0, 64);

            delay = BUILDER
                    .comment("The base delay between each block place/destroy")
                    .defineInRange("delay", 0.5D, 0F, Integer.MAX_VALUE);
            speedUpgradeValue = BUILDER
                    .comment("The amount by which each speed upgrade decreases the delay")
                    .defineInRange("speedUpgradeValue", 0.1D, 0F, Integer.MAX_VALUE);

            BUILDER.pop();
        }
    }

    private static final ForgeConfigSpec SPEC = BUILDER.build();
}
