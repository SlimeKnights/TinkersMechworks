package slimeknights.tmechworks.common.network.packet;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import slimeknights.tmechworks.TMechworks;
import slimeknights.tmechworks.common.blocks.tileentity.RedstoneMachineTileEntity;
import slimeknights.tmechworks.common.network.PacketHandler;

import java.util.function.Supplier;

public class UpdateDisguiseStatePacket {
    private BlockPos pos;
    private String state;

    public UpdateDisguiseStatePacket(BlockPos pos, String state){
        this.pos = pos;
        this.state = state;
    }

    public static void encode(UpdateDisguiseStatePacket msg, PacketBuffer buf) {
        buf.writeBlockPos(msg.pos);
        buf.writeString(msg.state, 256);
    }

    public static UpdateDisguiseStatePacket decode(PacketBuffer buf) {
        BlockPos pos = buf.readBlockPos();
        String state = buf.readString(256);

        return new UpdateDisguiseStatePacket(pos, state);
    }

    public static class Handler {
        public static void handle(final UpdateDisguiseStatePacket msg, Supplier<NetworkEvent.Context> ctx) {
            PlayerEntity player;

            NetworkEvent.Context context = ctx.get();
            if(context.getDirection().getReceptionSide() == LogicalSide.SERVER) {
                player = context.getSender();

                // World.func_234923_W_ => getDimension
                PacketHandler.send(PacketDistributor.DIMENSION.with(() -> player.world.func_234923_W_()), msg);
            } else {
                player = TMechworks.proxy.getPlayer();
            }

            context.enqueueWork(() -> {
                TileEntity te = player.getEntityWorld().getTileEntity(msg.pos);

                if(te instanceof RedstoneMachineTileEntity)
                    ((RedstoneMachineTileEntity)te).setDisguiseState(msg.state);
            });

            context.setPacketHandled(true);
        }
    }
}
