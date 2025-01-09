package slimeknights.tmechworks.common.network.packet;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import slimeknights.tmechworks.TMechworks;

import java.util.function.Supplier;

public class ClientSetCursorStackPacket {
    private ItemStack stack;

    public ClientSetCursorStackPacket(ItemStack stack) {
        this.stack = stack;
    }

    public static void encode(ClientSetCursorStackPacket msg, FriendlyByteBuf buf) {
        buf.writeItem(msg.stack);
    }

    public static ClientSetCursorStackPacket decode(FriendlyByteBuf buf) {
        ItemStack stack = buf.readItem();

        return new ClientSetCursorStackPacket(stack);
    }

    public static class Handler {
        public static void handle(final ClientSetCursorStackPacket msg, Supplier<NetworkEvent.Context> ctx) {
            NetworkEvent.Context context = ctx.get();
            Player player = TMechworks.proxy.getPlayer();

            context.enqueueWork(() -> {
                player.containerMenu.setCarried(msg.stack);
            });

            context.setPacketHandled(true);
        }
    }
}
