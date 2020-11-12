package slimeknights.tmechworks.common;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import slimeknights.tmechworks.library.Util;

public class MechworksTags {
    public static class Blocks {
        // Own
        public static final Tags.IOptionalNamedTag<Block> DRAWBRIDGE_BLACKLIST = tag("drawbridge_blacklist");
        public static final Tags.IOptionalNamedTag<Block> FIRESTARTER_WHITELIST = tag("firestarter_extinguish_whitelist");

        // Forge
        public static final Tags.IOptionalNamedTag<Block> ORES_ALUMINUM = forgeTag("ores/aluminum");
        public static final Tags.IOptionalNamedTag<Block> ORES_COPPER = forgeTag("ores/copper");

        public static final Tags.IOptionalNamedTag<Block> STORAGE_BLOCKS_ALUMINUM = forgeTag("storage_blocks/aluminum");
        public static final Tags.IOptionalNamedTag<Block> STORAGE_BLOCKS_COPPER = forgeTag("storage_blocks/copper");

        private static Tags.IOptionalNamedTag<Block> tag(String name) {
            return BlockTags.createOptional(Util.getResource(name));
        }

        private static Tags.IOptionalNamedTag<Block> forgeTag(String name) {
            return BlockTags.createOptional(new ResourceLocation("forge", name));
        }
    }

    public static class Items {
        // Forge
        public static final Tags.IOptionalNamedTag<Item> INGOTS_ALUMINUM = forgeTag("ingots/aluminum");
        public static final Tags.IOptionalNamedTag<Item> INGOTS_COPPER = forgeTag("ingots/copper");

        public static final Tags.IOptionalNamedTag<Item> NUGGETS_ALUMINUM = forgeTag("nuggets/aluminum");
        public static final Tags.IOptionalNamedTag<Item> NUGGETS_COPPER = forgeTag("nuggets/copper");

        public static final Tags.IOptionalNamedTag<Item> ORES_ALUMINUM = forgeTag("ores/aluminum");
        public static final Tags.IOptionalNamedTag<Item> ORES_COPPER = forgeTag("ores/copper");

        public static final Tags.IOptionalNamedTag<Item> STORAGE_BLOCKS_ALUMINUM = forgeTag("storage_blocks/aluminum");
        public static final Tags.IOptionalNamedTag<Item> STORAGE_BLOCKS_COPPER = forgeTag("storage_blocks/copper");

        private static Tags.IOptionalNamedTag<Item> tag(String name) {
            return ItemTags.createOptional(Util.getResource(name));
        }

        private static Tags.IOptionalNamedTag<Item> forgeTag(String name) {
            return ItemTags.createOptional(new ResourceLocation("forge", name));
        }
    }
}
