package tmechworks.lib;

import net.minecraftforge.common.Configuration;

public class ConfigCore
{
    
    // ITEMS
    public static int itemID_lengthWire = 12000;
    public static int itemID_spoolWire = 12001;
    
    // BLOCKS
    public static int blockID_signalBus = 3000;
    public static int blockID_signalTerminal = 3001;
    public static int blockID_dynamo = 3002;
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
        itemID_lengthWire = conf.getItem("LogicItem", "LengthWire", itemID_lengthWire).getInt();
        itemID_spoolWire = conf.getItem("LogicItem", "SpoolWire", itemID_spoolWire).getInt();
    }
    
    private static void loadBlocks (Configuration conf)
    {
        blockID_redstoneMachine = conf.getBlock("Machines", "Redstone", blockID_redstoneMachine).getInt();
        blockID_dynamo = conf.getBlock("Machines", "Dynamo", blockID_dynamo).getInt();
        blockID_signalBus = conf.getBlock("LogicBlock", "SignalBus", blockID_signalBus).getInt();
        blockID_signalTerminal = conf.getBlock("LogicBlock", "SignalTerminal", blockID_signalTerminal).getInt();
    }
}
