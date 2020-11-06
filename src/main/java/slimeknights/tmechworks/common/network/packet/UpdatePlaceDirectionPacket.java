package slimeknights.tmechworks.common.network.packet;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;
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

    public static void encode(UpdatePlaceDirectionPacket msg, PacketBuffer buf) {
        buf.writeBlockPos(msg.pos);
        buf.writeInt(msg.direction);
    }

    public static UpdatePlaceDirectionPacket decode(PacketBuffer buf) {
        BlockPos pos = buf.readBlockPos();
        int direction = buf.readInt();

        return new UpdatePlaceDirectionPacket(pos, direction);
    }

    public static class Handler {
        public static void handle(final UpdatePlaceDirectionPacket msg, Supplier<NetworkEvent.Context> ctx) {
            PlayerEntity player;

            NetworkEvent.Context context = ctx.get();
            if(context.getDirection().getReceptionSide() == LogicalSide.SERVER) {
                player = context.getSender();

                // World.func_234923_W_ => getDimension
                PacketHandler.send(PacketDistributor.DIMENSION.with(() -> player.world.getDimensionKey()), msg);
            } else {
                player = TMechworks.proxy.getPlayer();
            }

            context.enqueueWork(() -> {
                TileEntity te = player.getEntityWorld().getTileEntity(msg.pos);

                if(te instanceof IPlaceDirection)
                    ((IPlaceDirection)te).setPlaceDirection(msg.direction);
            });

            context.setPacketHandled(true);
        }
    }
}
