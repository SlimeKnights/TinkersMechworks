package tmechworks.common

import tmechworks.lib.ConfigCore._
import tmechworks.blocks._
import tmechworks.items._

object MechContent {

  def setup() {
    registerItems()
    registerBlocks()
    addCraftingRecipes()
  }

  private def registerItems() {
    // TODO: Register these with Forge
    item_lengthWire = new LengthWire(itemID_lengthWire)
    item_spoolWire = new SpoolOfWire(itemID_spoolWire)
  }

  private def registerBlocks() {
    // TODO: Register these with Forge
    block_signalBus = new SignalBus(blockID_signalBus)
    block_signalTerminal = new SignalTerminal(blockID_signalTerminal)
  }

  private def addCraftingRecipes() {

  }

  //---- ITEMS --------------------------------------------------------------------------
  var item_lengthWire:LengthWire = null
  var item_spoolWire:SpoolOfWire = null

  //---- BLOCKS -------------------------------------------------------------------------
  var block_signalBus:SignalBus = null
  var block_signalTerminal:SignalTerminal = null

}
