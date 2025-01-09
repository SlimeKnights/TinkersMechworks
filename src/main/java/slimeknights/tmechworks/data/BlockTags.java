package slimeknights.tmechworks.data;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.TagsProvider;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import slimeknights.tmechworks.TMechworks;
import slimeknights.tmechworks.common.MechworksContent;
import slimeknights.tmechworks.common.MechworksTags;

import static net.minecraft.tags.BlockTags.*;

public class BlockTags extends BlockTagsProvider {
    private TagsProvider.TagAppender<Block> allVanillaOreTags;
    private TagsProvider.TagAppender<Block> allMechworksOreTags;
    private TagsProvider.TagAppender<Block> allStorageBlockTags;

    public BlockTags(DataGenerator gen, ExistingFileHelper helper) {
        super(gen, TMechworks.modId, helper);
    }

    @Override
    protected void addTags() {
        tag(MechworksTags.Blocks.DRAWBRIDGE_BLACKLIST);
        tag(MechworksTags.Blocks.FIRESTARTER_WHITELIST)
                .add(Blocks.FIRE)
                .add(Blocks.NETHER_PORTAL);

        addOre(MechworksTags.Blocks.ORES_ALUMINUM, MechworksContent.Blocks.aluminum_ore.get());

        addStorageBlock(MechworksTags.Blocks.STORAGE_BLOCKS_ALUMINUM, MechworksContent.Blocks.aluminum_block.get());

        tag(MINEABLE_WITH_PICKAXE).add(
                MechworksContent.Blocks.aluminum_ore.get(),
                MechworksContent.Blocks.deepslate_aluminum_ore.get(),
                MechworksContent.Blocks.aluminum_block.get(),
                MechworksContent.Blocks.drawbridge.get(),
                MechworksContent.Blocks.firestarter.get()
        );

        tag(NEEDS_STONE_TOOL).add(
                MechworksContent.Blocks.aluminum_ore.get(),
                MechworksContent.Blocks.deepslate_aluminum_ore.get(),
                MechworksContent.Blocks.aluminum_block.get(),
                MechworksContent.Blocks.drawbridge.get(),
                MechworksContent.Blocks.firestarter.get()
        );
    }

    private void addOre(TagKey<Block> tag, Block... block) {
        tag(tag).add(block);

        if(allVanillaOreTags == null)
            allVanillaOreTags = tag(Tags.Blocks.ORES);
        if(allMechworksOreTags == null)
            allMechworksOreTags = tag(MechworksTags.Blocks.ORES_ALL);

        allVanillaOreTags.add(block);
        allMechworksOreTags.add(block);
    }

    private void addStorageBlock(TagKey<Block> tag, Block... block) {
        tag(tag).add(block);

        if(allStorageBlockTags == null)
            allStorageBlockTags = tag(Tags.Blocks.STORAGE_BLOCKS);

        allStorageBlockTags.add(block);
    }

    @Override
    public String getName() {
        return "Tinkers' Mechworks Block Tags";
    }
}
