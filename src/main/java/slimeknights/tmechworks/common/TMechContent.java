package slimeknights.tmechworks.common;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
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
import slimeknights.tmechworks.common.blocks.FirestarterBlock;
import slimeknights.tmechworks.common.blocks.MetalBlock;
import slimeknights.tmechworks.common.blocks.tileentity.FirestarterTileEntity;
import slimeknights.tmechworks.common.items.MechworksBookItem;

import java.util.function.Supplier;

@ObjectHolder(TMechworks.modId)
public class TMechContent implements IRegisterUtil {
    private Logger log = LogManager.getLogger(TMechworks.modId + ".content");

    // Creative tabs
    public static CreativeTab tabMechworks = new CreativeTab("TinkersMechworks", new ItemStack(Items.LIME_BANNER));

    // Blocks
    public static final MetalBlock aluminum_block = null;
    public static final MetalBlock copper_block = null;
    public static final FirestarterBlock firestarter = null;
    public static final FirestarterBlock firestarter_keeplit = null;

    // Items
    public static final MechworksBookItem book = null;

    // Tile Entities
    public static final TileEntityType<?> firestarter_te = null;

    @Override
    public String getModId() {
        return TMechworks.modId;
    }

    @SubscribeEvent
    public void registerBlocks(final RegistryEvent.Register<Block> event){
        IForgeRegistry<Block> registry = event.getRegistry();

        register(registry, new MetalBlock(), "aluminum_block");
        register(registry, new MetalBlock(), "copper_block");

        // Machines
        register(registry, new FirestarterBlock(true), "firestarter");
        register(registry, new FirestarterBlock(false), "firestarter_keeplit");
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

        // Machines
        registerBlockItem(registry, firestarter, tabMechworks);
        registerBlockItem(registry, firestarter_keeplit, tabMechworks);
    }

    @SubscribeEvent
    public void registerTileEntities(final RegistryEvent.Register<TileEntityType<?>> event) {
        IForgeRegistry<TileEntityType<?>> registry = event.getRegistry();

        register(registry, TileEntityType.Builder.create((Supplier<TileEntity>) FirestarterTileEntity::new, firestarter).build(null), "firestarter_te");
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
