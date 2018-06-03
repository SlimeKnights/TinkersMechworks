package slimeknights.tmechworks.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import slimeknights.tmechworks.TMechworks;
import slimeknights.tmechworks.client.gui.components.GuiArrowSelection;
import slimeknights.tmechworks.inventory.ContainerDrawbridge;
import slimeknights.tmechworks.library.Util;
import slimeknights.tmechworks.networking.PacketUpdatePlaceDirection;

public class GuiDrawbridge extends GuiContainer {
    protected static final ResourceLocation TEX_BACKGROUND = Util.getResource("textures/gui/drawbridge.png");

    protected ContainerDrawbridge container;

    public GuiDrawbridge(ContainerDrawbridge inventorySlotsIn) {
        super(inventorySlotsIn);

        container = inventorySlotsIn;
    }

    @Override
    public void initGui() {
        super.initGui();

        GuiArrowSelection sel = new GuiArrowSelection(0, (this.width - this.xSize) / 2 + 110, (this.height - this.ySize) / 2 + 20, width, height, true);
        updateSelection(sel);
        buttonList.add(sel);
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1F, 1F, 1F);
        mc.getTextureManager().bindTexture(TEX_BACKGROUND);

        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        String s = container.getTile().getDisplayName().getUnformattedText();
        fontRenderer.drawString(s, xSize / 2 - fontRenderer.getStringWidth(s) / 2, 6, 4210752);
        fontRenderer.drawString(container.inventoryPlayer.getDisplayName().getUnformattedText(), 8, ySize - 96 + 2, 4210752);

        GlStateManager.translate(-guiLeft, -guiTop, 0F);

        for (int i = 0; i < buttonList.size(); i++)
            buttonList.get(i).drawButtonForegroundLayer(mouseX, mouseY);

        GlStateManager.translate(guiLeft, guiTop, 0F);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button instanceof GuiArrowSelection) {
            GuiArrowSelection selBtn = (GuiArrowSelection) button;

            TMechworks.packetPipeline.network.sendToServer(new PacketUpdatePlaceDirection(container.getTile().getPos(), selBtn.getHoveredArrow().ordinal()));
            container.getTile().setPlaceDirection(selBtn.getHoveredArrow().ordinal());
            updateSelection(selBtn);
        }
    }

    private void updateSelection(GuiArrowSelection selBtn) {
        for (GuiArrowSelection.Arrow a : GuiArrowSelection.Arrow.values()) {
            if (selBtn.getState(a) == GuiArrowSelection.ArrowState.SELECTED)
                selBtn.setState(a, GuiArrowSelection.ArrowState.ENABLED);
        }

        selBtn.setState(GuiArrowSelection.Arrow.values()[container.getTile().getRawPlaceDirection().ordinal()], GuiArrowSelection.ArrowState.SELECTED);
        selBtn.setState(GuiArrowSelection.Arrow.values()[EnumFacing.values().length + container.getTile().getPlaceAngle().ordinal()], GuiArrowSelection.ArrowState.SELECTED);
    }
}
