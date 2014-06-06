package tmechworks.network.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import mantle.common.network.AbstractPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import tmechworks.blocks.logic.AdvancedDrawbridgeLogic;
import tmechworks.blocks.logic.DrawbridgeLogic;

public class PacketDrawbridge extends AbstractPacket
{

    public int x, y, z;
    public byte direction;

    public PacketDrawbridge()
    {

    }

    public PacketDrawbridge(int x, int y, int z, byte direction)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.direction = direction;
    }

    @Override
    public void encodeInto (ChannelHandlerContext ctx, ByteBuf buffer)
    {
        buffer.writeInt(x);
        buffer.writeInt(y);
        buffer.writeInt(z);
        buffer.writeByte(direction);
    }

    @Override
    public void decodeInto (ChannelHandlerContext ctx, ByteBuf buffer)
    {
        x = buffer.readInt();
        y = buffer.readInt();
        z = buffer.readInt();
        direction = buffer.readByte();
    }

    @Override
    public void handleClientSide (EntityPlayer player)
    {

    }

    @Override
    public void handleServerSide (EntityPlayer player)
    {
        World world = player.worldObj;
        TileEntity te = world.getTileEntity(x, y, z);

        if (te instanceof DrawbridgeLogic)
        {
            ((DrawbridgeLogic) te).setPlacementDirection(direction);
            te.markDirty();
        }
        else if (te instanceof AdvancedDrawbridgeLogic)
        {
            ((AdvancedDrawbridgeLogic) te).setPlacementDirection(direction);
        }
    }

}
