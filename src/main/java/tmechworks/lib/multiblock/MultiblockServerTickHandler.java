package tmechworks.lib.multiblock;

import java.util.EnumSet;

import net.minecraft.world.World;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.Type;

public class MultiblockServerTickHandler// implements IScheduledTickHandler
{
    public MultiblockServerTickHandler()
    {
    }

    @SubscribeEvent
    public void onTick (ClientTickEvent event)
    {
        if (event.phase.equals(Phase.END) && event.type.equals(Type.WORLD))
        {
            World world = (World) event.tickData[0];
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
