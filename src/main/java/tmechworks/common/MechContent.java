package tmechworks.common;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.client.TConstructClientRegistry;
import tmechworks.blocks.*;
import tmechworks.blocks.logic.*;
import tmechworks.items.*;
import tmechworks.items.blocks.*;
import tmechworks.lib.*;
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
	
	public void postInit()
	{
        setupTConManual();
	}

	private void registerItems()
	{
		// TODO: Register these with Forge
		lengthWire = new LengthWire(ConfigCore.itemID_lengthWire).setUnlocalizedName("lengthwire");
		GameRegistry.registerItem(lengthWire, "LengthWire");
		spoolWire = new SpoolOfWire(ConfigCore.itemID_spoolWire).setUnlocalizedName("spoolwire");
		GameRegistry.registerItem(spoolWire, "SpoolWire");
		
		// Fetch TCon Items
		proxyIS_alubrassIngot = TConstructRegistry.getItemStack("ingotAluminumBrass");
		proxyIS_bronzeIngot = TConstructRegistry.getItemStack("ingotBronze");
        
		proxyItem_blankPattern = TConstructRegistry.getItem("blankPattern");
		proxyItem_largePlate = TConstructRegistry.getItem("heavyPlate");
	}

	private void registerBlocks()
	{
        //Redstone machines
        redstoneMachine = new RedstoneMachine(ConfigCore.blockID_redstoneMachine).setUnlocalizedName("tmechworks.redstoneMachine");
        GameRegistry.registerBlock(redstoneMachine, RedstoneMachineItem.class, "RedstoneMachine");
        GameRegistry.registerTileEntity(DrawbridgeLogic.class, "Drawbridge");
        GameRegistry.registerTileEntity(FirestarterLogic.class, "Firestarter");
        GameRegistry.registerTileEntity(AdvancedDrawbridgeLogic.class, "AdvDrawbridge");
        
        dynamo = new DynamoBlock(ConfigCore.blockID_dynamo).setLightValue(1.0F).setUnlocalizedName("tmechworks.dynamo").setTextureName("tinker:compressed_alubrass");
        GameRegistry.registerBlock(dynamo, "TMechworks:Dynamo");
        GameRegistry.registerTileEntity(DynamoLogic.class, "TMechworks:Dynamo");

        //Signal blocks
		signalBus = new SignalBus(ConfigCore.blockID_signalBus).setUnlocalizedName("tmechworks.signalbus");
		GameRegistry.registerBlock(signalBus, SignalBusItem.class, "SignalBus");
		GameRegistry.registerTileEntity(SignalBusLogic.class, "SignalBus");
		signalTerminal = new SignalTerminal(ConfigCore.blockID_signalTerminal).setUnlocalizedName("tmechworks.signalterminal");
		GameRegistry.registerBlock(signalTerminal, SignalTerminalItem.class, "SignalTerminal");
		GameRegistry.registerTileEntity(SignalTerminalLogic.class, "SignalTerminal");

	}
	
	private void setupToolTabs()
	{
		TMechworksRegistry.Mechworks.init(new ItemStack(signalTerminal, 1));
		
	}
	
	private void setupTConManual()
	{
	    ItemStack redstone = new ItemStack(Item.redstone);
	    ItemStack blankCast = new ItemStack(proxyItem_blankPattern, 1, 1);
	    
        TConstructClientRegistry.registerManualLargeRecipe("drawbridge", new ItemStack(redstoneMachine, 1, 0), proxyIS_alubrassIngot, blankCast, proxyIS_alubrassIngot, proxyIS_bronzeIngot, new ItemStack(
                Block.dispenser), proxyIS_bronzeIngot, proxyIS_bronzeIngot, redstone, proxyIS_bronzeIngot);
        TConstructClientRegistry.registerManualLargeRecipe("igniter", new ItemStack(redstoneMachine, 1, 1), proxyIS_alubrassIngot, new ItemStack(proxyItem_largePlate, 1, 7), proxyIS_alubrassIngot,
                proxyIS_bronzeIngot, new ItemStack(Item.flintAndSteel), proxyIS_bronzeIngot, proxyIS_bronzeIngot, redstone, proxyIS_bronzeIngot);

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
	
	// ---- PROXY ITEMS
    // --------------------------------------------------------------------------
	public static ItemStack proxyIS_alubrassIngot;
	public static ItemStack proxyIS_bronzeIngot;
    public static Item proxyItem_blankPattern;
	public static Item proxyItem_largePlate;
	
	
}
