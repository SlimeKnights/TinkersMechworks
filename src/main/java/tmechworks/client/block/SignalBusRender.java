package tmechworks.client.block;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import tmechworks.blocks.SignalBus.BusGeometry;
import tmechworks.blocks.logic.SignalBusLogic;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class SignalBusRender implements ISimpleBlockRenderingHandler
{
    public static int renderID = RenderingRegistry.getNextAvailableRenderId();

    @Override
    public void renderInventoryBlock (Block block, int metadata, int modelID, RenderBlocks renderer)
    {
        //Base
        renderer.setRenderBounds(0.375D, 0.0D, 0.375D, 0.625D, 0.2D, 0.625D);
        this.renderStandardBlock(block, metadata, renderer);
        //Extend Z-
        renderer.setRenderBounds(0.375D, 0.0D, 0.0D, 0.625D, 0.2D, 0.375D);
        this.renderStandardBlock(block, metadata, renderer);
        //Extend Z+
        renderer.setRenderBounds(0.375D, 0.0D, 0.625D, 0.625D, 0.2D, 1D);
        this.renderStandardBlock(block, metadata, renderer);
    }

    @Override
    public boolean renderWorldBlock (IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
    {
        boolean[] placedSides;
        boolean[] connectedSides;
        boolean[] corners;
        boolean didRender = false;
        if (modelId == renderID)
        {
            SignalBusLogic tile = (SignalBusLogic) world.func_147438_o(x, y, z);
            placedSides = tile.placedSides();

            for (int i = 0; i < 6; ++i)
            {
                if (!placedSides[i]){
                    continue;
                }
                didRender = true;
                connectedSides = tile.connectedSides(ForgeDirection.getOrientation(i));
                corners = tile.getRenderCorners(ForgeDirection.getOrientation(i));
                
                renderFaceWithConnections(renderer, block, x, y, z, i, placedSides, connectedSides, corners);
            }
            if (!didRender)
            {
                double minX = BusGeometry.cable_width_min;
                double minY = BusGeometry.cable_low_offset;
                double minZ = BusGeometry.cable_width_min;
                double maxX = BusGeometry.cable_width_max;
                double maxY = BusGeometry.cable_low_height;
                double maxZ = BusGeometry.cable_width_max;
                
                renderer.setRenderBounds(minX, minY, minZ, maxX, maxY, maxZ);
                renderer.renderStandardBlock(block, x, y, z);
            }

        }
        return true;
    }

    private void renderFaceWithConnections (RenderBlocks renderer, Block block, int x, int y, int z, int side, boolean[] placed, boolean[] connectedSides, boolean[] corners)
    {
        double minX = 0D;
        double minY = 0D;
        double minZ = 0D;
        double maxX = 1D;
        double maxY = 1D;
        double maxZ = 1D;
        
        boolean[] renderDir = {
                (connectedSides[0] || placed[0] || corners[0]),
                (connectedSides[1] || placed[1] || corners[1]),
                (connectedSides[2] || placed[2] || corners[2]),
                (connectedSides[3] || placed[3] || corners[3]),
                (connectedSides[4] || placed[4] || corners[4]),
                (connectedSides[5] || placed[5] || corners[5])
        };
        
        switch (side)
        {
        case 0: // DOWN
            // Render East/West
            if (renderDir[ForgeDirection.WEST.ordinal()] || renderDir[ForgeDirection.EAST.ordinal()])
            {
                minX = (renderDir[ForgeDirection.WEST.ordinal()]) ? BusGeometry.cable_extend_min : BusGeometry.cable_width_min;
                minY = BusGeometry.cable_low_offset;
                minZ = BusGeometry.cable_width_min;
                maxX = (renderDir[ForgeDirection.EAST.ordinal()]) ? BusGeometry.cable_extend_max : BusGeometry.cable_width_max;
                maxY = BusGeometry.cable_low_height;
                maxZ = BusGeometry.cable_width_max;
                
                maxY += BusGeometry.zfight;
                
                minX = (corners[ForgeDirection.WEST.ordinal()]) ? BusGeometry.cable_corner_min : minX;
                maxX = (corners[ForgeDirection.EAST.ordinal()]) ? BusGeometry.cable_corner_max : maxX;
                
                renderer.setRenderBounds(minX, minY, minZ, BusGeometry.cable_width_min, maxY, maxZ);
                renderer.renderStandardBlock(block, x, y, z);
                
                renderer.setRenderBounds(BusGeometry.cable_width_max, minY, minZ, maxX, maxY, maxZ);
                renderer.renderStandardBlock(block, x, y, z);
            }
            // Render North/South
            if (renderDir[ForgeDirection.NORTH.ordinal()] || renderDir[ForgeDirection.SOUTH.ordinal()])
            {
                minX = BusGeometry.cable_width_min;
                minY = BusGeometry.cable_low_offset;
                minZ = (renderDir[ForgeDirection.NORTH.ordinal()]) ? BusGeometry.cable_extend_min : BusGeometry.cable_width_min;
                maxX = BusGeometry.cable_width_max;
                maxY = BusGeometry.cable_low_height;
                maxZ = (renderDir[ForgeDirection.SOUTH.ordinal()]) ? BusGeometry.cable_extend_max : BusGeometry.cable_width_max;
                
                minZ = (corners[ForgeDirection.NORTH.ordinal()]) ? BusGeometry.cable_corner_min : minZ;
                maxZ = (corners[ForgeDirection.SOUTH.ordinal()]) ? BusGeometry.cable_corner_max : maxZ;
                
                renderer.setRenderBounds(minX, minY, minZ, maxX, maxY, BusGeometry.cable_width_min);
                renderer.renderStandardBlock(block, x, y, z);
                
                renderer.setRenderBounds(minX, minY, BusGeometry.cable_width_max, maxX, maxY, maxZ);
                renderer.renderStandardBlock(block, x, y, z);
            }

            minX = BusGeometry.cable_width_min;
            minY = BusGeometry.cable_low_offset;
            minZ = BusGeometry.cable_width_min;
            maxX = BusGeometry.cable_width_max;
            maxY = BusGeometry.cable_low_height;
            maxZ = BusGeometry.cable_width_max;
            
            renderer.setRenderBounds(minX, minY, minZ, maxX, maxY, maxZ);
            renderer.renderStandardBlock(block, x, y, z);

            break;
        case 1: // UP
            // Render East/West
            if (renderDir[ForgeDirection.WEST.ordinal()] || renderDir[ForgeDirection.EAST.ordinal()])
            {
                minX = (renderDir[ForgeDirection.WEST.ordinal()]) ? BusGeometry.cable_extend_min : BusGeometry.cable_width_min;
                minY = BusGeometry.cable_high_offset;
                minZ = BusGeometry.cable_width_min;
                maxX = (renderDir[ForgeDirection.EAST.ordinal()]) ? BusGeometry.cable_extend_max : BusGeometry.cable_width_max;
                maxY = BusGeometry.cable_high_height;
                maxZ = BusGeometry.cable_width_max;
                
                minY -= BusGeometry.zfight;
                
                minX = (corners[ForgeDirection.WEST.ordinal()]) ? BusGeometry.cable_corner_min : minX;
                maxX = (corners[ForgeDirection.EAST.ordinal()]) ? BusGeometry.cable_corner_max : maxX;
                
                renderer.setRenderBounds(minX, minY, minZ, BusGeometry.cable_width_min, maxY, maxZ);
                renderer.renderStandardBlock(block, x, y, z);
                
                renderer.setRenderBounds(BusGeometry.cable_width_max, minY, minZ, maxX, maxY, maxZ);
                renderer.renderStandardBlock(block, x, y, z);
            }
            // Render North/South
            if (renderDir[ForgeDirection.NORTH.ordinal()] || renderDir[ForgeDirection.SOUTH.ordinal()])
            {
                minX = BusGeometry.cable_width_min;
                minY = BusGeometry.cable_high_offset;
                minZ = (renderDir[ForgeDirection.NORTH.ordinal()]) ? BusGeometry.cable_extend_min : BusGeometry.cable_width_min;
                maxX = BusGeometry.cable_width_max;
                maxY = BusGeometry.cable_high_height;
                maxZ = (renderDir[ForgeDirection.SOUTH.ordinal()]) ? BusGeometry.cable_extend_max : BusGeometry.cable_width_max;
                
                minZ = (corners[ForgeDirection.NORTH.ordinal()]) ? BusGeometry.cable_corner_min : minZ;
                maxZ = (corners[ForgeDirection.SOUTH.ordinal()]) ? BusGeometry.cable_corner_max : maxZ;
                
                renderer.setRenderBounds(minX, minY, minZ, maxX, maxY, BusGeometry.cable_width_min);
                renderer.renderStandardBlock(block, x, y, z);
                
                renderer.setRenderBounds(minX, minY, BusGeometry.cable_width_max, maxX, maxY, maxZ);
                renderer.renderStandardBlock(block, x, y, z);
            }

            minX = BusGeometry.cable_width_min;
            minY = BusGeometry.cable_high_offset;
            minZ = BusGeometry.cable_width_min;
            maxX = BusGeometry.cable_width_max;
            maxY = BusGeometry.cable_high_height;
            maxZ = BusGeometry.cable_width_max;
            
            renderer.setRenderBounds(minX, minY, minZ, maxX, maxY, maxZ);
            renderer.renderStandardBlock(block, x, y, z);
            
            break;
        case 2: // NORTH
            // Render East/West
            if (renderDir[ForgeDirection.WEST.ordinal()] || renderDir[ForgeDirection.EAST.ordinal()])
            {
                minX = (renderDir[ForgeDirection.WEST.ordinal()]) ? BusGeometry.cable_extend_min : BusGeometry.cable_width_min;
                minY = BusGeometry.cable_width_min;
                minZ = BusGeometry.cable_low_offset;
                maxX = (renderDir[ForgeDirection.EAST.ordinal()]) ? BusGeometry.cable_extend_max : BusGeometry.cable_width_max;
                maxY = BusGeometry.cable_width_max;
                maxZ = BusGeometry.cable_low_height;
                
                minX = (corners[ForgeDirection.WEST.ordinal()]) ? BusGeometry.cable_corner_min : minX;
                maxX = (corners[ForgeDirection.EAST.ordinal()]) ? BusGeometry.cable_corner_max : maxX;
                
                renderer.setRenderBounds(minX, minY, minZ, BusGeometry.cable_width_min, maxY, maxZ);
                renderer.renderStandardBlock(block, x, y, z);
                
                renderer.setRenderBounds(BusGeometry.cable_width_max, minY, minZ, maxX, maxY, maxZ);
                renderer.renderStandardBlock(block, x, y, z);
            }
            // Render Up/Down
            if (renderDir[ForgeDirection.DOWN.ordinal()] || renderDir[ForgeDirection.UP.ordinal()])
            {
                minX = BusGeometry.cable_width_min;
                minY = (renderDir[ForgeDirection.DOWN.ordinal()]) ? BusGeometry.cable_extend_min : BusGeometry.cable_width_min;
                minZ = BusGeometry.cable_low_offset;
                maxX = BusGeometry.cable_width_max;
                maxY = (renderDir[ForgeDirection.UP.ordinal()]) ? BusGeometry.cable_extend_max : BusGeometry.cable_width_max;
                maxZ = BusGeometry.cable_low_height;
                
                minY += (placed[ForgeDirection.DOWN.ordinal()]) ? BusGeometry.cable_low_height : 0;
                maxY -= (placed[ForgeDirection.UP.ordinal()]) ? BusGeometry.cable_low_height : 0; 
                
                renderer.setRenderBounds(minX, BusGeometry.cable_width_max, minZ, maxX, maxY, maxZ);
                renderer.renderStandardBlock(block, x, y, z);
                
                renderer.setRenderBounds(minX, minY, minZ, maxX, BusGeometry.cable_width_min, maxZ);
                renderer.renderStandardBlock(block, x, y, z);
            }

            minX = BusGeometry.cable_width_min;
            minY = BusGeometry.cable_width_min;
            minZ = BusGeometry.cable_low_offset;
            maxX = BusGeometry.cable_width_max;
            maxY = BusGeometry.cable_width_max;
            maxZ = BusGeometry.cable_low_height;
            
            renderer.setRenderBounds(minX, minY, minZ, maxX, maxY, maxZ);
            renderer.renderStandardBlock(block, x, y, z);

            break;
        case 3: // SOUTH
            // Render East/West
            if (renderDir[ForgeDirection.WEST.ordinal()] || renderDir[ForgeDirection.EAST.ordinal()])
            {
                minX = (renderDir[ForgeDirection.WEST.ordinal()]) ? BusGeometry.cable_extend_min : BusGeometry.cable_width_min;
                minY = BusGeometry.cable_width_min;
                minZ = BusGeometry.cable_high_offset;
                maxX = (renderDir[ForgeDirection.EAST.ordinal()]) ? BusGeometry.cable_extend_max : BusGeometry.cable_width_max;
                maxY = BusGeometry.cable_width_max;
                maxZ = BusGeometry.cable_high_height;
                
                minX = (corners[ForgeDirection.WEST.ordinal()]) ? BusGeometry.cable_corner_min : minX;
                maxX = (corners[ForgeDirection.EAST.ordinal()]) ? BusGeometry.cable_corner_max : maxX;
                
                renderer.setRenderBounds(minX, minY, minZ, BusGeometry.cable_width_min, maxY, maxZ);
                renderer.renderStandardBlock(block, x, y, z);
                
                renderer.setRenderBounds(BusGeometry.cable_width_max, minY, minZ, maxX, maxY, maxZ);
                renderer.renderStandardBlock(block, x, y, z);
            }
            // Render Up/Down
            if (renderDir[ForgeDirection.DOWN.ordinal()] || renderDir[ForgeDirection.UP.ordinal()])
            {
                minX = BusGeometry.cable_width_min;
                minY = (renderDir[ForgeDirection.DOWN.ordinal()]) ? BusGeometry.cable_extend_min : BusGeometry.cable_width_min;
                minZ = BusGeometry.cable_high_offset;
                maxX = BusGeometry.cable_width_max;
                maxY = (renderDir[ForgeDirection.UP.ordinal()]) ? BusGeometry.cable_extend_max : BusGeometry.cable_width_max;
                maxZ = BusGeometry.cable_high_height;
                
                minY += (placed[ForgeDirection.DOWN.ordinal()]) ? BusGeometry.cable_low_height : 0;
                maxY -= (placed[ForgeDirection.UP.ordinal()]) ? BusGeometry.cable_low_height : 0; 
                
                renderer.setRenderBounds(minX, BusGeometry.cable_width_max, minZ, maxX, maxY, maxZ);
                renderer.renderStandardBlock(block, x, y, z);
                
                renderer.setRenderBounds(minX, minY, minZ, maxX, BusGeometry.cable_width_min, maxZ);
                renderer.renderStandardBlock(block, x, y, z);
            }

            minX = BusGeometry.cable_width_min;
            minY = BusGeometry.cable_width_min;
            minZ = BusGeometry.cable_high_offset;
            maxX = BusGeometry.cable_width_max;
            maxY = BusGeometry.cable_width_max;
            maxZ = BusGeometry.cable_high_height;
            
            renderer.setRenderBounds(minX, minY, minZ, maxX, maxY, maxZ);
            renderer.renderStandardBlock(block, x, y, z);

            break;
        case 4: // WEST
            // Render North/South
            if (renderDir[ForgeDirection.NORTH.ordinal()] || renderDir[ForgeDirection.SOUTH.ordinal()])
            {
                minX = BusGeometry.cable_low_offset;
                minY = BusGeometry.cable_width_min;
                minZ = (renderDir[ForgeDirection.NORTH.ordinal()]) ? BusGeometry.cable_extend_min : BusGeometry.cable_width_min;
                maxX = BusGeometry.cable_low_height;
                maxY = BusGeometry.cable_width_max;
                maxZ = (renderDir[ForgeDirection.SOUTH.ordinal()]) ? BusGeometry.cable_extend_max : BusGeometry.cable_width_max;
                
                minZ += (placed[ForgeDirection.NORTH.ordinal()]) ? BusGeometry.cable_low_height : 0;
                maxZ -= (placed[ForgeDirection.SOUTH.ordinal()]) ? BusGeometry.cable_low_height : 0; 
                
                renderer.setRenderBounds(minX, minY, minZ, maxX, maxY, BusGeometry.cable_width_min);
                renderer.renderStandardBlock(block, x, y, z);
                
                renderer.setRenderBounds(minX, minY, BusGeometry.cable_width_max, maxX, maxY, maxZ);
                renderer.renderStandardBlock(block, x, y, z);
            }
            // Render Up/Down
            if (renderDir[ForgeDirection.DOWN.ordinal()] || renderDir[ForgeDirection.UP.ordinal()])
            {
                minX = BusGeometry.cable_low_offset;
                minY = (renderDir[ForgeDirection.DOWN.ordinal()]) ? BusGeometry.cable_extend_min : BusGeometry.cable_width_min;
                minZ = BusGeometry.cable_width_min;
                maxX = BusGeometry.cable_low_height;
                maxY = (renderDir[ForgeDirection.UP.ordinal()]) ? BusGeometry.cable_extend_max : BusGeometry.cable_width_max;
                maxZ = BusGeometry.cable_width_max;
                
                minY += (placed[ForgeDirection.DOWN.ordinal()]) ? BusGeometry.cable_low_height : 0;
                maxY -= (placed[ForgeDirection.UP.ordinal()]) ? BusGeometry.cable_low_height : 0; 
                
                renderer.setRenderBounds(minX, BusGeometry.cable_width_max, minZ, maxX, maxY, maxZ);
                renderer.renderStandardBlock(block, x, y, z);
                
                renderer.setRenderBounds(minX, minY, minZ, maxX, BusGeometry.cable_width_min, maxZ);
                renderer.renderStandardBlock(block, x, y, z);
            }

            minX = BusGeometry.cable_low_offset;
            minY = BusGeometry.cable_width_min;
            minZ = BusGeometry.cable_width_min;
            maxX = BusGeometry.cable_low_height;
            maxY = BusGeometry.cable_width_max;
            maxZ = BusGeometry.cable_width_max;
            
            renderer.setRenderBounds(minX, minY, minZ, maxX, maxY, maxZ);
            renderer.renderStandardBlock(block, x, y, z);

            break;
        case 5: // EAST
            // Render North/South
            if (renderDir[ForgeDirection.NORTH.ordinal()] || renderDir[ForgeDirection.SOUTH.ordinal()])
            {
                minX = BusGeometry.cable_high_offset;
                minY = BusGeometry.cable_width_min;
                minZ = (renderDir[ForgeDirection.NORTH.ordinal()]) ? BusGeometry.cable_extend_min : BusGeometry.cable_width_min;
                maxX = BusGeometry.cable_high_height;
                maxY = BusGeometry.cable_width_max;
                maxZ = (renderDir[ForgeDirection.SOUTH.ordinal()]) ? BusGeometry.cable_extend_max : BusGeometry.cable_width_max;
                
                minZ += (placed[ForgeDirection.NORTH.ordinal()]) ? BusGeometry.cable_low_height : 0;
                maxZ -= (placed[ForgeDirection.SOUTH.ordinal()]) ? BusGeometry.cable_low_height : 0; 
                
                renderer.setRenderBounds(minX, minY, minZ, maxX, maxY, BusGeometry.cable_width_min);
                renderer.renderStandardBlock(block, x, y, z);
                
                renderer.setRenderBounds(minX, minY, BusGeometry.cable_width_max, maxX, maxY, maxZ);
                renderer.renderStandardBlock(block, x, y, z);
            }
            // Render Up/Down
            if (renderDir[ForgeDirection.DOWN.ordinal()] || renderDir[ForgeDirection.UP.ordinal()])
            {
                minX = BusGeometry.cable_high_offset;
                minY = (renderDir[ForgeDirection.DOWN.ordinal()]) ? BusGeometry.cable_extend_min : BusGeometry.cable_width_min;
                minZ = BusGeometry.cable_width_min;
                maxX = BusGeometry.cable_high_height;
                maxY = (renderDir[ForgeDirection.UP.ordinal()]) ? BusGeometry.cable_extend_max : BusGeometry.cable_width_max;
                maxZ = BusGeometry.cable_width_max;
                
                minY += (placed[ForgeDirection.DOWN.ordinal()]) ? BusGeometry.cable_low_height : 0;
                maxY -= (placed[ForgeDirection.UP.ordinal()]) ? BusGeometry.cable_low_height : 0; 
                
                renderer.setRenderBounds(minX, BusGeometry.cable_width_max, minZ, maxX, maxY, maxZ);
                renderer.renderStandardBlock(block, x, y, z);
                
                renderer.setRenderBounds(minX, minY, minZ, maxX, BusGeometry.cable_width_min, maxZ);
                renderer.renderStandardBlock(block, x, y, z);
            }

            minX = BusGeometry.cable_high_offset;
            minY = BusGeometry.cable_width_min;
            minZ = BusGeometry.cable_width_min;
            maxX = BusGeometry.cable_high_height;
            maxY = BusGeometry.cable_width_max;
            maxZ = BusGeometry.cable_width_max;
            
            renderer.setRenderBounds(minX, minY, minZ, maxX, maxY, maxZ);
            renderer.renderStandardBlock(block, x, y, z);

            break;
        default:
            return;
        }

    }

    private void renderStandardBlock (Block block, int meta, RenderBlocks renderer)
    {
        Tessellator tessellator = Tessellator.instance;
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, -1.0F, 0.0F);
        renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, block.func_149691_a(0, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, block.func_149691_a(1, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, -1.0F);
        renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, block.func_149691_a(2, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, block.func_149691_a(3, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(-1.0F, 0.0F, 0.0F);
        renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, block.func_149691_a(4, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, block.func_149691_a(5, meta));
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
        return renderID;
    }
}
