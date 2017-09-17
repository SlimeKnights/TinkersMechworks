package slimeknights.tmechworks.common;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.*;
import slimeknights.mantle.block.*;
import slimeknights.mantle.client.CreativeTab;
import slimeknights.mantle.item.*;
import slimeknights.tmechworks.TMechworks;
import slimeknights.tmechworks.blocks.Drawbridge;
import slimeknights.tmechworks.blocks.Firestarter;
import slimeknights.tmechworks.blocks.IEnumBlock;
import slimeknights.tmechworks.blocks.Metal;
import slimeknights.tmechworks.blocks.logic.DrawbridgeLogic;
import slimeknights.tmechworks.blocks.logic.ExtendedDrawbridgeLogic;
import slimeknights.tmechworks.blocks.logic.FirestarterLogic;
import slimeknights.tmechworks.items.ItemBlockMetaExtra;
import slimeknights.tmechworks.library.Util;

import java.util.Locale;

public class MechworksContent
{

    // Items
    public static ItemMetaDynamic ingots;
    public static ItemMetaDynamic nuggets;

    // Blocks
    public static Metal metals;
    public static Drawbridge drawbridge;
    public static Firestarter firestarter;

    // Tabs
    public static CreativeTab tabMechworks = new CreativeTab("TabMechworks", new ItemStack(Items.POISONOUS_POTATO));

    // Ingot ItemStacks
    public static ItemStack ingotAluminum;
    public static ItemStack ingotCopper;

    // Nugget ItemStacks
    public static ItemStack nuggetAluminum;
    public static ItemStack nuggetCopper;

    // Block ItemStacks
    public static ItemStack blockAluminum;
    public static ItemStack blockCopper;

    @SubscribeEvent
    public void registerItems (RegistryEvent.Register<Item> event)
    {
        IForgeRegistry<Item> registry = event.getRegistry();

        // Item Blocks
        metals = registerEnumItemBlock(registry, metals);
        blockAluminum = new ItemStack(metals, 1, Metal.MetalTypes.ALUMINUM.getMeta());
        blockCopper = new ItemStack(metals, 1, Metal.MetalTypes.COPPER.getMeta());

        drawbridge = registerEnumItemBlockExtra(registry, drawbridge);
        firestarter = registerEnumItemBlockExtra(registry, firestarter, "extinguish=true", "facing=inv");

        ingots = registerItem(registry, new ItemMetaDynamic(), "ingots");
        ingots.setCreativeTab(tabMechworks);
        nuggets = registerItem(registry, new ItemMetaDynamic(), "nuggets");
        nuggets.setCreativeTab(tabMechworks);

        ingotAluminum = ingots.addMeta(0, "aluminum");
        nuggetAluminum = nuggets.addMeta(0, "aluminum");
        ingotCopper = ingots.addMeta(1, "copper");
        nuggetCopper = nuggets.addMeta(1, "copper");

        setupCreativeTabs();
    }

    @SubscribeEvent
    public void registerBlocks (RegistryEvent.Register<Block> event)
    {
        IForgeRegistry<Block> registry = event.getRegistry();

        metals = registerBlock(registry, new Metal(), "metal");

        drawbridge = registerBlock(registry, new Drawbridge(), "drawbridge");
        drawbridge.setCreativeTab(tabMechworks);
        registerTE(DrawbridgeLogic.class, "drawbridge");
        registerTE(ExtendedDrawbridgeLogic.class, "drawbridge.extended");

        firestarter = registerBlock(registry, new Firestarter(), "firestarter");
        firestarter.setCreativeTab(tabMechworks);
        registerTE(FirestarterLogic.class, "firestarter");
    }

    @SubscribeEvent
    public void registerModels (ModelRegistryEvent event){
        TMechworks.proxy.registerModels();
    }

    private void setupCreativeTabs ()
    {
        tabMechworks.setDisplayIcon(new ItemStack(drawbridge, 1, 0));
    }

    protected static <T extends Block> T registerBlock(IForgeRegistry<Block> registry, T block, String name) {
        if(!name.equals(name.toLowerCase(Locale.US))) {
            throw new IllegalArgumentException(String.format("Unlocalized names need to be all lowercase! Block: %s", name));
        }

        String prefixedName = Util.prefix(name);
        block.setUnlocalizedName(prefixedName);

        register(registry, block, name);
        return block;
    }

    protected static <E extends Enum<E> & EnumBlock.IEnumMeta & IStringSerializable> BlockStairsBase registerBlockStairsFrom(IForgeRegistry<Block> registry, EnumBlock<E> block, E value, String name) {
        return registerBlock(registry, new BlockStairsBase(block.getDefaultState().withProperty(block.prop, value)), name);
    }

    protected static <T extends Block> T registerItemBlock(IForgeRegistry<Item> registry, T block) {

        ItemBlock itemBlock = new ItemBlockMeta(block);

        itemBlock.setUnlocalizedName(block.getUnlocalizedName());

        register(registry, itemBlock, block.getRegistryName());
        return block;
    }

    protected static <T extends EnumBlock<?>> T registerEnumItemBlock(IForgeRegistry<Item> registry, T block) {
        ItemBlock itemBlock = new ItemBlockMeta(block);

        itemBlock.setUnlocalizedName(block.getUnlocalizedName());

        register(registry, itemBlock, block.getRegistryName());
        ItemBlockMeta.setMappingProperty(block, block.prop);
        return block;
    }

    protected static <T extends EnumBlock<?>> T registerEnumItemBlockExtra(IForgeRegistry<Item> registry, T block, String... extra) {
        registerItemBlock(registry, new ItemBlockMetaExtra(block, extra));
        ItemBlockMeta.setMappingProperty(block, block.prop);
        return block;
    }

    protected static <T extends IEnumBlock<?>> T registerEnumItemBlockExtra(IForgeRegistry<Item> registry, T block, String... extra) {
        registerItemBlock(registry, new ItemBlockMetaExtra(block.getSelf(), extra));
        ItemBlockMeta.setMappingProperty(block.getSelf(), block.getProperty());
        return block;
    }

    @SuppressWarnings("unchecked")
    protected static <T extends Block> T registerItemBlock(IForgeRegistry<Item> registry, ItemBlock itemBlock) {
        itemBlock.setUnlocalizedName(itemBlock.getBlock().getUnlocalizedName());

        register(registry, itemBlock, itemBlock.getBlock().getRegistryName());
        return (T) itemBlock.getBlock();
    }

    @SuppressWarnings("unchecked")
    protected static <T extends Block> T registerItemBlockProp(IForgeRegistry<Item> registry, ItemBlock itemBlock, IProperty<?> property) {
        itemBlock.setUnlocalizedName(itemBlock.getBlock().getUnlocalizedName());

        register(registry, itemBlock, itemBlock.getBlock().getRegistryName());
        ItemBlockMeta.setMappingProperty(itemBlock.getBlock(), property);
        return (T) itemBlock.getBlock();
    }

    protected static <T extends EnumBlockSlab<?>> T registerEnumItemBlockSlab(IForgeRegistry<Item> registry, T block) {
        @SuppressWarnings({ "unchecked", "rawtypes" })
        ItemBlock itemBlock = new ItemBlockSlab(block);

        itemBlock.setUnlocalizedName(block.getUnlocalizedName());

        register(registry, itemBlock, block.getRegistryName());
        ItemBlockMeta.setMappingProperty(block, block.prop);
        return block;
    }

    /**
     * Sets the correct unlocalized name and registers the item.
     */
    protected static <T extends Item> T registerItem(IForgeRegistry<Item> registry, T item, String name) {
        if(!name.equals(name.toLowerCase(Locale.US))) {
            throw new IllegalArgumentException(String.format("Unlocalized names need to be all lowercase! Item: %s", name));
        }

        item.setUnlocalizedName(Util.prefix(name));
        item.setRegistryName(Util.getResource(name));
        registry.register(item);
        return item;
    }

    protected static <T extends IForgeRegistryEntry<T>> T register(IForgeRegistry<T> registry, T thing, String name) {
        thing.setRegistryName(Util.getResource(name));
        registry.register(thing);
        return thing;
    }

    protected static <T extends IForgeRegistryEntry<T>> T register(IForgeRegistry<T> registry, T thing, ResourceLocation name) {
        thing.setRegistryName(name);
        registry.register(thing);
        return thing;
    }

    protected static void registerTE(Class<? extends TileEntity> teClazz, String name) {
        if(!name.equals(name.toLowerCase(Locale.US))) {
            throw new IllegalArgumentException(String.format("Unlocalized names need to be all lowercase! TE: %s", name));
        }

        GameRegistry.registerTileEntity(teClazz, Util.prefix(name));
    }
}
