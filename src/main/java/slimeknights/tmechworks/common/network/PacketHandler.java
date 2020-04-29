package slimeknights.tmechworks.common.network;

import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import slimeknights.tmechworks.common.network.packet.ClientSetCursorStackPacket;
import slimeknights.tmechworks.common.network.packet.ServerReopenUiPacket;
import slimeknights.tmechworks.common.network.packet.UpdateDisguiseStatePacket;
import slimeknights.tmechworks.common.network.packet.UpdatePlaceDirectionPacket;
import slimeknights.tmechworks.library.Util;

public final class PacketHandler {
    private static final String PROTOCOL_VERSION = Integer.toString(2);
    private static final SimpleChannel HANDLER = NetworkRegistry.ChannelBuilder.named(Util.getResource("main"))
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .simpleChannel();

    public static void register() {
        int id = 0;

        HANDLER.registerMessage(id++, UpdateDisguiseStatePacket.class, UpdateDisguiseStatePacket::encode, UpdateDisguiseStatePacket::decode, UpdateDisguiseStatePacket.Handler::handle);
        HANDLER.registerMessage(id++, UpdatePlaceDirectionPacket.class, UpdatePlaceDirectionPacket::encode, UpdatePlaceDirectionPacket::decode, UpdatePlaceDirectionPacket.Handler::handle);
        HANDLER.registerMessage(id++, ServerReopenUiPacket.class, ServerReopenUiPacket::encode, ServerReopenUiPacket::decode, ServerReopenUiPacket.Handler::handle);
        HANDLER.registerMessage(id++, ClientSetCursorStackPacket.class, ClientSetCursorStackPacket::encode, ClientSetCursorStackPacket::decode, ClientSetCursorStackPacket.Handler::handle);

        Util.sink(id);
    }

    public static <T> void send(PacketDistributor.PacketTarget target, T message) {

        HANDLER.send(target, message);
    }
}
