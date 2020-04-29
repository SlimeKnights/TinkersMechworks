package slimeknights.tmechworks.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import slimeknights.tmechworks.common.inventory.DisguiseContainer;

public class DisguiseScreen extends ContainerScreen<DisguiseContainer> {
    public static final ResourceLocation SCREEN_LOCATION = new ResourceLocation("tmechworks", "textures/gui/generic_1.png");

    public DisguiseScreen(DisguiseContainer container, PlayerInventory inventory, ITextComponent name) {
        super(container, inventory, name);
    }

    public static DisguiseScreen create(DisguiseContainer container, PlayerInventory player, ITextComponent title){
        return new DisguiseScreen(container, player, title);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(SCREEN_LOCATION);

        blit(guiLeft, guiTop, 0, 0, xSize, ySize); // Background
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        String s = title.getFormattedText();
        font.drawString(s, xSize / 2F - font.getStringWidth(s) / 2F,  6, 4210752);

        font.drawString(playerInventory.getDisplayName().getFormattedText(), 8, ySize - 96 + 2, 4210752);
    }
}
