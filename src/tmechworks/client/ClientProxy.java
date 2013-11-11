package tmechworks.client;

import tmechworks.client.block.SignalBusRender;
import tmechworks.client.block.SignalTerminalRender;
import tmechworks.common.CommonProxy;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {
    public void registerTickHandler ()
    {
    	super.registerTickHandler();
    }

    /* Registers any rendering code. */
    public void registerRenderer ()
    {
        RenderingRegistry.registerBlockHandler(new SignalBusRender());
        RenderingRegistry.registerBlockHandler(new SignalTerminalRender());

    }

}
