package slimeknights.tmechworks.data;

import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import slimeknights.tmechworks.TMechworks;
import slimeknights.tmechworks.common.MechworksContent;
import slimeknights.tmechworks.common.MechworksTags;

import static net.minecraft.tags.ItemTags.*;

public class ItemTags extends ItemTagsProvider {
    private TagsProvider.TagAppender<Item> allIngotTags;
    private TagsProvider.TagAppender<Item> allNuggetTags;

    public ItemTags(DataGenerator dataGenerator, BlockTagsProvider blockTagProvider, ExistingFileHelper helper) {
        super(dataGenerator, blockTagProvider, TMechworks.modId, helper);
    }

    @Override
    protected void addTags() {
        copy(Tags.Blocks.ORES, Tags.Items.ORES);
        copy(Tags.Blocks.STORAGE_BLOCKS, Tags.Items.STORAGE_BLOCKS);

        copy(MechworksTags.Blocks.ORES_ALUMINUM, MechworksTags.Items.ORES_ALUMINUM);
        copy(MechworksTags.Blocks.STORAGE_BLOCKS_ALUMINUM, MechworksTags.Items.STORAGE_BLOCKS_ALUMINUM);

        addIngot(MechworksTags.Items.INGOTS_ALUMINUM, MechworksContent.Items.aluminum_ingot.get());
        addNugget(MechworksTags.Items.NUGGETS_ALUMINUM, MechworksContent.Items.aluminum_nugget.get());

        tag(LECTERN_BOOKS).add(MechworksContent.Items.book.get());

        copy(MechworksTags.Blocks.ORES_ALL, MechworksTags.Items.ORES_ALL);

        tag(MechworksTags.Items.UPGRADES).add(
                MechworksContent.Items.upgrade_blank.get(),
                MechworksContent.Items.upgrade_speed.get(),
                MechworksContent.Items.upgrade_drawbridge_advanced.get(),
                MechworksContent.Items.upgrade_drawbridge_distance.get()
        );
    }

    private void addIngot(TagKey<Item> tag, Item... item) {
        tag(tag).add(item);

        if(allIngotTags == null)
            allIngotTags = tag(Tags.Items.INGOTS);

        allIngotTags.add(item);
    }

    private void addNugget(TagKey<Item> tag, Item... item) {
        tag(tag).add(item);

        if(allNuggetTags == null)
            allNuggetTags = tag(Tags.Items.NUGGETS);

        allNuggetTags.add(item);
    }

    @Override
    public String getName() {
        return "Tinkers' Mechworks Item Tags";
    }
}
