package slimeknights.tmechworks.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.network.PacketDistributor;
import slimeknights.tmechworks.client.gui.components.ArrowWidget;
import slimeknights.tmechworks.common.blocks.tileentity.DrawbridgeTileEntity;
import slimeknights.tmechworks.common.inventory.DrawbridgeContainer;
import slimeknights.tmechworks.common.network.PacketHandler;
import slimeknights.tmechworks.common.network.packet.ServerReopenUiPacket;
import slimeknights.tmechworks.common.network.packet.UpdatePlaceDirectionPacket;
import slimeknights.tmechworks.library.Util;

import java.util.List;

public class DrawbridgeScreen extends ContainerScreen<DrawbridgeContainer> {
    public static final ResourceLocation SCREEN_LOCATION = new ResourceLocation("tmechworks", "textures/gui/drawbridge.png");
    public static final ResourceLocation ADVANCED_LOCATION = new ResourceLocation("tmechworks", "textures/gui/drawbridge_advanced.png");

    public final boolean isAdvanced;
    private final int slotCount;

    public DrawbridgeScreen(DrawbridgeContainer container, PlayerInventory inventory, ITextComponent name) {
        super(container, inventory, name);

        isAdvanced = container.getTile().stats.isAdvanced;
        slotCount = container.getTile().slots.getSizeInventory();
    }

    public static DrawbridgeScreen create(DrawbridgeContainer container, PlayerInventory player, ITextComponent title){
        return new DrawbridgeScreen(container, player, title);
    }

    @Override
    protected void init() {
        super.init();

        int aX = 0, aY = 0;

        if(isAdvanced) {
            aX = guiLeft + 192;
            aY = guiTop + 10;
        } else {
            aX = (this.width - this.xSize) / 2 + 110;
            aY = (this.height - this.ySize) / 2 + 20;
        }            //blit(guiLeft + 191, guiTop + 4, 0, 196, 63, 60);

        ArrowWidget arrow = new ArrowWidget(aX, aY, width, height, true, this::arrowClicked);
        updateSelection(arrow);
        addButton(arrow);
    }

    @Override
    public void tick() {
        super.tick();

        DrawbridgeTileEntity te = container.getTile();

        // Reinitialize UI if the drawbridge size or type changes
        if(isAdvanced != te.stats.isAdvanced || slotCount != te.slots.getSizeInventory()) {
            PacketHandler.send(PacketDistributor.SERVER.noArg(), new ServerReopenUiPacket(container.getTile().getPos()));
        }
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
        blit(guiLeft - 44, guiTop + ySize - 65, 0, 182, 47, 60); // Upgrades cutout

        if(!isAdvanced) {
//        drawSlicedBox(guiLeft + 34, guiTop + 35, 18, 18, 17, 166); // Disguise slot
            drawSlicedBox(guiLeft + 75, guiTop + 31, 26, 26, 17, 166); // Drawbridge slot
        } else {
            this.minecraft.getTextureManager().bindTexture(ADVANCED_LOCATION);

            blit(guiLeft - 18, guiTop - 80, 0, 0, 213, 148); // Advanced cutout
            blit(guiLeft + 191, guiTop + 4, 0, 196, 63, 60); // Arrow cutout

            drawAdvancedSlots();
        }
    }

    private void drawAdvancedSlots() {
        for(Slot s : container.mainSlots){
            blit(guiLeft + s.xPos - 1, guiTop + s.yPos - 1, 0, 166, 18, 18);
        }
    }

    @Override
    protected void renderHoveredToolTip(int mouseX, int mouseY) {
        if(this.hoveredSlot == null)
            return;

        if(!isAdvanced || this.hoveredSlot.getHasStack()) {
            super.renderHoveredToolTip(mouseX, mouseY);
        } else if(hoveredSlot.inventory == getContainer().getTile().slots) {
            renderTooltip(I18n.format(Util.prefix("gui.blocknum"), hoveredSlot.getSlotIndex() + 1), mouseX, mouseY);
        }
    }

    @Override
    public List<String> getTooltipFromItem(ItemStack stack) {
        List<String> list = super.getTooltipFromItem(stack);

        if(isAdvanced && hoveredSlot.inventory == getContainer().getTile().slots) {
            list.add("");
            list.add(TextFormatting.GRAY + I18n.format(Util.prefix("gui.blocknum"), hoveredSlot.getSlotIndex() + 1));
        }

        return list;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        int screenTop = isAdvanced ? -80  : 0;

        String s = title.getFormattedText();
        font.drawString(s, xSize / 2F - font.getStringWidth(s) / 2F, screenTop + 6, 4210752);

        font.drawString(playerInventory.getDisplayName().getFormattedText(), 8, ySize - 96 + 2, 4210752);

        float scale = .75F;
        float invScale = 1 / scale;

        GlStateManager.scalef(scale, scale, scale);
        String upgrades = I18n.format(Util.prefix("gui.upgrades"));
        font.drawString(upgrades, 47 / 2F - font.getStringWidth(upgrades) / 2F - 50, (xSize - 69) * invScale, 4210752);
        GlStateManager.scalef(invScale, invScale, invScale);
    }

    private void arrowClicked(ArrowWidget widget, ArrowWidget.Arrow arrow) {
        PacketHandler.send(PacketDistributor.SERVER.noArg(), new UpdatePlaceDirectionPacket(container.getTile().getPos(), arrow.ordinal()));
        container.getTile().setPlaceDirection(arrow.ordinal());
        updateSelection(widget);
    }

    private void updateSelection(ArrowWidget arrow) {
        for (ArrowWidget.Arrow a : ArrowWidget.Arrow.values()) {
            if (arrow.getState(a) == ArrowWidget.ArrowState.SELECTED)
                arrow.setState(a, ArrowWidget.ArrowState.ENABLED);
        }

        arrow.setState(ArrowWidget.Arrow.values()[container.getTile().getRawPlaceDirection().ordinal()], ArrowWidget.ArrowState.SELECTED);
        arrow.setState(ArrowWidget.Arrow.values()[Direction.values().length + container.getTile().getPlaceAngle().ordinal()], ArrowWidget.ArrowState.SELECTED);
    }

    private void drawSlicedBox(int x, int y, int width, int height, int u, int v) {
        // Corners
        blit(x, y, u, v, 4, 4); // Top Left
        blit(x + width - 4, y, u + 12, v, 4, 4); // Top Right
        blit(x, y + height - 4, u, v + 12, 4, 4); // Bottom Left
        blit(x + width - 4, y + height - 4, u + 12, v + 12, 4, 4); // Bottom Right

        // Sides
        blit(x + 4, y, width - 8, 4, u + 6, u + 10, v, v + 4); // Top
        blit(x + 4, y + height - 4, width - 8, 4, u + 6, u + 10, v + 12, v + 16); // Bottom
        blit(x, y + 4, 4, height - 8, u, u + 4, v + 6, v + 10); // Left
        blit(x + width - 4, y + 4, 4, height - 8, u + 12, u + 16, v + 6, v + 10); // Right

        // Center
        blit(x + 4, y + 4, width - 8, height - 8, u + 6, u + 10, v + 6, v + 10);
    }

    public static void blit(int x, int y, int w, int h, int minU, int maxU, int minV, int maxV) {
        blit(x, y, w, h, minU, maxU, minV, maxV, 256F, 256F);
    }

    public static void blit(int x, int y, int w, int h, int minU, int maxU, int minV, int maxV, float tw, float th) {
        innerBlit(x, x + w, y, y + h, 0, minU / tw, maxU / tw, minV / th, maxV / th);
    }
}
