package slimeknights.tmechworks.common;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import slimeknights.mantle.client.CreativeTab;
import slimeknights.mantle.common.IRegisterUtil;
import slimeknights.tmechworks.TMechworks;
import slimeknights.tmechworks.common.blocks.MetalBlock;
import slimeknights.tmechworks.common.items.MechworksBookItem;

@ObjectHolder(TMechworks.modId)
public class TMechContent implements IRegisterUtil {
    private Logger log = LogManager.getLogger(TMechworks.modId + ".content");

    // Creative tabs
    public static CreativeTab tabMechworks = new CreativeTab("TinkersMechworks", new ItemStack(Items.LIME_BANNER));

    // Blocks
    public static final MetalBlock aluminum_block = null;
    public static final MetalBlock copper_block = null;

    // Items
    public static final MechworksBookItem book = null;

    @Override
    public String getModId() {
        return TMechworks.modId;
    }

    @SubscribeEvent
    public void registerBlocks(final RegistryEvent.Register<Block> event){
        IForgeRegistry<Block> registry = event.getRegistry();

        register(registry, new MetalBlock(), "aluminum_block");
        register(registry, new MetalBlock(), "copper_block");
    }

    @SubscribeEvent
    public void registerItems(final RegistryEvent.Register<Item> event){
        IForgeRegistry<Item> registry = event.getRegistry();

        register(registry, new MechworksBookItem(), "book");

        // Metals
        registerBlockItem(registry, copper_block, tabMechworks);
        registerBlockItem(registry, aluminum_block, tabMechworks);
        register(registry, new Item(new Item.Properties().group(tabMechworks)), "copper_ingot");
        register(registry, new Item(new Item.Properties().group(tabMechworks)), "aluminum_ingot");
        register(registry, new Item(new Item.Properties().group(tabMechworks)), "copper_nugget");
        register(registry, new Item(new Item.Properties().group(tabMechworks)), "aluminum_nugget");
    }

//    public void registerEntities(final RegistryEvent.Register<EntityType<?>> event){}

    public void preInit(FMLCommonSetupEvent event){

    }

    public void init(InterModEnqueueEvent event){

    }

    public void postInit(InterModProcessEvent event){
        tabMechworks.setDisplayIcon(new ItemStack(book));
    }
}
