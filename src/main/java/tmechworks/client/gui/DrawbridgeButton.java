package tmechworks.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class DrawbridgeButton extends GuiButton
{
    /**
     * True for pointing right (next page), false for pointing left (previous page).
     */
    int uPos;
    int vPos;

    public DrawbridgeButton(int id, int xPos, int yPos, int u, int v, int xSize, int ySize)
    {
        super(id, xPos, yPos, xSize, ySize, "");
        uPos = u;
        vPos = v;
    }

    private static final ResourceLocation background = new ResourceLocation("tmechworks", "textures/gui/drawbridge.png");

    /**
     * Draws this button to the screen.
     */
    public void drawButton (Minecraft mc, int par2, int par3)
    {
        if (this.field_146125_m)
        {
            boolean hovering = par2 >= this.field_146128_h && par3 >= this.field_146129_i && par2 < this.field_146128_h + this.field_146120_f && par3 < this.field_146129_i + this.field_146121_g;
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            mc.getTextureManager().bindTexture(background);
            int offset = 0;
            if (!this.field_146124_l)
                offset = 46;
            else if (hovering)
                offset = 92;

            this.drawTexturedModalRect(this.field_146128_h, this.field_146129_i, uPos, vPos + offset, field_146120_f, field_146121_g);
        }
    }
}
