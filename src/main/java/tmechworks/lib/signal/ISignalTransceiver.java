package tmechworks.lib.signal;

import net.minecraft.world.World;
import tmechworks.lib.util.CoordTuple;

public interface ISignalTransceiver
{

    void setBusCoords (World world, int xCoord, int yCoord, int zCoord);

    CoordTuple getBusCoords ();
    
    byte[] getReceivedSignals ();

    void receiveSignalUpdate (byte[] signals);

    int doUnregister (boolean reHoming);

    int getDroppedWire ();
    
}
