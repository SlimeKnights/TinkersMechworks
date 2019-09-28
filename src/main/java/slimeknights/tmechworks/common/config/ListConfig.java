package slimeknights.tmechworks.common.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import slimeknights.tmechworks.TMechworks;
import slimeknights.tmechworks.library.Util;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class ListConfig {
    public static final ListConfig DRAWBRIDGE_BLACKLIST = new ListConfig("Drawbridge", ListType.Blacklist);
    public static final ListConfig FIRESTARTER_WHITELIST = new ListConfig("Firestarter Extinguish", ListType.Whitelist, ImmutableList.of(
            Blocks.FIRE.getRegistryName().toString(),
            Blocks.NETHER_PORTAL.getRegistryName().toString()
    ));

    private final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

    public final ForgeConfigSpec.ConfigValue<List<? extends String>> list;
    private final Set<Predicate<ResourceLocation>> extraList = Sets.newHashSet();

    private final ForgeConfigSpec spec;

    private ListConfig(String of, ListType type) {
        this(of, type, Collections.emptyList());
    }

    private ListConfig(String of, ListType type, List<? extends String> defaultValues) {
        StringBuilder comment = new StringBuilder();
        comment.append("Any blocks in this list will")
                .append(type.extra)
                .append(" be allowed for ")
                .append(of)
                .append("\nA list of fully qualified registry names, for example, \"minecraft:iron_block\"");

        list = builder
                .comment(comment.toString())
                .defineList(type.path, defaultValues, obj -> Util.validateResourceName(obj.toString()));

        spec = builder.build();

        Path root = Paths.get(TMechworks.CONFIG_ROOT.toString(), type.folder);
        root.toFile().mkdirs();

        CommentedFileConfig config = CommentedFileConfig.builder(Paths.get(root.toString(), of + ".toml")).build();
        config.load();
        spec.setConfig(config);
    }

    public boolean isListed(@Nullable ResourceLocation resource) {
        if (resource == null)
            return false;

        List<? extends String> list = this.list.get();

        return list.stream().anyMatch(obj -> resource.toString().equals(obj)) || extraList.stream().anyMatch(x -> x.test(resource));
    }

    /**
     * Adds an additional check for this list, to be used by mods that want their resources to be part of this list
     *
     * Does not become part of the config file and cannot be disabled if added
     * @param predicate A predicate that returns true if the provided resource matches the list
     */
    public void addExtra(Predicate<ResourceLocation> predicate) {
        extraList.add(predicate);
    }

    public static void ping() {
    }

    public enum ListType {
        Whitelist("Whitelist", "whitelist", ""),
        Blacklist("Blacklist", "blacklist", " not");

        public final String folder;
        public final String path;
        public final String extra;

        ListType(String folder, String path, String extra) {
            this.folder = folder;
            this.path = path;
            this.extra = extra;
        }
    }
}
