package tmechworks.network

import cpw.mods.fml.common.network.{Player, IPacketHandler}
import net.minecraft.network.INetworkManager
import net.minecraft.network.packet.Packet250CustomPayload

class PacketHandler extends IPacketHandler {

  def onPacketData(manager: INetworkManager, packet: Packet250CustomPayload, player: Player) {

  }

}
