package tmechworks.client.block;

import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import tmechworks.blocks.logic.DynamoLogic;
import tmechworks.client.model.DynamoModel;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class DynamoSpecialRender extends TileEntitySpecialRenderer
{
    private static final ResourceLocation texture = new ResourceLocation("tmechworks", "textures/entity/dynamo.png");

    /** The ModelSign instance used by the TileEntitySignRenderer */
    private final DynamoModel model = new DynamoModel();

    public void renderModel (DynamoLogic par1TileEntitySign, double x, double y, double z, float notDeltaTime)
    {
        Block block = par1TileEntitySign.getBlockType();
        GL11.glPushMatrix();
        float f1 = 1F;
        float f2;
        GL11.glTranslatef((float) x + 0.5F, (float) y + 1.0F * f1, (float) z + 0.5F);
        float f3 = 0f;
        GL11.glRotatef(-f3, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(90, 0.0F, 1.0F, 0.0F);

        this.bindTexture(texture);
        GL11.glPushMatrix();
        GL11.glScalef(f1, -f1, -f1);
        this.model.render(0.0625f, notDeltaTime);
        GL11.glPopMatrix();
        FontRenderer fontrenderer = this.func_147498_b();
        f2 = 0.016666668F * f1;
        GL11.glTranslatef(0.0F, 0.5F * f1, 0.07F * f1);
        GL11.glScalef(f2, -f2, f2);
        GL11.glNormal3f(0.0F, 0.0F, -1.0F * f2);
        GL11.glDepthMask(false);
        byte b0 = 0;

        GL11.glDepthMask(true);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
    }

    public void renderTileEntityAt (TileEntity par1TileEntity, double par2, double par4, double par6, float par8)
    {
        this.renderModel((DynamoLogic) par1TileEntity, par2, par4, par6, par8);
    }
}
