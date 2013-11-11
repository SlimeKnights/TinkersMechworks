package tmechworks.common;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import tmechworks.blocks.SignalBus;
import tmechworks.blocks.SignalTerminal;
import tmechworks.blocks.logic.SignalBusLogic;
import tmechworks.blocks.logic.SignalTerminalLogic;
import tmechworks.items.LengthWire;
import tmechworks.items.SpoolOfWire;
import tmechworks.items.blocks.SignalBusItem;
import tmechworks.items.blocks.SignalTerminalItem;
import tmechworks.lib.ConfigCore;
import tmechworks.lib.TMechworksRegistry;

public class MechContent
{

	public MechContent()
	{
		registerItems();
		registerBlocks();
		MechRecipes.registerAllTheThings();// !
        setupToolTabs();

	}

	private void registerItems()
	{
		// TODO: Register these with Forge
		lengthWire = new LengthWire(ConfigCore.itemID_lengthWire).setUnlocalizedName("lengthwire");
		GameRegistry.registerItem(lengthWire, "LengthWire");
		spoolWire = new SpoolOfWire(ConfigCore.itemID_spoolWire).setUnlocalizedName("spoolwire");
		GameRegistry.registerItem(spoolWire, "SpoolWire");
	}

	private void registerBlocks()
	{

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

	// ---- ITEMS
	// --------------------------------------------------------------------------
	public static Item lengthWire;
	public static Item spoolWire;

	// ---- BLOCKS
	// -------------------------------------------------------------------------
	public static Block signalBus;
	public static Block signalTerminal;
}
