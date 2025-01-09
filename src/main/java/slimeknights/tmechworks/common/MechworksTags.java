package slimeknights.tmechworks.common;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.resources.ResourceLocation;
import slimeknights.tmechworks.library.Util;

public class MechworksTags {
    public static class Blocks {
        // Own
        public static final TagKey<Block> DRAWBRIDGE_BLACKLIST = tag("drawbridge_blacklist");
        public static final TagKey<Block> FIRESTARTER_WHITELIST = tag("firestarter_extinguish_whitelist");
        public static final TagKey<Block> ORES_ALL = tag("ores");

        // Forge
        public static final TagKey<Block> ORES_ALUMINUM = forgeTag("ores/aluminum");
        public static final TagKey<Block> STORAGE_BLOCKS_ALUMINUM = forgeTag("storage_blocks/aluminum");

        private static TagKey<Block> tag(String name) {
            return BlockTags.create(Util.getResource(name));
        }

        private static TagKey<Block> forgeTag(String name) {
            return BlockTags.create(new ResourceLocation("forge", name));
        }
    }

    public static class Items {
        // Own
        public static final TagKey<Item> UPGRADES = tag("upgrades");
        public static final TagKey<Item> ORES_ALL = tag("ores");

        // Forge
        public static final TagKey<Item> RAW_ALUMINUM = forgeTag("raw_materials/aluminum");
        public static final TagKey<Item> INGOTS_ALUMINUM = forgeTag("ingots/aluminum");
        public static final TagKey<Item> NUGGETS_ALUMINUM = forgeTag("nuggets/aluminum");
        public static final TagKey<Item> ORES_ALUMINUM = forgeTag("ores/aluminum");
        public static final TagKey<Item> STORAGE_BLOCKS_ALUMINUM = forgeTag("storage_blocks/aluminum");

        private static TagKey<Item> tag(String name) {
            return ItemTags.create(Util.getResource(name));
        }

        private static TagKey<Item> forgeTag(String name) {
            return ItemTags.create(new ResourceLocation("forge", name));
        }
    }
}
