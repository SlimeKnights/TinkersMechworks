package tmechworks.blocks.component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import mantle.world.CoordTuple;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import tmechworks.blocks.logic.SignalBusLogic;
import tmechworks.lib.multiblock.IMultiblockMember;
import tmechworks.lib.multiblock.MultiblockMasterBaseLogic;

public class SignalBusMasterLogic extends MultiblockMasterBaseLogic
{
    private boolean forceUpdate = false;
    private boolean forceSouthboundUpdates = false;
    private boolean signalUpdate = false;
    private byte[] masterSignals = new byte[16];
    private CoordTuple[] signalProviderCoords = new CoordTuple[16];

    //private List<CoordTuple> tetheredBuses = new LinkedList<CoordTuple>(); // Buses that contain linked Terminals
    private Map<CoordTuple, byte[]> tetheredBuses = new HashMap<CoordTuple, byte[]>();

    public SignalBusMasterLogic(World world)
    {
        super(world);

        for (int i = 0; i < 16; i++)
        {
            masterSignals[i] = 0;
        }
    }

    @Override
    public boolean doUpdate ()
    {
        if (worldObj.isRemote || !forceUpdate)
        {
            return false;
        }

        forceUpdate = false;

        TileEntity te;

        // Calculate new signals from last tick's information
        byte[] oldSignals = masterSignals;
        masterSignals = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

        for (byte[] signals : tetheredBuses.values())
        {
            calcSignals(signals);
        }

        if (forceSouthboundUpdates || !Arrays.equals(oldSignals, masterSignals))
        {
            // Send updates to SignalBuses
            for (CoordTuple coord : tetheredBuses.keySet())
            {
                if (worldObj.getChunkProvider().chunkExists(coord.x >> 4, coord.z >> 4))
                {
                    te = worldObj.getTileEntity(coord.x, coord.y, coord.z);
                    if (te instanceof SignalBusLogic)
                    {
                        ((SignalBusLogic) te).updateLocalSignals(masterSignals);
                        ((SignalBusLogic) te).multiBlockTick();
                    }
                }
            }
        }

        return true;
    }

    public void updateBusSignals (CoordTuple bus, byte[] signals)
    {
        if (!Arrays.equals(tetheredBuses.get(bus), signals))
        {
            tetheredBuses.put(bus, signals);
            forceUpdate = true;
        }
    }

    public byte[] getSignals ()
    {
        return masterSignals.clone();
    }

    @Override
    protected void onBlockAdded (IMultiblockMember newMember)
    {

    }

    @Override
    protected void onBlockRemoved (IMultiblockMember oldMember)
    {
        if (tetheredBuses.containsKey(oldMember.getCoordInWorld()))
        {
            tetheredBuses.remove(oldMember.getCoordInWorld());
            forceUpdate = true;
        }
    }

    @Override
    protected void onDataMerge (MultiblockMasterBaseLogic newMaster)
    {
        if (tetheredBuses.size() > 0)
        {
            ((SignalBusMasterLogic) newMaster).mergeTethered(tetheredBuses);
        }

        ((SignalBusMasterLogic) newMaster).calcSignals(masterSignals);

        ((SignalBusMasterLogic) newMaster).forceUpdate();
    }

    @Override
    public void endMerging ()
    {
        forceSouthboundUpdates = true;
    }

    protected void calcSignals (byte[] signals)
    {
        for (int idx = 0; idx < 16; idx++)
        {
            if (signals[idx] > masterSignals[idx])
            {
                masterSignals[idx] = signals[idx];
            }
        }
    }

    protected void mergeTethered (Map<CoordTuple, byte[]> oldMasterTethered)
    {
        tetheredBuses.putAll(oldMasterTethered);
    }

    @Override
    public void writeToNBT (NBTTagCompound data)
    {
        // Nothing important at the moment
    }

    @Override
    public void readFromNBT (NBTTagCompound data)
    {
        // Nothing important at the moment
    }

    @Override
    public void formatDescriptionPacket (NBTTagCompound data)
    {
        // Nothing important at the moment
    }

    @Override
    public void decodeDescriptionPacket (NBTTagCompound data)
    {
        // Nothing important at the moment
    }

    public void forceUpdate ()
    {
        forceUpdate = true;
    }

}
