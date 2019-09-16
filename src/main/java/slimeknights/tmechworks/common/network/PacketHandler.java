package slimeknights.tmechworks.common.network;

import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;
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

        HANDLER.registerMessage(id++, UpdatePlaceDirectionPacket.class, UpdatePlaceDirectionPacket::encode, UpdatePlaceDirectionPacket::decode, UpdatePlaceDirectionPacket.Handler::handle);
    }

    public static <T> void send(PacketDistributor.PacketTarget target, T message) {

        HANDLER.send(target, message);
    }
}
