package tmechworks.network;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import tmechworks.TMechworks;
import tconstruct.library.blocks.InventoryLogic;
import tmechworks.blocks.logic.DrawbridgeLogic;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fluids.FluidStack;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;

public class PacketHandler implements IPacketHandler
{

    @Override
    public void onPacketData (INetworkManager manager, Packet250CustomPayload packet, Player player)
    {
        Side side = FMLCommonHandler.instance().getEffectiveSide();

        if (packet.channel.equals("TMechworks"))
        {
            if (side == Side.SERVER)
                handleServerPacket(packet, (EntityPlayerMP) player);
            else
                handleClientPacket(packet, (EntityPlayer) player);
        }
    }
    
    void handleClientPacket (Packet250CustomPayload packet, EntityPlayer player)
    {
        DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));

        byte packetID;

        try
        {
            packetID = inputStream.readByte();
        }
        catch (Exception e)
        {
            TMechworks.logger.warning("Failed at reading client packet for TMechworks.");
            e.printStackTrace();
        }
    }

    void handleServerPacket (Packet250CustomPayload packet, EntityPlayerMP player)
    {
        DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));

        byte packetID;

        try
        {
            packetID = inputStream.readByte();

            if (packetID == 5) //Drawbridge
            {
                int dimension = inputStream.readInt();
                World world = DimensionManager.getWorld(dimension);
                int x = inputStream.readInt();
                int y = inputStream.readInt();
                int z = inputStream.readInt();
                TileEntity te = world.getBlockTileEntity(x, y, z);

                byte direction = inputStream.readByte();
                if (te instanceof DrawbridgeLogic)
                {
                    ((DrawbridgeLogic) te).setPlacementDirection(direction);
                }
            }
        }
        catch (IOException e)
        {
            TMechworks.logger.warning("Failed at reading server packet for TMechworks.");
            e.printStackTrace();
        }
    }

    Entity getEntity (World world, int id)
    {
        for (Object o : world.loadedEntityList)
        {
            if (((Entity) o).entityId == id)
                return (Entity) o;
        }
        return null;
    }

}
