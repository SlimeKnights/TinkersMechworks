package slimeknights.tmechworks.client;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.registries.GameData;
import slimeknights.mantle.item.ItemBlockMeta;
import slimeknights.tmechworks.common.CommonProxy;
import slimeknights.tmechworks.common.MechworksContent;

public class ClientProxy extends CommonProxy
{
    public void preInit ()
    {
        super.preInit();
        registerModels();
    }

    public void init ()
    {
        super.init();
    }

    public void postInit ()
    {
        super.postInit();
    }

    protected void registerModels ()
    {
        MechworksContent.ingots.registerItemModels();
        MechworksContent.nuggets.registerItemModels();

        registerItemBlockMeta(MechworksContent.metals);
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

    protected void registerItemBlock(Block block) {
        if (block != null) {
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(block.getRegistryName().toString(), "inventory"));
        }
    }
}
