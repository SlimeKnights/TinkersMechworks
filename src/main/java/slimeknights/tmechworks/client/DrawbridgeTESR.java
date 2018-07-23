package slimeknights.tmechworks.client;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import slimeknights.tmechworks.blocks.RedstoneMachine;
import slimeknights.tmechworks.blocks.logic.drawbridge.DrawbridgeLogicBase;

public class DrawbridgeTESR extends TileEntitySpecialRenderer<DrawbridgeLogicBase> {

    @Override
    public void render(DrawbridgeLogicBase te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        GlStateManager.pushAttrib();
        GlStateManager.pushMatrix();

        // Translate to the location of our tile entity
        GlStateManager.translate(x, y, z);
        GlStateManager.disableRescaleNormal();

        // Render the rotating handles
        renderDrawbridge(te);


        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }

    public void renderDrawbridge(DrawbridgeLogicBase te) {
        GlStateManager.pushMatrix();

        //GlStateManager.translate(.5, 0, .5);

        RenderHelper.disableStandardItemLighting();
        this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        if (Minecraft.isAmbientOcclusionEnabled()) {
            GlStateManager.shadeModel(GL11.GL_SMOOTH);
        } else {
            GlStateManager.shadeModel(GL11.GL_FLAT);
        }

        // Translate back to local view coordinates so that we can do the acual rendering here
        GlStateManager.translate(-te.getPos().getX(), -te.getPos().getY(), -te.getPos().getZ());

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);

        Item drawbridgeBlock = te.getDisguiseBlock().getItem();

        IBlockState state;
        if (drawbridgeBlock == new ItemStack(Blocks.AIR).getItem()) {
            state = te.getWorld().getBlockState(te.getPos()).withProperty(RedstoneMachine.FACING, te.getFacingDirection());
        } else {
            state = Block.getBlockFromItem(drawbridgeBlock).getStateFromMeta(drawbridgeBlock.getMetadata(te.getDisguiseBlock()));
        }
        // ModBlocks.pedestalBlock.getDefaultState().withProperty(PedestalBlock.IS_HANDLES, true);
        BlockRendererDispatcher dispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
        IBakedModel model = dispatcher.getModelForState(state);
        dispatcher.getBlockModelRenderer().renderModel(te.getWorld(), model, state, te.getPos(), buffer, true);


        tessellator.draw();

        RenderHelper.enableStandardItemLighting();
        GlStateManager.popMatrix();
    }
}
