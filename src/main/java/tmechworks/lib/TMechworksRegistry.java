package tmechworks.lib;

import java.util.HashMap;
import java.util.logging.Logger;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.google.common.collect.HashBiMap;

import tmechworks.lib.blocks.PlacementType;
import tmechworks.lib.util.TabTools;

public class TMechworksRegistry
{
    public static TMechworksRegistry instance = new TMechworksRegistry();

    public static Logger logger = Logger.getLogger("TMech-API");

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

}
