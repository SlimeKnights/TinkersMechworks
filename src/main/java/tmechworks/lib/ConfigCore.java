package tmechworks.lib;

import net.minecraftforge.common.config.Configuration;

public class ConfigCore
{

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
    }

    private static void loadBlocks (Configuration conf)
    {
    }
}
