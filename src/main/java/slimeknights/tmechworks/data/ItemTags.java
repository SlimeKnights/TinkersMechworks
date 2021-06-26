package slimeknights.tmechworks.data;

import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.data.TagsProvider;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import slimeknights.tmechworks.TMechworks;
import slimeknights.tmechworks.common.MechworksContent;
import slimeknights.tmechworks.common.MechworksTags;

public class ItemTags extends ItemTagsProvider {
    private TagsProvider.Builder<Item> allIngotTags;
    private TagsProvider.Builder<Item> allNuggetTags;

    public ItemTags(DataGenerator dataGenerator, BlockTagsProvider blockTagProvider, ExistingFileHelper helper) {
        super(dataGenerator, blockTagProvider, TMechworks.modId, helper);
    }

    @Override
    protected void registerTags() {
        copy(Tags.Blocks.ORES, Tags.Items.ORES);
        copy(Tags.Blocks.STORAGE_BLOCKS, Tags.Items.STORAGE_BLOCKS);

        copy(MechworksTags.Blocks.ORES_ALUMINUM, MechworksTags.Items.ORES_ALUMINUM);
        copy(MechworksTags.Blocks.ORES_COPPER, MechworksTags.Items.ORES_COPPER);
        copy(MechworksTags.Blocks.STORAGE_BLOCKS_ALUMINUM, MechworksTags.Items.STORAGE_BLOCKS_ALUMINUM);
        copy(MechworksTags.Blocks.STORAGE_BLOCKS_COPPER, MechworksTags.Items.STORAGE_BLOCKS_COPPER);

        addIngot(MechworksTags.Items.INGOTS_ALUMINUM, MechworksContent.Items.aluminum_ingot.get());
        addIngot(MechworksTags.Items.INGOTS_COPPER, MechworksContent.Items.copper_ingot.get());

        addNugget(MechworksTags.Items.NUGGETS_ALUMINUM, MechworksContent.Items.aluminum_nugget.get());
        addNugget(MechworksTags.Items.NUGGETS_COPPER, MechworksContent.Items.copper_nugget.get());

        getOrCreateBuilder(net.minecraft.tags.ItemTags.LECTERN_BOOKS).add(MechworksContent.Items.book.get());
    }

    private void addIngot(ITag.INamedTag<Item> tag, Item... item) {
        getOrCreateBuilder(tag).add(item);

        if(allIngotTags == null)
            allIngotTags = getOrCreateBuilder(Tags.Items.INGOTS);

        allIngotTags.add(item);
    }

    private void addNugget(ITag.INamedTag<Item> tag, Item... item) {
        getOrCreateBuilder(tag).add(item);

        if(allNuggetTags == null)
            allNuggetTags = getOrCreateBuilder(Tags.Items.NUGGETS);

        allNuggetTags.add(item);
    }

    @Override
    public String getName() {
        return "Tinkers' Mechworks Item Tags";
    }
}
