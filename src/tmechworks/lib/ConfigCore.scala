package tmechworks.lib

import net.minecraftforge.common.Configuration

import tmechworks.lib.Repo._

object ConfigCore {

  def loadConfig(conf:Configuration) {
    logger.info("Loading configuration...")
    conf.load()

    // Config goes here (assign using current value as default!)

    conf.save()
    logger.info("Done.")
  }

  // Config vars go here in form "var configName = defaultValue"

}
