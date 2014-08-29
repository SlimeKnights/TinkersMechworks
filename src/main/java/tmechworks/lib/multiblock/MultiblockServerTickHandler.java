package tmechworks.lib.multiblock;

import net.minecraft.world.World;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.Type;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import cpw.mods.fml.relauncher.Side;

public class MultiblockServerTickHandler// implements IScheduledTickHandler
{
    public MultiblockServerTickHandler()
    {
    }

    @SubscribeEvent
    public void tick (WorldTickEvent evt)
    {
        if (evt.phase.equals(Phase.END) && evt.type.equals(Type.WORLD) && evt.side == Side.SERVER)
        {
            World world = (World) evt.world;
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
