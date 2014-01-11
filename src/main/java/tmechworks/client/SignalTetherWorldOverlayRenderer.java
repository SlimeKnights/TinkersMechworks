package tmechworks.client;

import java.util.ArrayDeque;

import mantle.world.CoordTuple;
import mantle.world.CoordTuplePair;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import tmechworks.TMechworks;
import tmechworks.lib.signal.ISignalTransceiver;

public class SignalTetherWorldOverlayRenderer
{

    @SubscribeEvent
    public void onWorldRenderLast (RenderWorldLastEvent event)
    {
        if (event.context.mc.thePlayer == null || event.context.mc.thePlayer.getHeldItem() == null)
        {
            return;
        }
        if (event.context.mc.thePlayer.getHeldItem() == TMechworks.instance.content.spoolWire)
        {
            GL11.glPushMatrix();
            Entity entity = event.context.mc.renderViewEntity;

            double posX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * event.partialTicks;
            double posY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * event.partialTicks;
            double posZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * event.partialTicks;

            GL11.glTranslated(-posX, -posY, -posZ);

            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glLineWidth(2.5F);

            World world = entity.worldObj;
            int x1 = (int) entity.posX;
            int z1 = (int) entity.posZ;

            Chunk chunks[] = new Chunk[9];

            chunks[4] = world.getChunkFromBlockCoords(x1, z1);
            int cX = chunks[4].xPosition;
            int cZ = chunks[4].zPosition;

            chunks[0] = world.getChunkFromChunkCoords(cX - 1, cZ - 1);
            chunks[1] = world.getChunkFromChunkCoords(cX, cZ - 1);
            chunks[2] = world.getChunkFromChunkCoords(cX + 1, cZ - 1);

            chunks[3] = world.getChunkFromChunkCoords(cX - 1, cZ);
            chunks[5] = world.getChunkFromChunkCoords(cX + 1, cZ);

            chunks[6] = world.getChunkFromChunkCoords(cX - 1, cZ + 1);
            chunks[7] = world.getChunkFromChunkCoords(cX, cZ + 1);
            chunks[8] = world.getChunkFromChunkCoords(cX + 1, cZ + 1);

            ArrayDeque<CoordTuplePair> transceivers = new ArrayDeque<CoordTuplePair>();
            CoordTuple src;
            CoordTuple dst;
            for (int c = 0; c < 9; ++c)
            {
                for (Object obj : chunks[c].field_150816_i.values())
                {
                    if (obj instanceof ISignalTransceiver && obj instanceof TileEntity)
                    {
                        dst = ((ISignalTransceiver) obj).getBusCoords();
                        if (dst != null)
                        {
                            src = new CoordTuple(((TileEntity) obj).field_145851_c, ((TileEntity) obj).field_145848_d, ((TileEntity) obj).field_145849_e);
                            transceivers.push(new CoordTuplePair(src, dst));
                        }
                    }
                }
            }

            GL11.glBegin(GL11.GL_LINES);

            GL11.glColor4f(1, 0, 0, 1);

            CoordTuplePair renderPair;
            while (transceivers.size() > 0)
            {
                renderPair = transceivers.pop();

                GL11.glVertex3d(renderPair.a.x + 0.5, renderPair.a.y + 0.5, renderPair.a.z + 0.5);
                GL11.glVertex3d(renderPair.b.x + 0.5, renderPair.b.y, renderPair.b.z + 0.5);
            }

            GL11.glEnd();
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_BLEND);

            GL11.glPopMatrix();
        }
    }
}
