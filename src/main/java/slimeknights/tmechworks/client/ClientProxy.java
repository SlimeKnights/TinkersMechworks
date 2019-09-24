package slimeknights.tmechworks.client;

import slimeknights.tmechworks.common.CommonProxy;
import slimeknights.tmechworks.common.MechworksContent;
import slimeknights.tmechworks.common.event.ModelBakeEventListener;

public class ClientProxy extends CommonProxy {
    @Override
    public void init() {
        super.init();

        ModelBakeEventListener.registerDisguiseBlock(MechworksContent.Blocks.drawbridge.getRegistryName());
        ModelBakeEventListener.registerDisguiseBlock(MechworksContent.Blocks.firestarter.getRegistryName());
    }
}
