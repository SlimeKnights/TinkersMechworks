package tmechworks.lib;

import net.minecraftforge.common.config.Configuration;
import tmechworks.TMechworks;

public class ConfigCore
{
    public static String[] drawbridgeBlackList = new String[0];

    public static void loadConfig (Configuration conf)
    {
        TMechworks.logger.info("Loading configuration...");
        conf.load();

        loadDrawbridge(conf);

        conf.save();
        TMechworks.logger.info("Done.");
    }

    private static void loadDrawbridge (Configuration conf)
    {
        drawbridgeBlackList = conf.getStringList("blacklist", "drawbridge", drawbridgeBlackList, "Add block names that should not be placed from the drawbridge");
    }

}
