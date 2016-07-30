package slimeknights.tmechworks.common;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;
import slimeknights.mantle.block.EnumBlock;
import slimeknights.mantle.client.CreativeTab;
import slimeknights.mantle.item.ItemBlockMeta;
import slimeknights.mantle.item.ItemMetaDynamic;
import slimeknights.tmechworks.blocks.Drawbridge;
import slimeknights.tmechworks.blocks.Firestarter;
import slimeknights.tmechworks.blocks.IEnumBlock;
import slimeknights.tmechworks.blocks.Metal;
import slimeknights.tmechworks.blocks.logic.DrawbridgeLogic;
import slimeknights.tmechworks.blocks.logic.FirestarterLogic;
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

    public MechworksContent ()
    {
        registerItems();
        registerBlocks();
        setupCreativeTabs();

        MechRecipes.register();
    }

    private void registerItems ()
    {
        ingots = registerItem(new ItemMetaDynamic(), "ingots");
        ingots.setCreativeTab(tabMechworks);
        nuggets = registerItem(new ItemMetaDynamic(), "nuggets");
        nuggets.setCreativeTab(tabMechworks);

        ingotAluminum = ingots.addMeta(0, "aluminum");
        nuggetAluminum = nuggets.addMeta(0, "aluminum");
        ingotCopper = ingots.addMeta(1, "copper");
        nuggetCopper = nuggets.addMeta(1, "copper");

    }

    private void registerBlocks ()
    {
        metals = registerEnumBlock(new Metal(), "metal");
        blockAluminum = new ItemStack(metals, 1, Metal.MetalTypes.ALUMINUM.getMeta());
        blockCopper = new ItemStack(metals, 1, Metal.MetalTypes.COPPER.getMeta());

        drawbridge = registerEnumBlock(new Drawbridge(), "drawbridge");
        drawbridge.setCreativeTab(tabMechworks);
        registerTE(DrawbridgeLogic.class, "drawbridge");

        firestarter = registerEnumBlock(new Firestarter(), "firestarter");
        firestarter.setCreativeTab(tabMechworks);
        registerTE(FirestarterLogic.class, "firestarter");
    }

    private void setupCreativeTabs ()
    {
        tabMechworks.setDisplayIcon(new ItemStack(drawbridge, 1, 0));
    }

    protected static <T extends Item> T registerItem (T item, String name)
    {
        if (!name.equals(name.toLowerCase(Locale.US)))
        {
            throw new IllegalArgumentException(String.format("Unlocalized names need to be all lowercase! Item: %s", name));
        }

        item.setUnlocalizedName(Util.prefix(name));
        item.setRegistryName(Util.getResource(name));
        GameRegistry.register(item);
        return item;
    }

    protected static <T extends Block> T registerBlock (T block, String name)
    {
        ItemBlock itemBlock = new ItemBlockMeta(block);
        registerBlock(block, itemBlock, name);
        return block;
    }

    protected static <T extends EnumBlock<?>> T registerEnumBlock (T block, String name)
    {
        registerBlock(block, new ItemBlockMeta(block), name);
        ItemBlockMeta.setMappingProperty(block, block.prop);
        return block;
    }

    protected static <T extends IEnumBlock<?>> T registerEnumBlock (T block, String name)
    {
        registerBlock(block.getSelf(), new ItemBlockMeta(block.getSelf()), name);
        ItemBlockMeta.setMappingProperty(block.getSelf(), block.getProperty());
        return block;
    }

    protected static <T extends Block> T registerBlock (ItemBlock itemBlock, String name)
    {
        Block block = itemBlock.getBlock();
        return (T) registerBlock(block, itemBlock, name);
    }

    protected static <T extends Block> T registerBlock (T block, String name, IProperty<?> property)
    {
        ItemBlockMeta itemBlock = new ItemBlockMeta(block);
        registerBlock(block, itemBlock, name);
        ItemBlockMeta.setMappingProperty(block, property);
        return block;
    }

    protected static <T extends Block> T registerBlock (T block, ItemBlock itemBlock, String name)
    {
        if (!name.equals(name.toLowerCase(Locale.US)))
        {
            throw new IllegalArgumentException(String.format("Unlocalized names need to be all lowercase! Block: %s", name));
        }

        String prefixedName = Util.prefix(name);
        block.setUnlocalizedName(prefixedName);
        itemBlock.setUnlocalizedName(prefixedName);

        register(block, name);
        register(itemBlock, name);
        return block;
    }

    protected static <T extends Block> T registerBlockNoItem (T block, String name)
    {
        if (!name.equals(name.toLowerCase(Locale.US)))
        {
            throw new IllegalArgumentException(String.format("Unlocalized names need to be all lowercase! Block: %s", name));
        }

        String prefixedName = Util.prefix(name);
        block.setUnlocalizedName(prefixedName);

        register(block, name);
        return block;
    }

    protected static <T extends IForgeRegistryEntry<?>> T register (T thing, String name)
    {
        thing.setRegistryName(Util.getResource(name));
        GameRegistry.register(thing);
        return thing;
    }

    protected static void registerTE (Class<? extends TileEntity> teClazz, String name)
    {
        if (!name.equals(name.toLowerCase(Locale.US)))
        {
            throw new IllegalArgumentException(String.format("Unlocalized names need to be all lowercase! TE: %s", name));
        }

        GameRegistry.registerTileEntity(teClazz, Util.prefix(name));
    }
}
