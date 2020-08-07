package slimeknights.tmechworks.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import slimeknights.tmechworks.TMechworks;
import slimeknights.tmechworks.api.disguisestate.DisguiseStates;
import slimeknights.tmechworks.client.gui.components.DisguiseStateWidget;
import slimeknights.tmechworks.common.blocks.tileentity.RedstoneMachineTileEntity;
import slimeknights.tmechworks.common.inventory.DisguiseContainer;

public class DisguiseScreen extends ContainerScreen<DisguiseContainer> {
    public static final ResourceLocation SCREEN_LOCATION = new ResourceLocation(TMechworks.modId, "textures/gui/generic_1.png");

    private DisguiseStateWidget disguiseWidget;

    public DisguiseScreen(DisguiseContainer container, PlayerInventory inventory, ITextComponent name) {
        super(container, inventory, name);
    }

    public static DisguiseScreen create(DisguiseContainer container, PlayerInventory player, ITextComponent title){
        return new DisguiseScreen(container, player, title);
    }

    @Override
    protected void init() {
        super.init();

        disguiseWidget = new DisguiseStateWidget(guiLeft + 99, guiTop + 30, container.getTile());
        addButton(disguiseWidget);
    }

    @Override
    public void tick() {
        super.tick();

        RedstoneMachineTileEntity te = container.getTile();
        ItemStack disguise = te.getDisguiseBlock();

        if (disguise.getItem() instanceof BlockItem) {
            BlockState disguiseState = ((BlockItem) disguise.getItem()).getBlock().getDefaultState();
            disguiseWidget.setState(DisguiseStates.getForState(disguiseState), te.getDisguiseState());
        } else {
            disguiseWidget.setState(null, null);
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.func_230459_a_(matrixStack, mouseX, mouseY); // func_230459_a_ -> renderHoveredToolTip
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(SCREEN_LOCATION);

        blit(matrixStack, guiLeft, guiTop, 0, 0, xSize, ySize); // Background
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
