package slimeknights.tmechworks.common;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.minecraftforge.common.ForgeConfigSpec;

import java.nio.file.Paths;

public class MechworksConfig {
    public static void load() {
        SPEC.setConfig(CommentedFileConfig.builder(Paths.get("config", "Tinkers Mechworks.toml")).build());
    }

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final Drawbridge DRAWBRIDGE = new Drawbridge();
    public static class Drawbridge {
        public ForgeConfigSpec.IntValue extendLength;
        public ForgeConfigSpec.IntValue extendUpgradeValue;
        public ForgeConfigSpec.DoubleValue delay;
        public ForgeConfigSpec.DoubleValue speedUpgradeValue;

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
