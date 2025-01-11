package slimeknights.tmechworks.common.network.packet;

import net.minecraft.world.entity.player.Player;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import slimeknights.tmechworks.TMechworks;
import slimeknights.tmechworks.common.blocks.entity.RedstoneMachineBlockEntity;
import slimeknights.tmechworks.common.network.PacketHandler;

import java.util.function.Supplier;

public class UpdateDisguiseStatePacket {
    private BlockPos pos;
    private String state;

    public UpdateDisguiseStatePacket(BlockPos pos, String state){
        this.pos = pos;
        this.state = state;
    }

    public static void encode(UpdateDisguiseStatePacket msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.pos);
        buf.writeUtf(msg.state, 256);
    }

    public static UpdateDisguiseStatePacket decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        String state = buf.readUtf(256);

        return new UpdateDisguiseStatePacket(pos, state);
    }

    public static class Handler {
        public static void handle(final UpdateDisguiseStatePacket msg, Supplier<NetworkEvent.Context> ctx) {
            Player player;

            NetworkEvent.Context context = ctx.get();
            if(context.getDirection().getReceptionSide() == LogicalSide.SERVER) {
                player = context.getSender();

                // World.dimension => getDimension
                PacketHandler.send(PacketDistributor.DIMENSION.with(() -> player.level.dimension()), msg);
            } else {
                player = TMechworks.proxy.getPlayer();
            }

            context.enqueueWork(() -> {
                BlockEntity te = player.getCommandSenderWorld().getBlockEntity(msg.pos);

                if(te instanceof RedstoneMachineBlockEntity)
                    ((RedstoneMachineBlockEntity)te).setDisguiseState(msg.state);
            });

            context.setPacketHandled(true);
        }
    }
}
