package slimeknights.tmechworks.data;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.TagsProvider;
import net.minecraft.tags.ITag;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import slimeknights.tmechworks.TMechworks;
import slimeknights.tmechworks.common.MechworksContent;
import slimeknights.tmechworks.common.MechworksTags;

public class BlockTags extends BlockTagsProvider {
    private TagsProvider.Builder<Block> allOreTags;
    private TagsProvider.Builder<Block> allStorageBlockTags;

    public BlockTags(DataGenerator gen, ExistingFileHelper helper) {
        super(gen, TMechworks.modId, helper);
    }

    @Override
    protected void registerTags() {
        getOrCreateBuilder(MechworksTags.Blocks.DRAWBRIDGE_BLACKLIST);
        getOrCreateBuilder(MechworksTags.Blocks.FIRESTARTER_WHITELIST)
                .add(Blocks.FIRE)
                .add(Blocks.NETHER_PORTAL);

        addOre(MechworksTags.Blocks.ORES_ALUMINUM, MechworksContent.Blocks.aluminum_ore.get());
        addOre(MechworksTags.Blocks.ORES_COPPER, MechworksContent.Blocks.copper_ore.get());

        addStorageBlock(MechworksTags.Blocks.STORAGE_BLOCKS_ALUMINUM, MechworksContent.Blocks.aluminum_block.get());
        addStorageBlock(MechworksTags.Blocks.STORAGE_BLOCKS_COPPER, MechworksContent.Blocks.copper_block.get());
    }

    private void addOre(ITag.INamedTag<Block> tag, Block... block) {
        getOrCreateBuilder(tag).add(block);

        if(allOreTags == null)
            allOreTags = getOrCreateBuilder(Tags.Blocks.ORES);

        allOreTags.add(block);
    }

    private void addStorageBlock(ITag.INamedTag<Block> tag, Block... block) {
        getOrCreateBuilder(tag).add(block);

        if(allStorageBlockTags == null)
            allStorageBlockTags = getOrCreateBuilder(Tags.Blocks.STORAGE_BLOCKS);

        allStorageBlockTags.add(block);
    }

    @Override
    public String getName() {
        return "Tinkers' Mechworks Block Tags";
    }
}
