package tmechworks.client.block;

import static tmechworks.blocks.SignalTerminal.TerminalGeometry.center_max;
import static tmechworks.blocks.SignalTerminal.TerminalGeometry.center_min;
import static tmechworks.blocks.SignalTerminal.TerminalGeometry.channel_high_max;
import static tmechworks.blocks.SignalTerminal.TerminalGeometry.channel_high_min;
import static tmechworks.blocks.SignalTerminal.TerminalGeometry.channel_low_max;
import static tmechworks.blocks.SignalTerminal.TerminalGeometry.channel_low_min;
import static tmechworks.blocks.SignalTerminal.TerminalGeometry.channel_width_max;
import static tmechworks.blocks.SignalTerminal.TerminalGeometry.channel_width_min;
import static tmechworks.blocks.SignalTerminal.TerminalGeometry.plate_high_max;
import static tmechworks.blocks.SignalTerminal.TerminalGeometry.plate_high_min;
import static tmechworks.blocks.SignalTerminal.TerminalGeometry.plate_low_max;
import static tmechworks.blocks.SignalTerminal.TerminalGeometry.plate_low_min;
import static tmechworks.blocks.SignalTerminal.TerminalGeometry.plate_width_max;
import static tmechworks.blocks.SignalTerminal.TerminalGeometry.plate_width_min;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import tmechworks.blocks.logic.SignalTerminalLogic;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class SignalTerminalRender implements ISimpleBlockRenderingHandler
{
    public static int renderID = RenderingRegistry.getNextAvailableRenderId();

    @Override
    public void renderInventoryBlock (Block block, int metadata, int modelID, RenderBlocks renderer)
    {
        // Render X-
        renderer.func_147782_a(plate_low_min, plate_width_min, plate_width_min, plate_low_max, plate_width_max, plate_width_max);
        this.func_147784_q(block, metadata, renderer);

        renderer.func_147782_a(center_min, center_min, center_min, center_max, center_max, center_max);
        this.func_147784_q(block, metadata, renderer);

        renderer.func_147782_a(channel_low_min, channel_width_min, channel_width_min, channel_low_max, channel_width_max, channel_width_max);
        renderer.func_147757_a(SignalTerminalLogic.getChannelIcon(0));
        this.func_147784_q(block, metadata, renderer);
        renderer.func_147771_a();
    }

    @Override
    public boolean renderWorldBlock (IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
    {
        int sidesRendered = 0;

        if (modelId == renderID)
        {
            TileEntity te = world.getBlockTileEntity(x, y, z);
            if (!(te instanceof SignalTerminalLogic))
            {
                // Render X-
                renderer.func_147782_a(plate_low_min, plate_width_min, plate_width_min, plate_low_max, plate_width_max, plate_width_max);
                renderer.func_147784_q(block, x, y, z);

                renderer.func_147782_a(center_min, center_min, center_min, center_max, center_max, center_max);
                renderer.func_147784_q(block, x, y, z);

                renderer.func_147782_a(channel_low_min, channel_width_min, channel_width_min, channel_low_max, channel_width_max, channel_width_max);
                renderer.func_147757_a(SignalTerminalLogic.getChannelIcon(0));
                renderer.func_147784_q(block, x, y, z);
                renderer.func_147771_a();

                return true;
            }
            byte[] connectedSides = ((SignalTerminalLogic) te).getConnectedSides();
            IIcon channelIcons[] = ((SignalTerminalLogic) te).getSideIcons();

            // Center
            renderer.func_147782_a(center_min, center_min, center_min, center_max, center_max, center_max);
            renderer.func_147784_q(block, x, y, z);

            if (connectedSides[ForgeDirection.WEST.ordinal()] != -1)
            {
                // Render X-
                renderer.func_147782_a(plate_low_min, plate_width_min, plate_width_min, plate_low_max, plate_width_max, plate_width_max);
                renderer.func_147784_q(block, x, y, z);

                renderer.func_147782_a(channel_low_min, channel_width_min, channel_width_min, channel_low_max, channel_width_max, channel_width_max);
                renderer.func_147757_a(channelIcons[ForgeDirection.WEST.ordinal()]);
                renderer.func_147784_q(block, x, y, z);
                renderer.func_147771_a();

                sidesRendered++;
            }
            if (connectedSides[ForgeDirection.EAST.ordinal()] != -1)
            {
                //Extend X+
                renderer.func_147782_a(plate_high_min, plate_width_min, plate_width_min, plate_high_max, plate_width_max, plate_width_max);
                renderer.func_147784_q(block, x, y, z);

                renderer.func_147782_a(channel_high_min, channel_width_min, channel_width_min, channel_high_max, channel_width_max, channel_width_max);
                renderer.func_147757_a(channelIcons[ForgeDirection.EAST.ordinal()]);
                renderer.func_147784_q(block, x, y, z);
                renderer.func_147771_a();

                sidesRendered++;
            }
            if (connectedSides[ForgeDirection.SOUTH.ordinal()] != -1)
            {
                //Extend Z-
                renderer.func_147782_a(plate_width_min, plate_width_min, plate_low_min, plate_width_max, plate_width_max, plate_low_max);
                renderer.func_147784_q(block, x, y, z);

                renderer.func_147782_a(channel_width_min, channel_width_min, channel_low_min, channel_width_max, channel_width_max, channel_low_max);
                renderer.func_147757_a(channelIcons[ForgeDirection.SOUTH.ordinal()]);
                renderer.func_147784_q(block, x, y, z);
                renderer.func_147771_a();

                sidesRendered++;
            }
            if (connectedSides[ForgeDirection.NORTH.ordinal()] != -1)
            {
                //Extend Z+
                renderer.func_147782_a(plate_width_min, plate_width_min, plate_high_min, plate_width_max, plate_width_max, plate_high_max);
                renderer.func_147784_q(block, x, y, z);

                renderer.func_147782_a(channel_width_min, channel_width_min, channel_high_min, channel_width_max, channel_width_max, channel_high_max);
                renderer.func_147757_a(channelIcons[ForgeDirection.NORTH.ordinal()]);
                renderer.func_147784_q(block, x, y, z);
                renderer.func_147771_a();

                sidesRendered++;
            }
            if (connectedSides[ForgeDirection.DOWN.ordinal()] != -1)
            {
                //Extend Y-
                renderer.func_147782_a(plate_width_min, plate_low_min, plate_width_min, plate_width_max, plate_low_max, plate_width_max);
                renderer.func_147784_q(block, x, y, z);

                renderer.func_147782_a(channel_width_min, channel_low_min, channel_width_min, channel_width_max, channel_low_max, channel_width_max);
                renderer.func_147757_a(channelIcons[ForgeDirection.DOWN.ordinal()]);
                renderer.func_147784_q(block, x, y, z);
                renderer.func_147771_a();

                sidesRendered++;
            }
            if (connectedSides[ForgeDirection.UP.ordinal()] != -1)
            {
                //Extend Y+
                renderer.func_147782_a(plate_width_min, plate_high_min, plate_width_min, plate_width_max, plate_high_max, plate_width_max);
                renderer.func_147784_q(block, x, y, z);

                renderer.func_147782_a(channel_width_min, channel_high_min, channel_width_min, channel_width_max, channel_high_max, channel_width_max);
                renderer.func_147757_a(channelIcons[ForgeDirection.UP.ordinal()]);
                renderer.func_147784_q(block, x, y, z);
                renderer.func_147771_a();

                sidesRendered++;
            }

            if (sidesRendered == 0)
            {
                // Render X-
                renderer.func_147782_a(plate_low_min, plate_width_min, plate_width_min, plate_low_max, plate_width_max, plate_width_max);
                renderer.func_147784_q(block, x, y, z);

                renderer.func_147782_a(channel_low_min, channel_width_min, channel_width_min, channel_low_max, channel_width_max, channel_width_max);
                renderer.func_147757_a(SignalTerminalLogic.getChannelIcon(0));
                renderer.func_147784_q(block, x, y, z);
                renderer.func_147771_a();

            }
            return true;
        }
        return false;
    }

    private void func_147784_q (Block block, int meta, RenderBlocks renderer)
    {
        Tessellator tessellator = Tessellator.instance;
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, -1.0F, 0.0F);
        renderer.func_147768_a(block, 0.0D, 0.0D, 0.0D, block.func_149691_a(0, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderer.func_147806_b(block, 0.0D, 0.0D, 0.0D, block.func_149691_a(1, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, -1.0F);
        renderer.func_147761_c(block, 0.0D, 0.0D, 0.0D, block.func_149691_a(2, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        renderer.func_147734_d(block, 0.0D, 0.0D, 0.0D, block.func_149691_a(3, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(-1.0F, 0.0F, 0.0F);
        renderer.func_147798_e(block, 0.0D, 0.0D, 0.0D, block.func_149691_a(4, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderer.func_147764_f(block, 0.0D, 0.0D, 0.0D, block.func_149691_a(5, meta));
        tessellator.draw();
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
    }

    @Override
    public boolean shouldRender3DInInventory (int model)
    {
        return true;
    }

    @Override
    public int getRenderId ()
    {
        return this.renderID;
    }
}
