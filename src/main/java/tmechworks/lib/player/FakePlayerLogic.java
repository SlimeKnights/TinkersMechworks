package tmechworks.lib.player;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;

import com.mojang.authlib.GameProfile;

public class FakePlayerLogic extends FakePlayer
{
    ChunkCoordinates logicPos;

    public FakePlayerLogic(GameProfile gm, TileEntity te)
    {
        super((WorldServer) te.getWorldObj(), gm);
        logicPos = new ChunkCoordinates(te.xCoord, te.yCoord, te.zCoord);

    }

    public ChunkCoordinates getPlayerCoordinates ()
    {
        return logicPos;
    }

}
