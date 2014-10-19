package tmechworks.lib;

import net.minecraftforge.common.config.Configuration;
import tmechworks.TMechworks;

public class ConfigCore
{

    public static void loadConfig (Configuration conf)
    {
        TMechworks.logger.info("Loading configuration...");
        conf.load();

        loadItems(conf);
        loadBlocks(conf);

        conf.save();
        TMechworks.logger.info("Done.");
    }

    private static void loadItems (Configuration conf)
    {
    }

    private static void loadBlocks (Configuration conf)
    {
    }
}
