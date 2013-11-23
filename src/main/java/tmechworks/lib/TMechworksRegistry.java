package tmechworks.lib;

import java.util.logging.Logger;

import tmechworks.lib.util.TabTools;


public class TMechworksRegistry {
    public static TMechworksRegistry instance = new TMechworksRegistry();

    public static Logger logger = Logger.getLogger("TMech-API");

    /* Creative tabs */
    public static TabTools Mechworks;

}
