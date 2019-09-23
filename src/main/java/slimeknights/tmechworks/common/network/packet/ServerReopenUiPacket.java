package slimeknights.tmechworks.common.network.packet;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import slimeknights.tmechworks.common.blocks.RedstoneMachineBlock;
import slimeknights.tmechworks.common.network.PacketHandler;

import java.util.function.Supplier;

public class ServerReopenUiPacket {
    private BlockPos pos;

    public ServerReopenUiPacket(BlockPos pos) {
        this.pos = pos;
    }

    public static void encode(ServerReopenUiPacket msg, PacketBuffer buf) {
        buf.writeBlockPos(msg.pos);
    }

    public static ServerReopenUiPacket decode(PacketBuffer buf) {
        BlockPos pos = buf.readBlockPos();

        return new ServerReopenUiPacket(pos);
    }

    public static class Handler {
        public static void handle(final ServerReopenUiPacket msg, Supplier<NetworkEvent.Context> ctx) {
            NetworkEvent.Context context = ctx.get();
            PlayerEntity player = context.getSender();

            context.enqueueWork(() -> {
                BlockState block = player.getEntityWorld().getBlockState(msg.pos);

                if(block.getBlock() instanceof RedstoneMachineBlock) {
                    ItemStack cursorStack = player.inventory.getItemStack();
                    player.inventory.setItemStack(ItemStack.EMPTY);
                    ((RedstoneMachineBlock)block.getBlock()).openGui(player, player.getEntityWorld(), msg.pos);
                    player.inventory.setItemStack(cursorStack);

                    PacketHandler.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new ClientSetCursorStackPacket(cursorStack));
                }
            });

            context.setPacketHandled(true);
        }
    }
}
