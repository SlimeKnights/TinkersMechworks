package slimeknights.tmechworks.common.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

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
            WorldGeneration(ForgeConfigSpec.Builder builder) {
                builder.comment("Everything to do with world generation").push("world");
                builder.comment("World generation configuration has been retired in favour of datapacks.");
                builder.pop();
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
