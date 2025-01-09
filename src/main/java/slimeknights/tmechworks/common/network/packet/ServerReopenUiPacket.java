package slimeknights.tmechworks.common.network.packet;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import slimeknights.tmechworks.common.blocks.RedstoneMachineBlock;
import slimeknights.tmechworks.common.network.PacketHandler;

import java.util.function.Supplier;

public class ServerReopenUiPacket {
    private BlockPos pos;

    public ServerReopenUiPacket(BlockPos pos) {
        this.pos = pos;
    }

    public static void encode(ServerReopenUiPacket msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.pos);
    }

    public static ServerReopenUiPacket decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();

        return new ServerReopenUiPacket(pos);
    }

    public static class Handler {
        public static void handle(final ServerReopenUiPacket msg, Supplier<NetworkEvent.Context> ctx) {
            NetworkEvent.Context context = ctx.get();
            Player player = context.getSender();

            context.enqueueWork(() -> {
                BlockState block = player.getCommandSenderWorld().getBlockState(msg.pos);

                if(block.getBlock() instanceof RedstoneMachineBlock) {
                    ItemStack cursorStack = player.containerMenu.getCarried();
                    player.containerMenu.setCarried(ItemStack.EMPTY);
                    ((RedstoneMachineBlock)block.getBlock()).openGui(player, player.getCommandSenderWorld(), msg.pos);
                    player.containerMenu.setCarried(cursorStack);

                    PacketHandler.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new ClientSetCursorStackPacket(cursorStack));
                }
            });

            context.setPacketHandled(true);
        }
    }
}
