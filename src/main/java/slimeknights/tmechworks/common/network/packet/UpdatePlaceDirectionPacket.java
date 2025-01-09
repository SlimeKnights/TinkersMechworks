package slimeknights.tmechworks.common.network.packet;

import net.minecraft.world.entity.player.Player;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import slimeknights.tmechworks.TMechworks;
import slimeknights.tmechworks.common.blocks.tileentity.IPlaceDirection;
import slimeknights.tmechworks.common.network.PacketHandler;

import java.util.function.Supplier;

public class UpdatePlaceDirectionPacket {
    private BlockPos pos;
    private int direction;

    public UpdatePlaceDirectionPacket(BlockPos pos, int direction){
        this.pos = pos;
        this.direction = direction;
    }

    public static void encode(UpdatePlaceDirectionPacket msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.pos);
        buf.writeInt(msg.direction);
    }

    public static UpdatePlaceDirectionPacket decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        int direction = buf.readInt();

        return new UpdatePlaceDirectionPacket(pos, direction);
    }

    public static class Handler {
        public static void handle(final UpdatePlaceDirectionPacket msg, Supplier<NetworkEvent.Context> ctx) {
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

                if(te instanceof IPlaceDirection)
                    ((IPlaceDirection)te).setPlaceDirection(msg.direction);
            });

            context.setPacketHandled(true);
        }
    }
}
