package tmechworks.client.block;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import tmechworks.blocks.FilterBlock;
import tmechworks.lib.util.CoordTuple;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class FilterRender implements ISimpleBlockRenderingHandler
{
    public static int renderID = RenderingRegistry.getNextAvailableRenderId();

    //Width of the frame pieces.
    public static final double sideWidth = FilterBlock.sideWidth;
    //Thickness of the whole assembly
    public static final double thickness = FilterBlock.thickness;

    @Override
    public void renderInventoryBlock (Block block, int metadata, int modelID, RenderBlocks renderer)
    {
        //Long sides.
        renderer.setRenderBounds(0.0, 0.0D, 0.0, sideWidth, thickness, 1.0D);
        this.renderStandardBlockInv(block, metadata, renderer);

        renderer.setRenderBounds(1.0D - sideWidth, 0.0D, 0.0, 1.0D, thickness, 1.0D);
        this.renderStandardBlockInv(block, metadata, renderer);
        
        //Short sides.
        renderer.setRenderBounds(sideWidth, 0.0D, 0.0, 1.0D - sideWidth, thickness, sideWidth);
        this.renderStandardBlockInv(block, metadata, renderer);

        renderer.setRenderBounds(sideWidth, 0.0D, 1.0D - sideWidth, 1.0D - sideWidth, thickness, 1.0D);
        this.renderStandardBlockInv(block, metadata, renderer);

        //Filter mesh
        FilterBlock fb = (FilterBlock) block;
        if (fb.getMeshIcon(metadata) != null)
        {
            renderer.setOverrideBlockTexture(fb.getMeshIcon(metadata));
            renderer.setRenderBounds(sideWidth, thickness / 2.0D, sideWidth, 1.0D - sideWidth, (thickness / 2.0D) + 0.02F, 1.0D - sideWidth);
            this.renderStandardBlockInv(block, metadata, renderer);
            renderer.clearOverrideBlockTexture();
        }
    }

    @Override
    public boolean renderWorldBlock (IBlockAccess world, int x, int y, int z, Block block, int modelID, RenderBlocks renderer)
    {
        if (modelID == renderID)
        {
            CoordTuple position = new CoordTuple(x, y, z);
            int metadata = world.getBlockMetadata(position.x, position.y, position.z);
            FilterBlock fb = (FilterBlock) block;
            double bottom = 0.0D;
            double top = thickness;
            if (fb.isTop(world, position))
            {
                bottom = 1.0D - thickness;
                top = 1.0D;
            }
            //Long sides.
            renderer.setRenderBounds(0.0D, bottom, 0.0, sideWidth, top, 1.0D);
            renderer.renderStandardBlock(block, position.x, position.y, position.z);

            renderer.setRenderBounds(1.0D - sideWidth, bottom, 0.0, 1.0D, top, 1.0D);
            renderer.renderStandardBlock(block, position.x, position.y, position.z);
            //Short sides.
            renderer.setRenderBounds(sideWidth, bottom, 0.0, 1.0D - sideWidth, top, sideWidth);
            renderer.renderStandardBlock(block, position.x, position.y, position.z);

            renderer.setRenderBounds(sideWidth, bottom, 1.0D - sideWidth, 1.0D - sideWidth, top, 1.0D);
            renderer.renderStandardBlock(block, position.x, position.y, position.z);

            //Filter mesh
            if (fb.getMeshIcon(metadata) != null)
            {
                renderer.setOverrideBlockTexture(fb.getMeshIcon(metadata));
                renderer.setRenderBounds(sideWidth, bottom + (thickness / 6.0D), sideWidth, 1.0D - sideWidth, top - (thickness / 6.0D), 1.0D - sideWidth);
                renderer.renderStandardBlock(block, position.x, position.y, position.z);
                renderer.clearOverrideBlockTexture();
            }
            return true;
        }
        return false;
    }

    @Override
    public int getRenderId ()
    {
        return renderID;
    }

    private void renderStandardBlockInv (Block block, int meta, RenderBlocks renderer)
    {
        Tessellator tessellator = Tessellator.instance;
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, -1.0F, 0.0F);
        renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(0, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(1, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, -1.0F);
        renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(2, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(3, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(-1.0F, 0.0F, 0.0F);
        renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(4, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(5, meta));
        tessellator.draw();
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
    }

    @Override
    public boolean shouldRender3DInInventory (int modelId)
    {
        return true;
    }
}
