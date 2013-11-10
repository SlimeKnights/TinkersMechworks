package tmechworks

import tmechworks.lib.Repo._
import cpw.mods.fml.common.{SidedProxy, FMLLog, Mod}
import cpw.mods.fml.common.network.NetworkMod
import tmechworks.network.PacketHandler
import cpw.mods.fml.common.event.{FMLPostInitializationEvent, FMLInitializationEvent, FMLPreInitializationEvent}
import cpw.mods.fml.common.Mod.EventHandler
import tmechworks.lib.ConfigCore
import net.minecraftforge.common.Configuration
import tmechworks.common.{MechContent, CommonProxy}

@Mod(modid = modId, name = modName, version = modVer, modLanguage = "scala")
@NetworkMod(serverSideRequired = false, clientSideRequired = true, channels = Array("TMechworks"), packetHandler = classOf[PacketHandler])
object TMechworks {

  @SidedProxy(clientSide = "tmechworks.client.ClientProxy", serverSide = "tmechworks.common.CommonProxy", modId = modId)
  var proxy: CommonProxy = null

  @EventHandler
  def preInit(evt:FMLPreInitializationEvent) {
    logger.setParent(FMLLog.getLogger)
    logger.info(s"$modName ($modVer) starting...")
    ConfigCore.loadConfig(new Configuration(evt.getSuggestedConfigurationFile))
    MechContent.setup()
  }

  @EventHandler
  def init(evt:FMLInitializationEvent) {

  }

  @EventHandler
  def postInit(evt:FMLPostInitializationEvent) {
    logger.info("Setup complete.")
  }

}
