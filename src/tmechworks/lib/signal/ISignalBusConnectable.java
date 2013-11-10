package tmechworks.lib.signal;

import net.minecraftforge.common.ForgeDirection;

public interface ISignalBusConnectable
{
    public boolean connectableOnFace (ForgeDirection side);

    public boolean connectableOnCorner (ForgeDirection side, ForgeDirection turn);
}
