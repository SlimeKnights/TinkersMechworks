package tmechworks.lib.player;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import mantle.blocks.abstracts.InventoryLogic;

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
        return new ChunkCoordinates(logic.field_145851_c, logic.field_145848_d, logic.field_145849_e);
    }
}
