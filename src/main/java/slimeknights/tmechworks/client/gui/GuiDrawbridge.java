package slimeknights.tmechworks.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import slimeknights.tmechworks.inventory.ContainerDrawbridge;
import slimeknights.tmechworks.library.Util;

public class GuiDrawbridge extends GuiContainer
{
    protected static final ResourceLocation TEX_BACKGROUND = Util.getResource("textures/gui/drawbridge.png");

    protected ContainerDrawbridge container;

    public GuiDrawbridge (ContainerDrawbridge inventorySlotsIn)
    {
        super(inventorySlotsIn);

        container = inventorySlotsIn;
    }

    @Override protected void drawGuiContainerBackgroundLayer (float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1F, 1F, 1F);
        mc.getTextureManager().bindTexture(TEX_BACKGROUND);

        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
    }

    @Override protected void drawGuiContainerForegroundLayer (int mouseX, int mouseY)
    {
        String s = container.getTile().getDisplayName().getUnformattedText();
        fontRendererObj.drawString(s, xSize / 2 - fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
        fontRendererObj.drawString(container.inventoryPlayer.getDisplayName().getUnformattedText(), 8, ySize - 96 + 2, 4210752);
    }
}
