package tmechworks.lib;

import net.minecraftforge.common.Configuration;

import tmechworks.lib.Repo;

public class ConfigCore
{
    
    // ITEMS
    public static int itemID_lengthWire = 12000;
    public static int itemID_spoolWire = 12001;
    
    // BLOCKS
    public static int blockID_signalBus = 3000;
    public static int blockID_signalTerminal = 3001;
    public static int blockID_redstoneMachine = 3226; // Default to TCon value
    
    public static void loadConfig (Configuration conf)
    {
        Repo.logger.info("Loading configuration...");
        conf.load();
        
        loadItems(conf);
        loadBlocks(conf);
        
        conf.save();
        Repo.logger.info("Done.");
    }
    
    private static void loadItems (Configuration conf)
    {
        itemID_lengthWire = conf.getItem("Signals", "LengthWire", itemID_lengthWire).getInt();
        itemID_spoolWire = conf.getItem("Signals", "SpoolWire", itemID_spoolWire).getInt();
    }
    
    private static void loadBlocks (Configuration conf)
    {
        blockID_redstoneMachine = conf.getBlock("Machines", "Redstone", blockID_redstoneMachine).getInt();
        blockID_signalBus = conf.getBlock("Signals", "SignalBus", blockID_signalBus).getInt();
        blockID_signalTerminal = conf.getBlock("Signals", "SignalTerminal", blockID_signalTerminal).getInt();
    }
}
