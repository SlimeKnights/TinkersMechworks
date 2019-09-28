package slimeknights.tmechworks.common.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.google.common.collect.ImmutableList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import slimeknights.tmechworks.TMechworks;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

public class BlacklistConfig {
    public static final Path BLACKLIST_ROOT = Paths.get(TMechworks.CONFIG_ROOT.toString(), "Blacklist");
    public static final BlacklistConfig DRAWBRIDGE = new BlacklistConfig("Drawbridge");

    private final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

    public final ForgeConfigSpec.ConfigValue<List<? extends String>> blacklist;

    private final ForgeConfigSpec spec;

    private BlacklistConfig(String of) {
        this(of, Collections.emptyList());
    }

    private BlacklistConfig(String of, List<? extends String> defaultValues) {
        blacklist = builder
                .comment()
                .comment("Any blocks in this list will not be allowed in the " + of + "\nA list of fully qualified block registry names, for example, \"minecraft:iron_block\"")
                .defineList("blacklist", defaultValues, obj -> ResourceLocation.isResouceNameValid(obj.toString()));

        spec = builder.build();

        BLACKLIST_ROOT.toFile().mkdirs();

        CommentedFileConfig config = CommentedFileConfig.builder(Paths.get(BLACKLIST_ROOT.toString(), of + ".toml")).build();
        config.load();
        spec.setConfig(config);
    }

    public boolean isBlacklisted(@Nullable ResourceLocation resource) {
        if(resource == null)
            return false;

        List<? extends String> list = blacklist.get();

        return list.stream().anyMatch(resource.toString()::equals);
    }

    public static void ping() {
    }
}
