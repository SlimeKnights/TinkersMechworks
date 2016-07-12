package slimeknights.tmechworks.common;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;
import slimeknights.mantle.block.EnumBlock;
import slimeknights.mantle.client.CreativeTab;
import slimeknights.mantle.item.ItemBlockMeta;
import slimeknights.tmechworks.blocks.Drawbridge;
import slimeknights.tmechworks.blocks.Firestarter;
import slimeknights.tmechworks.blocks.logic.DrawbridgeLogic;
import slimeknights.tmechworks.blocks.logic.FirestarterLogic;
import slimeknights.tmechworks.library.Util;

import java.util.Locale;

public class MechworksContent
{

    // Items

    // Blocks
    public static Block drawbridge;
    public static Block firestarter;

    // Tabs
    public static CreativeTab tabMechworks = new CreativeTab("TabMechworks", new ItemStack(Items.POISONOUS_POTATO));

    public MechworksContent ()
    {
        registerItems();
        registerBlocks();
        setupCreativeTabs();
    }

    private void registerItems ()
    {

    }

    private void registerBlocks ()
    {
        drawbridge = new Drawbridge().setCreativeTab(tabMechworks);
        registerBlock(drawbridge, "drawbridge");
        registerTE(DrawbridgeLogic.class, "drawbridge");

        firestarter = new Firestarter().setCreativeTab(tabMechworks);
        registerBlock(firestarter, "firestarter");
        registerTE(FirestarterLogic.class, "firestarter");
    }

    private void setupCreativeTabs ()
    {
        tabMechworks.setDisplayIcon(new ItemStack(drawbridge));
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
