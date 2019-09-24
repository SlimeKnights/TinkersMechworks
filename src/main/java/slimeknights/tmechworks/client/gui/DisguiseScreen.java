package slimeknights.tmechworks.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.network.PacketDistributor;
import slimeknights.tmechworks.client.gui.components.ArrowWidget;
import slimeknights.tmechworks.common.blocks.tileentity.DrawbridgeTileEntity;
import slimeknights.tmechworks.common.inventory.DisguiseContainer;
import slimeknights.tmechworks.common.inventory.DrawbridgeContainer;
import slimeknights.tmechworks.common.network.PacketHandler;
import slimeknights.tmechworks.common.network.packet.ServerReopenUiPacket;
import slimeknights.tmechworks.common.network.packet.UpdatePlaceDirectionPacket;
import slimeknights.tmechworks.library.Util;

import java.util.List;

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
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
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
