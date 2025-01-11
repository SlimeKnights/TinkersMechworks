package slimeknights.tmechworks.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import slimeknights.tmechworks.TMechworks;
import slimeknights.tmechworks.api.disguisestate.DisguiseStates;
import slimeknights.tmechworks.client.gui.components.DisguiseStateWidget;
import slimeknights.tmechworks.common.blocks.entity.RedstoneMachineBlockEntity;
import slimeknights.tmechworks.common.inventory.DisguiseContainerMenu;

public class DisguiseScreen extends AbstractContainerScreen<DisguiseContainerMenu> {
    public static final ResourceLocation SCREEN_LOCATION = new ResourceLocation(TMechworks.modId, "textures/gui/generic_1.png");

    private DisguiseStateWidget disguiseWidget;

    public DisguiseScreen(DisguiseContainerMenu container, Inventory inventory, Component name) {
        super(container, inventory, name);
    }

    public static DisguiseScreen create(DisguiseContainerMenu container, Inventory player, Component title){
        return new DisguiseScreen(container, player, title);
    }

    @Override
    protected void init() {
        super.init();

        disguiseWidget = new DisguiseStateWidget(leftPos + 99, topPos + 30, menu.getTile());
        addRenderableWidget(disguiseWidget);
    }

    @Override
    public void containerTick() {
        super.containerTick();

        RedstoneMachineBlockEntity te = menu.getTile();
        ItemStack disguise = te.getDisguiseBlock();

        if (disguise.getItem() instanceof BlockItem) {
            BlockState disguiseState = ((BlockItem) disguise.getItem()).getBlock().defaultBlockState();
            disguiseWidget.setState(DisguiseStates.getForState(disguiseState), te.getDisguiseState());
        } else {
            disguiseWidget.setState(null, null);
        }
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, SCREEN_LOCATION);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        blit(matrixStack, leftPos, topPos, 0, 0, imageWidth, imageHeight); // Background
    }

//    @Override
//    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
//        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
//
//        String s = title.getFormattedText();
//        font.drawString(s, xSize / 2F - font.getStringWidth(s) / 2F,  6, 4210752);
//
//        font.drawString(playerInventory.getDisplayName().getFormattedText(), 8, ySize - 96 + 2, 4210752);
//    }
}
