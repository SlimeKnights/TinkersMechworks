package tmechworks.common;

import tmechworks.lib.multiblock.MultiblockServerTickHandler;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class CommonProxy {

    public void registerTickHandler ()
    {
    	TickRegistry.registerScheduledTickHandler(new MultiblockServerTickHandler(), Side.SERVER);
    }
    
    public void registerRenderer ()
    {
    }
    public void postInit ()
    {
    }
}
