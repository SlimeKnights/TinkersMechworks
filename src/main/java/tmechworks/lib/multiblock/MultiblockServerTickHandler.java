package tmechworks.lib.multiblock;

import net.minecraft.world.World;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.Type;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;
import cpw.mods.fml.relauncher.Side;

public class MultiblockServerTickHandler// implements IScheduledTickHandler
{
    public MultiblockServerTickHandler()
    {
    }

    @SubscribeEvent
    public void onTick (WorldTickEvent event)
    {
        if (event.phase.equals(Phase.END) && event.type.equals(Type.WORLD) && event.side == Side.SERVER)
        {
            World world = (World) event.world;
            MultiblockRegistry.tick(world);
        }
    }

  /*  @Override
    public String getLabel ()
    {
        return "TConstruct:MultiblockServerTickHandler";
    }

    @Override
    public int nextTickSpacing ()
    {
        return 1;
    }
*/
}
