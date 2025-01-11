package slimeknights.tmechworks.client.gui;

import com.mojang.blaze3d.vertex.*;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import com.mojang.math.Matrix4f;
import net.minecraftforge.network.PacketDistributor;
import slimeknights.tmechworks.TMechworks;
import slimeknights.tmechworks.api.disguisestate.DisguiseStates;
import slimeknights.tmechworks.client.gui.components.ArrowWidget;
import slimeknights.tmechworks.client.gui.components.DisguiseStateWidget;
import slimeknights.tmechworks.common.MechworksTags;
import slimeknights.tmechworks.common.blocks.entity.DrawbridgeBlockEntity;
import slimeknights.tmechworks.common.inventory.DrawbridgeContainerMenu;
import slimeknights.tmechworks.common.network.PacketHandler;
import slimeknights.tmechworks.common.network.packet.ServerReopenUiPacket;
import slimeknights.tmechworks.common.network.packet.UpdatePlaceDirectionPacket;
import slimeknights.tmechworks.library.Util;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class DrawbridgeScreen extends AbstractContainerScreen<DrawbridgeContainerMenu> {
    public static final ResourceLocation SCREEN_LOCATION = new ResourceLocation(TMechworks.modId, "textures/gui/drawbridge.png");
    public static final ResourceLocation ADVANCED_LOCATION = new ResourceLocation(TMechworks.modId, "textures/gui/drawbridge_advanced.png");

    public DisguiseStateWidget disguiseWidget;

    public final boolean isAdvanced;
    private final int slotCount;

    public DrawbridgeScreen(DrawbridgeContainerMenu container, Inventory inventory, Component name) {
        super(container, inventory, name);

        isAdvanced = container.getTile().stats.isAdvanced;
        slotCount = container.getTile().slots.getContainerSize();
    }

    public static DrawbridgeScreen create(DrawbridgeContainerMenu container, Inventory player, Component title){
        return new DrawbridgeScreen(container, player, title);
    }

    @Override
    protected void init() {
        super.init();

        int aX, aY, screenLeft, screenTop;

        if(isAdvanced) {
            aX = leftPos + 192;
            aY = topPos + 10;

            screenLeft = -18;
            screenTop = -80;
        } else {
            aX = (this.width - this.imageWidth) / 2 + 110;
            aY = (this.height - this.imageHeight) / 2 + 20;

            screenLeft = 0;
            screenTop = 0;
        }

        titleLabelX = screenLeft + 8;
        titleLabelY = screenTop + 6;

        ArrowWidget arrow = new ArrowWidget(aX, aY, width, height, true, this::arrowClicked);
        updateSelection(arrow);
        addRenderableWidget(arrow);

        disguiseWidget = new DisguiseStateWidget(leftPos + 198, topPos + 133, menu.getTile());
        addRenderableWidget(disguiseWidget);
    }

    @Override
    public void containerTick() {
        super.containerTick();

        DrawbridgeBlockEntity te = menu.getTile();

        // Reinitialize UI if the drawbridge size or type changes
        if(isAdvanced != te.stats.isAdvanced || slotCount != te.slots.getContainerSize()) {
            PacketHandler.send(PacketDistributor.SERVER.noArg(), new ServerReopenUiPacket(menu.getTile().getBlockPos()));
        }

        // Update disguise state controls
        ItemStack disguise = te.getDisguiseBlock();

        if (disguise.getItem() instanceof BlockItem) {
            BlockState disguiseState = ((BlockItem) disguise.getItem()).getBlock().defaultBlockState();
            disguiseWidget.setState(DisguiseStates.getForState(disguiseState), te.getDisguiseState());
        } else {
            disguiseWidget.setState(null, null);
        }
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        super.render(stack, mouseX, mouseY, partialTicks);
        this.renderTooltip(stack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(PoseStack stack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, SCREEN_LOCATION);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        blit(stack, leftPos, topPos, 0, 0, imageWidth, imageHeight); // Background
        blit(stack, leftPos - 44, topPos + imageHeight - 65, 0, 182, 47, 60); // Upgrades cutout

        blit(stack, leftPos + imageWidth - 3, topPos + imageHeight - 37, 52, 182, 29, 32); // Disguise cutout
        // 75 182
        if(disguiseWidget.getColumnCount() > 0) {
            blit(stack, leftPos + imageWidth + 22, topPos + imageHeight - 37, disguiseWidget.getColumnCount() * 8 + 2, 32, 75, 76, 182, 214);
            blit(stack, leftPos + imageWidth + 22 + disguiseWidget.getColumnCount() * 8 + 2, topPos + imageHeight - 37, 3, 32, 78, 81, 183, 214);
        }

        if(!isAdvanced) {
            drawSlicedBox(stack, leftPos + 75, topPos + 31, 26, 26, 17, 166); // Drawbridge slot
        } else {
            RenderSystem.setShaderTexture(0, ADVANCED_LOCATION);

            blit(stack, leftPos - 18, topPos - 80, 0, 0, 213, 148); // Advanced cutout
            blit(stack, leftPos + 191, topPos + 4, 0, 196, 63, 60); // Arrow cutout

            drawAdvancedSlots(stack);
        }
    }

    private void drawAdvancedSlots(PoseStack stack) {
        for(Slot s : menu.mainSlots){
            blit(stack, leftPos + s.x - 1, topPos + s.y - 1, 0, 166, 18, 18);
        }
    }

    @Override
    protected void renderTooltip(PoseStack stack, int mouseX, int mouseY) {
        if(this.hoveredSlot == null)
            return;

        if(!isAdvanced || this.hoveredSlot.hasItem()) {
            super.renderTooltip(stack, mouseX, mouseY); // renderTooltip => renderHoveredTooltip
        } else if(hoveredSlot.container == getMenu().getTile().slots) {
            renderTooltip(stack, new TranslatableComponent(Util.prefix("gui.blocknum"), hoveredSlot.getSlotIndex() + 1).setStyle(Style.EMPTY.applyFormat(ChatFormatting.GRAY)), mouseX, mouseY);
        }
    }

    @Override
    public List<Component> getTooltipFromItem(ItemStack stack) {
        List<Component> list = super.getTooltipFromItem(stack);

        if(isAdvanced && hoveredSlot.container == getMenu().getTile().slots) {
            list.add(TextComponent.EMPTY);
            list.add(new TranslatableComponent(Util.prefix("gui.blocknum"), hoveredSlot.getSlotIndex() + 1).withStyle(ChatFormatting.GRAY));
        }

        if(Block.byItem(stack.getItem()).defaultBlockState().is(MechworksTags.Blocks.DRAWBRIDGE_BLACKLIST)) {
            list.add(TextComponent.EMPTY);
            list.add(new TranslatableComponent(Util.prefix("gui.blacklisted")));
        }

        return list;
    }

    @Override
    protected void renderLabels(PoseStack stack, int mouseX, int mouseY) {
        super.renderLabels(stack, mouseX, mouseY);

        float scale = .75F;
        float invScale = 1 / scale;

        stack.scale(scale, scale, scale);
        String upgrades = I18n.get(Util.prefix("gui.upgrades"));
        font.draw(stack, upgrades, 47 / 2F - font.width(upgrades) / 2F - 50, (imageWidth - 69) * invScale, 4210752);
        stack.scale(invScale, invScale, invScale);
    }

    private void arrowClicked(ArrowWidget widget, ArrowWidget.Arrow arrow) {
        PacketHandler.send(PacketDistributor.SERVER.noArg(), new UpdatePlaceDirectionPacket(menu.getTile().getBlockPos(), arrow.ordinal()));
        menu.getTile().setPlaceDirection(arrow.ordinal());
        updateSelection(widget);
    }

    private void updateSelection(ArrowWidget arrow) {
        for (ArrowWidget.Arrow a : ArrowWidget.Arrow.values()) {
            if (arrow.getState(a) == ArrowWidget.ArrowState.SELECTED)
                arrow.setState(a, ArrowWidget.ArrowState.ENABLED);
        }

        arrow.setState(ArrowWidget.Arrow.values()[menu.getTile().getRawPlaceDirection().ordinal()], ArrowWidget.ArrowState.SELECTED);
        arrow.setState(ArrowWidget.Arrow.values()[Direction.values().length + menu.getTile().getPlaceAngle().ordinal()], ArrowWidget.ArrowState.SELECTED);
    }

    private void drawSlicedBox(PoseStack stack, int x, int y, int width, int height, int u, int v) {
        // Corners
        blit(stack, x, y, u, v, 4, 4); // Top Left
        blit(stack, x + width - 4, y, u + 12, v, 4, 4); // Top Right
        blit(stack, x, y + height - 4, u, v + 12, 4, 4); // Bottom Left
        blit(stack, x + width - 4, y + height - 4, u + 12, v + 12, 4, 4); // Bottom Right

        // Sides
        blit(stack, x + 4, y, width - 8, 4, u + 6, u + 10, v, v + 4); // Top
        blit(stack, x + 4, y + height - 4, width - 8, 4, u + 6, u + 10, v + 12, v + 16); // Bottom
        blit(stack, x, y + 4, 4, height - 8, u, u + 4, v + 6, v + 10); // Left
        blit(stack, x + width - 4, y + 4, 4, height - 8, u + 12, u + 16, v + 6, v + 10); // Right

        // Center
        blit(stack, x + 4, y + 4, width - 8, height - 8, u + 6, u + 10, v + 6, v + 10);
    }

    public static void blit(PoseStack stack, int x, int y, int w, int h, int minU, int maxU, int minV, int maxV) {
        blit(stack, x, y, w, h, minU, maxU, minV, maxV, 256F, 256F);
    }

    public static void blit(PoseStack stack, int x, int y, int w, int h, int minU, int maxU, int minV, int maxV, float tw, float th) {
        innerBlit(stack.last().pose(), x, x + w, y, y + h, 0, minU / tw, maxU / tw, minV / th, maxV / th);
    }

    private static void innerBlit(Matrix4f matrix, int x1, int x2, int y1, int y2, int blitOffset, float minU, float maxU, float minV, float maxV) {
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(matrix, (float)x1, (float)y2, (float)blitOffset).uv(minU, maxV).endVertex();
        bufferbuilder.vertex(matrix, (float)x2, (float)y2, (float)blitOffset).uv(maxU, maxV).endVertex();
        bufferbuilder.vertex(matrix, (float)x2, (float)y1, (float)blitOffset).uv(maxU, minV).endVertex();
        bufferbuilder.vertex(matrix, (float)x1, (float)y1, (float)blitOffset).uv(minU, minV).endVertex();
        bufferbuilder.end();
        BufferUploader.end(bufferbuilder);
    }
}
