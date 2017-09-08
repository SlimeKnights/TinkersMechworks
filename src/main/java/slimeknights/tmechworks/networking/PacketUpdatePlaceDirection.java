package slimeknights.tmechworks.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import slimeknights.mantle.network.AbstractPacketThreadsafe;
import slimeknights.tmechworks.TMechworks;
import slimeknights.tmechworks.blocks.logic.IPlaceDirection;

public class PacketUpdatePlaceDirection extends AbstractPacketThreadsafe {
    private BlockPos pos;
    private int direction;

    public PacketUpdatePlaceDirection() {
    }

    public PacketUpdatePlaceDirection(BlockPos pos, int direction) {
        this.pos = pos;
        this.direction = direction;
    }

    @Override
    public void handleClientSafe(NetHandlerPlayClient netHandler) {
        handlePacket(Minecraft.getMinecraft().player);
    }

    @Override
    public void handleServerSafe(NetHandlerPlayServer netHandler) {
        handlePacket(netHandler.player);

        TMechworks.packetPipeline.network.sendToDimension(this, netHandler.player.dimension);
    }

    public void handlePacket(EntityPlayer player) {
        TileEntity te = player.getEntityWorld().getTileEntity(pos);

        if (te instanceof IPlaceDirection) {
            ((IPlaceDirection) te).setPlaceDirectioni(direction);
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int x = buf.readInt();
        int y = buf.readInt();
        int z = buf.readInt();

        pos = new BlockPos(x, y, z);
        direction = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(pos.getX()).writeInt(pos.getY()).writeInt(pos.getZ()).writeInt(direction);
    }
}
