package slimeknights.tmechworks.integration.waila;

import mcp.mobius.waila.api.IWailaRegistrar;
import slimeknights.tmechworks.TMechworks;
import slimeknights.tmechworks.blocks.logic.RedstoneMachineLogicBase;
import slimeknights.tmechworks.library.Util;

public class WailaRegistrar {
    static final String CONFIG_REDSTONE_MACHINE = Util.prefix("redstoneMachine");

    public static void wailaCallback(IWailaRegistrar registrar){
        registrar.addConfig(TMechworks.modName, CONFIG_REDSTONE_MACHINE, true);

        registrar.registerBodyProvider(new GenericTileDataProvider(CONFIG_REDSTONE_MACHINE), RedstoneMachineLogicBase.class);
    }
}
