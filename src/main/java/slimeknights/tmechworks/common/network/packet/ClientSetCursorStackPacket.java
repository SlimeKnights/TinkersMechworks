package slimeknights.tmechworks.common.network.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import slimeknights.tmechworks.TMechworks;

import java.util.function.Supplier;

public class ClientSetCursorStackPacket {
    private ItemStack stack;

    public ClientSetCursorStackPacket(ItemStack stack) {
        this.stack = stack;
    }

    public static void encode(ClientSetCursorStackPacket msg, PacketBuffer buf) {
        buf.writeItemStack(msg.stack);
    }

    public static ClientSetCursorStackPacket decode(PacketBuffer buf) {
        ItemStack stack = buf.readItemStack();

        return new ClientSetCursorStackPacket(stack);
    }

    public static class Handler {
        public static void handle(final ClientSetCursorStackPacket msg, Supplier<NetworkEvent.Context> ctx) {
            NetworkEvent.Context context = ctx.get();
            PlayerEntity player = TMechworks.proxy.getPlayer();

            context.enqueueWork(() -> {
                player.inventory.setItemStack(msg.stack);
            });

            context.setPacketHandled(true);
        }
    }
}
