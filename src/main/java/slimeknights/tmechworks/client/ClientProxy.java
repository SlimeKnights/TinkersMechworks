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
    }

    public void postInit ()
    {
        super.postInit();
        registerModels();
    }

    protected void registerModels ()
    {
        MechworksContent.ingots.registerItemModels();
        ;
        MechworksContent.nuggets.registerItemModels();

        registerItemBlockMeta(MechworksContent.drawbridge);
        registerItemBlockMeta(MechworksContent.metals);
    }

    protected void registerItemBlockMeta (Block block)
    {
        if (block != null)
        {
            ((ItemBlockMeta) Item.getItemFromBlock(block)).registerItemModels();
        }
    }
}
