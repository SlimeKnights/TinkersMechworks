package tmechworks.common;

import mantle.lib.client.MantleClientRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import tconstruct.library.TConstructRegistry;
import tmechworks.blocks.DynamoBlock;
import tmechworks.blocks.FilterBlock;
import tmechworks.blocks.RedstoneMachine;
import tmechworks.blocks.SignalBus;
import tmechworks.blocks.SignalTerminal;
import tmechworks.blocks.logic.AdvancedDrawbridgeLogic;
import tmechworks.blocks.logic.DrawbridgeLogic;
import tmechworks.blocks.logic.DynamoLogic;
import tmechworks.blocks.logic.FilterLogic;
import tmechworks.blocks.logic.FineFilter;
import tmechworks.blocks.logic.FirestarterLogic;
import tmechworks.blocks.logic.MeshFilter;
import tmechworks.blocks.logic.SignalBusLogic;
import tmechworks.blocks.logic.SignalTerminalLogic;
import tmechworks.blocks.logic.SlatFilter;
import tmechworks.blocks.logic.SubFilter;
import tmechworks.items.LengthWire;
import tmechworks.items.SpoolOfWire;
import tmechworks.items.blocks.ItemBlockWithMetadata;
import tmechworks.items.blocks.RedstoneMachineItem;
import tmechworks.items.blocks.SignalBusItem;
import tmechworks.items.blocks.SignalTerminalItem;
import tmechworks.lib.TMechworksRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

public class MechContent
{

    public MechContent()
    {
        registerItems();
        registerBlocks();
        MechRecipes.registerAllTheThings();// !
        setupToolTabs();

    }

    public void postInit ()
    {
        setupTConManual();
    }

    private void registerItems ()
    {
        // TODO: Register these with Forge
        lengthWire = new LengthWire().setUnlocalizedName("lengthwire");
        GameRegistry.registerItem(lengthWire, "LengthWire");
        spoolWire = new SpoolOfWire().setUnlocalizedName("spoolwire");
        GameRegistry.registerItem(spoolWire, "SpoolWire");

        // Fetch TCon Items
        proxyIS_alubrassIngot = TConstructRegistry.getItemStack("ingotAluminumBrass");
        proxyIS_bronzeIngot = TConstructRegistry.getItemStack("ingotBronze");

        proxyItem_blankPattern = TConstructRegistry.getItem("blankPattern");
        proxyItem_largePlate = TConstructRegistry.getItem("heavyPlate");
    }

    private void registerBlocks ()
    {
        //Redstone machines
        redstoneMachine = new RedstoneMachine().setBlockName("tmechworks.redstoneMachine");
        GameRegistry.registerBlock(redstoneMachine, RedstoneMachineItem.class, "RedstoneMachine");
        GameRegistry.registerTileEntity(DrawbridgeLogic.class, "Drawbridge");
        GameRegistry.registerTileEntity(FirestarterLogic.class, "Firestarter");
        GameRegistry.registerTileEntity(AdvancedDrawbridgeLogic.class, "AdvDrawbridge");

        dynamo = new DynamoBlock().setLightLevel(1.0F).setBlockName("tmechworks.dynamo").setBlockTextureName("tinker:compressed_alubrass");
        GameRegistry.registerBlock(dynamo, "TMechworks:Dynamo");
        GameRegistry.registerTileEntity(DynamoLogic.class, "TMechworks:Dynamo");

        //Signal blocks
        signalBus = new SignalBus().setBlockName("tmechworks.signalbus");
        GameRegistry.registerBlock(signalBus, SignalBusItem.class, "SignalBus");
        GameRegistry.registerTileEntity(SignalBusLogic.class, "SignalBus");
        signalTerminal = new SignalTerminal().setBlockName("tmechworks.signalterminal");
        GameRegistry.registerBlock(signalTerminal, SignalTerminalItem.class, "SignalTerminal");
        GameRegistry.registerTileEntity(SignalTerminalLogic.class, "SignalTerminal");

        //Inventory management
        filter = (FilterBlock) new FilterBlock().setBlockName("tmechworks.meshFilter").setBlockTextureName("tmechworks:machines/drawbridge_bottom");
        //A technical filter.
        SubFilter nilFilter = new SubFilter()
        {
            @Override
            public boolean canPass (Entity entity)
            {
                return ((entity instanceof EntityItem) || (entity instanceof EntityXPOrb));
            }

            @Override
            public boolean canPass (ItemStack itemStack)
            {
                return true;
            }
        };
        nilFilter.setSuffix("empty");
        filter.subFilters[0] = nilFilter;
        //Lets through any item or XP orb, but not other entities.
        SubFilter wideFilter = new SubFilter()
        {
            public boolean canPass (Entity entity)
            {
                return ((entity instanceof EntityItem) || (entity instanceof EntityXPOrb));
            }

            @Override
            public boolean canPass (ItemStack itemStack)
            {
                return true;
            }
        };
        wideFilter.setSuffix("wide");
        wideFilter.setMeshIconName("tmechworks:filters/widefilter");
        wideFilter.setAssociatedItem(TConstructRegistry.getItemStack("ingotBronze"));
        wideFilter.setItemMetaSensitive(false);
        filter.setSubFilter(wideFilter, 1);

        SubFilter slatFilter = new SlatFilter();
        slatFilter.setMeshIconName("tmechworks:filters/slatfilter");
        slatFilter.setAssociatedItem(TConstructRegistry.getItemStack("ingotCopper"));
        slatFilter.setItemMetaSensitive(true);
        slatFilter.setSuffix("slat");
        filter.setSubFilter(slatFilter, 2);

        SubFilter meshFilter = new MeshFilter();
        meshFilter.setMeshIconName("tmechworks:filters/meshfilter");
        meshFilter.setAssociatedItem(TConstructRegistry.getItemStack("ingotAluminum"));
        meshFilter.setItemMetaSensitive(false);
        meshFilter.setSuffix("mesh");
        filter.setSubFilter(meshFilter, 3);

        SubFilter fineFilter = new FineFilter();
        fineFilter.setMeshIconName("tmechworks:filters/finefilter");
        fineFilter.setAssociatedItem(TConstructRegistry.getItemStack("ingotAlumite"));
        fineFilter.setItemMetaSensitive(true);
        fineFilter.setSuffix("fine");
        filter.setSubFilter(fineFilter, 4);

        GameRegistry.registerBlock(filter, ItemBlockWithMetadata.class, "MeshFilter");
        GameRegistry.registerTileEntity(FilterLogic.class, "MeshFilter");
    }

    private void setupToolTabs ()
    {
        TMechworksRegistry.Mechworks.init(new ItemStack(signalTerminal, 1));

    }

    private void setupTConManual ()
    {
        ItemStack redstone = new ItemStack(Items.redstone);
        ItemStack blankCast = new ItemStack(proxyItem_blankPattern, 1, 1);

        MantleClientRegistry.registerManualLargeRecipe("drawbridge", new ItemStack(redstoneMachine, 1, 0), proxyIS_alubrassIngot, blankCast, proxyIS_alubrassIngot, proxyIS_bronzeIngot, new ItemStack(
                Blocks.dispenser), proxyIS_bronzeIngot, proxyIS_bronzeIngot, redstone, proxyIS_bronzeIngot);
        MantleClientRegistry.registerManualLargeRecipe("igniter", new ItemStack(redstoneMachine, 1, 1), proxyIS_alubrassIngot, new ItemStack(proxyItem_largePlate, 1, 7), proxyIS_alubrassIngot,
                proxyIS_bronzeIngot, new ItemStack(Items.flint_and_steel), proxyIS_bronzeIngot, proxyIS_bronzeIngot, redstone, proxyIS_bronzeIngot);

    }

    // ---- ITEMS
    // --------------------------------------------------------------------------
    public static Item lengthWire;
    public static Item spoolWire;

    // ---- BLOCKS
    // -------------------------------------------------------------------------
    public static Block redstoneMachine;
    public static Block signalBus;
    public static Block signalTerminal;
    public static Block dynamo;
    public static FilterBlock filter;

    // ---- PROXY ITEMS
    // --------------------------------------------------------------------------
    public static ItemStack proxyIS_alubrassIngot;
    public static ItemStack proxyIS_bronzeIngot;
    public static Item proxyItem_blankPattern;
    public static Item proxyItem_largePlate;

}
