package slimeknights.tmechworks.common.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;
import slimeknights.tmechworks.library.Util;

import java.util.Collections;
import java.util.List;

public class MechworksConfig {
    public static final Common COMMON_CONFIG;
    public static final ForgeConfigSpec COMMON_SPEC;

    public static class Common {
        public final WorldGeneration worldGen;
        public final Drawbridge drawbridge;

        private Common(ForgeConfigSpec.Builder builder) {
            worldGen = new WorldGeneration(builder);
            drawbridge = new Drawbridge(builder);
        }

        public static final class WorldGeneration {
            public final ForgeConfigSpec.BooleanValue enabled;

            public final Ore copper;
            public final Ore aluminum;

            WorldGeneration(ForgeConfigSpec.Builder builder) {
                builder.comment("Everything to do with world generation").push("world");

                enabled = builder
                        .comment("Whether world generation is enabled as a whole")
                        .define("enabled", true);

                copper = new Ore(builder, "copper");
                aluminum = new Ore(builder, "aluminum");

                builder.pop();
            }

            public static class Ore {
                public ForgeConfigSpec.BooleanValue enabled;

                public ForgeConfigSpec.BooleanValue isWhitelist;
                public ForgeConfigSpec.ConfigValue<List<? extends String>> filter;

                Ore(ForgeConfigSpec.Builder builder, String name) {
                    this(builder, name, true);
                }

                Ore(ForgeConfigSpec.Builder builder, String name, boolean onByDefault) {
                    this(builder, name, onByDefault, false, Collections.emptyList());
                }

                Ore(ForgeConfigSpec.Builder builder, String name, boolean onByDefault, boolean isWhitelist, List<String> filter) {
                    builder.comment("Generation settings for " + name + " ore").push(name);

                    enabled = builder
                            .comment("Whether or not this ore is generated")
                            .define("enabled", onByDefault);
                    this.isWhitelist = builder
                            .comment("If true, the filter will act as a whitelist, otherwise, blacklist")
                            .define("isWhitelist", isWhitelist);
                    this.filter = builder
                            .comment("A list of fully qualified biome names, for example \"minecraft:river\"")
                            .defineList("filter", filter, (obj) -> obj != null && Util.validateResourceName(obj.toString()));

                    builder.pop();
                }
            }
        }

        public static final class Drawbridge {
            public final ForgeConfigSpec.IntValue extendLength;
            public final ForgeConfigSpec.IntValue extendUpgradeValue;
            public final ForgeConfigSpec.DoubleValue delay;
            public final ForgeConfigSpec.DoubleValue speedUpgradeValue;

            Drawbridge(ForgeConfigSpec.Builder builder) {
                builder.comment("All the settings to do with the drawbridge").push("drawbridge");

                extendLength = builder
                        .comment("Total drawbridge distance (with upgrades) going above 66 in an advanced drawbridge may cause slots to overlap with player inventory slots")
                        .comment("The distance that the base drawbridge can extend")
                        .defineInRange("extendLength", 16, 1, 64);
                extendUpgradeValue = builder
                        .comment("How much each distance upgrade increases the max distance by")
                        .defineInRange("extendUpgradeValue", 16, 0, 64);

                delay = builder
                        .comment("The base delay between each block place/destroy")
                        .defineInRange("delay", 0.5D, 0F, Integer.MAX_VALUE);
                speedUpgradeValue = builder
                        .comment("The amount by which each speed upgrade decreases the delay")
                        .defineInRange("speedUpgradeValue", 0.1D, 0F, Integer.MAX_VALUE);

                builder.pop();
            }
        }
    }

    static {
        Pair<Common, ForgeConfigSpec> commonPair = new ForgeConfigSpec.Builder().configure(Common::new);

        COMMON_CONFIG = commonPair.getLeft();
        COMMON_SPEC = commonPair.getRight();
    }
}
