package slimeknights.tmechworks.data;

import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.tmechworks.TMechworks;

import javax.annotation.Nonnull;
import java.util.stream.Collectors;

import static slimeknights.tmechworks.common.MechworksContent.Blocks.*;

public class BlockLootTables extends net.minecraft.data.loot.BlockLoot {
    @Nonnull
    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ForgeRegistries.BLOCKS.getValues().stream()
                .filter(block -> TMechworks.modId.equals(block.getRegistryName().getNamespace()))
                .collect(Collectors.toList());
    }

    @Override
    protected void addTables() {
        noDrop(firestarter.get());
        noDrop(drawbridge.get());

        dropSelf(aluminum_block.get());
        dropSelf(aluminum_ore.get());
        dropSelf(deepslate_aluminum_ore.get());
    }

    private void noDrop(Block block) {
        add(block, noDrop());
    }
}
