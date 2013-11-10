package tmechworks.lib

import net.minecraftforge.common.Configuration

import tmechworks.lib.Repo._

object ConfigCore {

  def loadConfig(conf:Configuration) {
    logger.info("Loading configuration...")
    conf.load()

    loadItems(conf)
    loadBlocks(conf)

    conf.save()
    logger.info("Done.")
  }

  private def loadItems(conf:Configuration) {
    itemID_lengthWire = conf.getItem("Signals", "LengthWire", itemID_lengthWire).getInt
    itemID_spoolWire = conf.getItem("Signals", "SpoolWire", itemID_spoolWire).getInt
  }

  private def loadBlocks(conf:Configuration) {
    blockID_signalBus = conf.getBlock("Signals", "SignalBus", blockID_signalBus).getInt
    blockID_signalTerminal = conf.getBlock("Signals", "SignalTerminal", blockID_signalTerminal).getInt
  }

  //---- ITEMS -- 12000-12250 -------------------------------------------------
  var itemID_lengthWire = 12000
  var itemID_spoolWire  = 12001

  //---- BLOCKS -- 3000-3250 --------------------------------------------------
  var blockID_signalBus      = 3000
  var blockID_signalTerminal = 3001

}
