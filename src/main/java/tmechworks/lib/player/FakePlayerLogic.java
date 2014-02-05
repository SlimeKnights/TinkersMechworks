package tmechworks.lib.player;

import mantle.blocks.abstracts.InventoryLogic;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;

import com.mojang.authlib.GameProfile;

public class FakePlayerLogic extends FakePlayer
{
    InventoryLogic logic;

    public FakePlayerLogic(WorldServer world, GameProfile gm, InventoryLogic logic)
    {
        super(world, gm);
        this.logic = logic;
    }

    public ChunkCoordinates getPlayerCoordinates ()
    {
        return new ChunkCoordinates(logic.xCoord, logic.yCoord, logic.zCoord);
    }
}
