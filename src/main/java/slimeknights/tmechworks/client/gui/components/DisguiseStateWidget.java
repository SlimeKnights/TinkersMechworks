package slimeknights.tmechworks.client.gui.components;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.network.PacketDistributor;
import slimeknights.tmechworks.api.disguisestate.DisguiseState;
import slimeknights.tmechworks.common.blocks.tileentity.RedstoneMachineTileEntity;
import slimeknights.tmechworks.common.network.PacketHandler;
import slimeknights.tmechworks.common.network.packet.UpdateDisguiseStatePacket;

import java.util.Collection;

public class DisguiseStateWidget extends AbstractWidget {
    private DisguiseState<?> state;
    private String stateString;

    private String hoveredState;

    private final RedstoneMachineTileEntity te;

    public DisguiseStateWidget(int x, int y, RedstoneMachineTileEntity te) {
        super(x, y, 0, 0, new TextComponent(""));

        this.te = te;
    }

    public void setState(DisguiseState<?> state, String stateString) {
        this.state = state;

        if(state != null) {
            this.stateString = state.getValueFrom(stateString).toString();
        }
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        if(state == null)
            return;

        Collection<?> states = state.getAllowedValues();

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, state.getIconSheet());

        hoveredState = null;
        boolean canHover = true;

        int i = 0;
        for(Object obj : states) {
            String state = obj != null ? obj.toString() : "null";

            int rem = states.size() - i;
            int col = Mth.floor(i / 3F);
            int row = i % 3;

            int xPos = x + col * 8;
            int yPos = y + row * 8;

            if(row == 0 && rem == 1)
                yPos += 8;
            else if(row == 0 && rem == 2)
                yPos += 4;
            else if(row == 1 && rem == 1)
                yPos += 4;

            boolean isActive = state.equals(stateString);
            boolean isHovered = false;

            if(canHover && !isActive && intersects(xPos, yPos, mouseX, mouseY)) {
                hoveredState = state;
                isHovered = true;
                canHover = false;
            } else if(intersects(xPos, yPos, mouseX, mouseY)) {
                canHover = false;
            }

            if(isActive)
                RenderSystem.setShaderColor(.47F, .36F, .2F, 1F);
            else if(isHovered)
                RenderSystem.setShaderColor(.17F, .08F, .01F, 1F);
            else
                RenderSystem.setShaderColor(.38F, .16F, .05F, 1F);

            int index = this.state.unsafeGetIconFor(obj);
            int indexX = index % 32;
            int indexY = Mth.floor(index / 32F);

            blit(stack, xPos, yPos, indexX * 8, indexY * 8, 8, 8);

            i++;
            if(i >= 9)
                break;
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public int getColumnCount() {
        if(state == null)
            return 0;

        return Mth.clamp(Mth.ceil(state.getAllowedValues().size() / 3F), 0, 3);
    }

    @Override
    public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
        if(state == null)
            return false;

        if (hoveredState != null) {
            this.playDownSound(Minecraft.getInstance().getSoundManager());
            onClick(p_mouseClicked_1_, p_mouseClicked_3_);
            return true;
        }

        return false;
    }

    @Override
    public void onClick(double p_onClick_1_, double p_onClick_3_) {
        if(state == null)
            return;

        super.onClick(p_onClick_1_, p_onClick_3_);
        PacketHandler.send(PacketDistributor.SERVER.noArg(), new UpdateDisguiseStatePacket(te.getBlockPos(), hoveredState));
        te.setDisguiseState(hoveredState);
    }

    private static boolean intersects(int x, int y, int mx, int my) {
        return mx > x && my > y && mx < x + 8 && my < y + 8;
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {
        defaultButtonNarrationText(narrationElementOutput);
    }
}
