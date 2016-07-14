package slimeknights.tmechworks.client;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import slimeknights.mantle.item.ItemBlockMeta;
import slimeknights.tmechworks.common.CommonProxy;
import slimeknights.tmechworks.common.MechworksContent;

public class ClientProxy extends CommonProxy
{

    public void preInit ()
    {
        super.preInit();
    }

    public void init ()
    {
        super.init();
        registerModels();
    }

    public void postInit ()
    {
        super.postInit();
    }

    protected void registerModels ()
    {
        registerItemBlockMeta(MechworksContent.drawbridge);
        registerItemBlockMeta(MechworksContent.firestarter);
    }

    protected void registerItemBlockMeta (Block block)
    {
        if (block != null)
        {
            ((ItemBlockMeta) Item.getItemFromBlock(block)).registerItemModels();
        }
    }
}
