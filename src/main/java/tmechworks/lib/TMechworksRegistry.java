package tmechworks.lib;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import tconstruct.common.TRepo;
import tmechworks.lib.blocks.PlacementType;
import tmechworks.lib.util.TabTools;

import com.google.common.collect.HashBiMap;

public class TMechworksRegistry
{
    public static TMechworksRegistry instance = new TMechworksRegistry();

    public static Logger logger = LogManager.getLogger("TMech-API");

    /* Creative tabs */
    public static TabTools Mechworks;

    /* Used to determine how blocks are laid out in the drawbridge
     * 0: Metadata has to match
     * 1: Metadata has no meaning
     * 2: Should not be placed
     * 3: Has rotational metadata
     * 4: Rails
     * 5: Has rotational TileEntity data
     * 6: Custom placement logic
     */
    public static HashMap<ItemStack, PlacementType> drawbridgeState = new HashMap<ItemStack, PlacementType>();
    /** Blocks that are interchangable with each other. Ex: Still and flowing water */
    public static HashMap<Block, Block> interchangableBlockMapping = new HashMap<Block, Block>();
    /** Blocks that place items, and vice versa */
    public static HashBiMap<Block, Item> blockToItemMapping = HashBiMap.create();;

    /*
     * Used to determine how blocks are laid out in the drawbridge 0: Metadata
     * has to match 1: Metadata has no meaning 2: Should not be placed 3: Has
     * rotational metadata 4: Rails 5: Has rotational TileEntity data 6: Custom
     * placement logic
     */

    // moved to TMech
    // public static HashMap<ItemStack, Integer> drawbridgeState = new
    // HashMap<ItemStack, Integer>();
    /*
     * Blocks that are interchangable with each other. Ex: Still and flowing
     * water
     */
    // static HashMap<Block, Block> interchangableBlockMapping = new
    // HashMap<Block, Block> ();
    /* Blocks that place items, and vice versa */
    // public static HashBiMap<Block, Item> blockToItemMapping;

    static void initializeDrawbridgeState ()
    {
        // TODO fix this mess and move to TMech
        /*0: Metadata has to match            PlacementType.metaMatch
        1: Metadata has no meaning          PlacementType.metaIgnore
        2: Should not be placed             PlacementType.GTFO
        3: Has rotational metadata          PlacementType.rotationalMeta
        4: Rails                            PlacementType.rails
        5: Has rotational TileEntity data   PlacementType.rotationalTE
        6: Custom placement logic           PlacementType.custom*/

        drawbridgeState.put(new ItemStack(Blocks.stone), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.grass), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.dirt), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.cobblestone), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.bedrock), PlacementType.GTFO);
        drawbridgeState.put(new ItemStack(Blocks.water), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.lava), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.sand), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.gravel), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.gold_ore), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.iron_ore), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.coal_ore), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.sponge), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.lapis_ore), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.lapis_block), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.dispenser), PlacementType.rotationalMeta);
        drawbridgeState.put(new ItemStack(Blocks.jukebox), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.bed), PlacementType.GTFO);
        drawbridgeState.put(new ItemStack(Blocks.golden_rail), PlacementType.rails);
        drawbridgeState.put(new ItemStack(Blocks.detector_rail), PlacementType.rails);
        drawbridgeState.put(new ItemStack(Blocks.sticky_piston), PlacementType.rotationalMeta);
        drawbridgeState.put(new ItemStack(Blocks.web), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.piston), PlacementType.rotationalMeta);
        drawbridgeState.put(new ItemStack(Blocks.piston_extension), PlacementType.GTFO);
        drawbridgeState.put(new ItemStack(Blocks.yellow_flower), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.red_flower), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.brown_mushroom_block), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.red_mushroom_block), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.gold_block), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.iron_block), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.brick_block), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.tnt), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.bookshelf), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.mossy_cobblestone), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.obsidian), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.torch), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.fire), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.mob_spawner), PlacementType.GTFO);
        drawbridgeState.put(new ItemStack(Blocks.oak_stairs), PlacementType.rotationalMeta);
        drawbridgeState.put(new ItemStack(Blocks.chest), PlacementType.rotationalTE);
        drawbridgeState.put(new ItemStack(Blocks.redstone_wire), PlacementType.metaIgnore);
        blockToItemMapping.put(Blocks.redstone_wire, Items.redstone);
        drawbridgeState.put(new ItemStack(Blocks.diamond_ore), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.diamond_block), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.crafting_table), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.wheat), PlacementType.GTFO);
        drawbridgeState.put(new ItemStack(Blocks.farmland), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.furnace), PlacementType.rotationalMeta);
        drawbridgeState.put(new ItemStack(Blocks.lit_furnace), PlacementType.rotationalMeta);
        interchangableBlockMapping.put(Blocks.furnace, Blocks.lit_furnace);

        drawbridgeState.put(new ItemStack(Blocks.farmland), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.standing_sign), PlacementType.rotationalMeta);
        drawbridgeState.put(new ItemStack(Blocks.wooden_door), PlacementType.GTFO);
        drawbridgeState.put(new ItemStack(Blocks.ladder), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.rail), PlacementType.rails);
        drawbridgeState.put(new ItemStack(Blocks.stone_stairs), PlacementType.rotationalMeta);
        drawbridgeState.put(new ItemStack(Blocks.wall_sign), PlacementType.rotationalMeta);
        drawbridgeState.put(new ItemStack(Blocks.lever), PlacementType.rotationalMeta);
        drawbridgeState.put(new ItemStack(Blocks.stone_pressure_plate), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.iron_door), PlacementType.GTFO);
        drawbridgeState.put(new ItemStack(Blocks.wooden_pressure_plate), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.redstone_ore), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.lit_redstone_ore), PlacementType.metaIgnore);
        interchangableBlockMapping.put(Blocks.redstone_ore, Blocks.lit_redstone_ore);
        drawbridgeState.put(new ItemStack(Blocks.redstone_torch), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.unlit_redstone_torch), PlacementType.metaIgnore);
        interchangableBlockMapping.put(Blocks.redstone_torch, Blocks.unlit_redstone_torch);

        drawbridgeState.put(new ItemStack(Blocks.stone_button), PlacementType.rotationalMeta);
        drawbridgeState.put(new ItemStack(Blocks.snow), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.ice), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.snow), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.cactus), PlacementType.GTFO);
        drawbridgeState.put(new ItemStack(Blocks.clay), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.reeds), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.jukebox), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.fence), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.pumpkin), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.netherrack), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.soul_sand), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.glowstone), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.portal), PlacementType.GTFO);
        drawbridgeState.put(new ItemStack(Blocks.lit_pumpkin), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.cake), PlacementType.GTFO);
        drawbridgeState.put(new ItemStack(Blocks.unpowered_repeater), PlacementType.rotationalMeta);
        drawbridgeState.put(new ItemStack(Blocks.powered_repeater), PlacementType.rotationalMeta);
        interchangableBlockMapping.put(Blocks.unpowered_repeater, Blocks.powered_repeater);
        interchangableBlockMapping.put(Blocks.powered_repeater, Blocks.unpowered_repeater);
        blockToItemMapping.put(Blocks.unpowered_repeater, Items.repeater);
        blockToItemMapping.put(Blocks.powered_repeater, Items.repeater);
        drawbridgeState.put(new ItemStack(Blocks.trapdoor), PlacementType.rotationalMeta);
        drawbridgeState.put(new ItemStack(Blocks.brown_mushroom), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.red_mushroom), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.iron_bars), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.glass_pane), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.melon_block), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.pumpkin_stem), PlacementType.GTFO);
        drawbridgeState.put(new ItemStack(Blocks.melon_stem), PlacementType.GTFO);
        drawbridgeState.put(new ItemStack(Blocks.vine), PlacementType.rotationalMeta);
        drawbridgeState.put(new ItemStack(Blocks.fence_gate), PlacementType.rotationalMeta);
        drawbridgeState.put(new ItemStack(Blocks.brick_stairs), PlacementType.rotationalMeta);
        drawbridgeState.put(new ItemStack(Blocks.stone_brick_stairs), PlacementType.rotationalMeta);
        drawbridgeState.put(new ItemStack(Blocks.mycelium), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.waterlily), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.nether_brick), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.nether_brick_fence), PlacementType.rotationalMeta);
        drawbridgeState.put(new ItemStack(Blocks.nether_wart), PlacementType.GTFO);
        drawbridgeState.put(new ItemStack(Blocks.enchanting_table), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.brewing_stand), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.cauldron), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.end_portal), PlacementType.GTFO);
        drawbridgeState.put(new ItemStack(Blocks.dragon_egg), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.redstone_lamp), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.cocoa), PlacementType.GTFO);
        drawbridgeState.put(new ItemStack(Blocks.sandstone_stairs), PlacementType.rotationalMeta);
        drawbridgeState.put(new ItemStack(Blocks.emerald_ore), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.ender_chest), PlacementType.rotationalTE);
        drawbridgeState.put(new ItemStack(Blocks.tripwire_hook), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.tripwire_hook), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.emerald_block), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.spruce_stairs), PlacementType.rotationalMeta);
        drawbridgeState.put(new ItemStack(Blocks.birch_stairs), PlacementType.rotationalMeta);
        drawbridgeState.put(new ItemStack(Blocks.jungle_stairs), PlacementType.rotationalMeta);
        drawbridgeState.put(new ItemStack(Blocks.command_block), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.beacon), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.cobblestone_wall), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.flower_pot), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.carrots), PlacementType.GTFO);
        drawbridgeState.put(new ItemStack(Blocks.potatoes), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.wooden_button), PlacementType.rotationalMeta);
        drawbridgeState.put(new ItemStack(Blocks.skull), PlacementType.GTFO);
        drawbridgeState.put(new ItemStack(Blocks.trapped_chest), PlacementType.rotationalTE);
        drawbridgeState.put(new ItemStack(Blocks.light_weighted_pressure_plate), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.heavy_weighted_pressure_plate), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.unpowered_comparator), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.powered_comparator), PlacementType.metaIgnore);
        interchangableBlockMapping.put(Blocks.unpowered_comparator, Blocks.powered_comparator);
        interchangableBlockMapping.put(Blocks.powered_comparator, Blocks.unpowered_comparator);
        blockToItemMapping.put(Blocks.unpowered_comparator, Items.comparator);
        blockToItemMapping.put(Blocks.powered_comparator, Items.comparator);
        drawbridgeState.put(new ItemStack(Blocks.daylight_detector), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.redstone_block), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.quartz_ore), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.hopper), PlacementType.rotationalMeta);
        drawbridgeState.put(new ItemStack(Blocks.quartz_block), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(Blocks.quartz_stairs), PlacementType.rotationalMeta);
        drawbridgeState.put(new ItemStack(Blocks.activator_rail), PlacementType.rails);
        drawbridgeState.put(new ItemStack(Blocks.dropper), PlacementType.rotationalMeta);
        interchangableBlockMapping.put(Blocks.dirt, Blocks.grass);
        interchangableBlockMapping.put(Blocks.grass, Blocks.dirt);
        
        //TCON STUFFS
        drawbridgeState.put(new ItemStack(TRepo.slimePad), PlacementType.metaIgnore);
        drawbridgeState.put(new ItemStack(TRepo.bloodChannel), PlacementType.metaIgnore);

    }

    static
    {
        initializeDrawbridgeState();
    }

}
