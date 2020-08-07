package slimeknights.tmechworks.client.gui.components;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import slimeknights.tmechworks.TMechworks;

import javax.annotation.Nonnull;
import java.util.Arrays;

public class ArrowWidget extends Widget {
    public static final ResourceLocation ARROW_WIDGET = new ResourceLocation(TMechworks.modId, "textures/gui/arrows.png");

    public static final String[] LABELS_DEFAULT = {
            "tmechworks:widget.arrow.down",
            "tmechworks:widget.arrow.up",
            "tmechworks:widget.arrow.front",
            "tmechworks:widget.arrow.back",
            "tmechworks:widget.arrow.left",
            "tmechworks:widget.arrow.right",
            "tmechworks:widget.arrow.high",
            "tmechworks:widget.arrow.middle",
            "tmechworks:widget.arrow.low"
    };
    private static final int ARROW_SIZE = 20;
    private static final int ARROW_SMALL_SIZE = 6;
    private static final int ARROW_ROWS = 2;

    private String[] labels;
    private ArrowState[] states = new ArrowState[Arrow.values().length];
    private Arrow hoveredArrow;
    private int screenW, screenH;
    private IArrowPressed onClick;

    public ArrowWidget(int x, int y, int screenW, int screenH, IArrowPressed onClick) {
        this(x, y, screenW, screenH, false, onClick);
    }

    public ArrowWidget(int x, int y, int screenW, int screenH, boolean drawAdditionalArrows, IArrowPressed onClick) {
        super(x, y, 0, 0, new StringTextComponent(""));

        setLabels(LABELS_DEFAULT);
        Arrays.fill(states, ArrowState.ENABLED);

        if (!drawAdditionalArrows) {
            for (int i = Arrow.MID_UP.ordinal(); i < Arrow.values().length; i++) {
                states[i] = ArrowState.NO_DRAW;
            }
        }

        this.screenW = screenW;
        this.screenH = screenH;
        this.onClick = onClick;
    }

    public ArrowWidget setLabels(String[] labels) {
        if (labels.length < Arrow.values().length) {
            labels = new String[Arrow.values().length];
            Arrays.fill(this.labels, "");

            for (int i = 0; i < this.labels.length; i++) {
                if (i < labels.length)
                    this.labels[i] = labels[i];
            }
        } else {
            this.labels = labels;
        }

        return this;
    }

    public ArrowState getState(Arrow arrow) {
        return states[arrow.ordinal()];
    }

    public ArrowWidget setState(Arrow arrow, ArrowState state) {
        states[arrow.ordinal()] = state;

        return this;
    }

    public Arrow getHoveredArrow() {
        return hoveredArrow;
    }

    @Override
    public void renderButton(@Nonnull MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();

        RenderSystem.pushMatrix();
        RenderSystem.translatef(x, y, 0F);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        mc.getTextureManager().bindTexture(ARROW_WIDGET);

        hoveredArrow = null;
        boolean canHover = true;
        for (int i = 0; i < Arrow.values().length; i++) {
            Arrow arrow = Arrow.values()[i];
            ArrowState state = states[i];

            if (state == ArrowState.NO_DRAW)
                continue;

            if (state != ArrowState.DISABLED && state != ArrowState.SELECTED) {
                if (canHover && hoveredArrow == null && arrow.isHovered(x, y, mouseX, mouseY)) {
                    states[i] = ArrowState.HOVER;
                    hoveredArrow = arrow;
                    canHover = false;
                } else {
                    states[i] = ArrowState.ENABLED;
                }
            } else if (arrow.isHovered(x, y, mouseX, mouseY)) {
                canHover = false;
            }
        }

        for (int i = 0; i < Arrow.values().length; i++) {
            Arrow arrow = Arrow.values()[i];
            ArrowState state = states[i];

            if (state == ArrowState.NO_DRAW)
                continue;

            int indexX = arrow.indexX * arrow.w + arrow.subX * arrow.subW;
            int indexY = (arrow.indexY + state.ordinal() * ARROW_ROWS) * arrow.h + arrow.subY * arrow.subH;

            blit(stack, arrow.x, arrow.y, indexX, indexY, arrow.subW, arrow.subH);
        }

        RenderSystem.popMatrix();

        if (hoveredArrow == null)
            return;

        // ITextProperties.func_240652_a_ -> create
        if (labels != null && states[hoveredArrow.ordinal()] == ArrowState.HOVER && !labels[hoveredArrow.ordinal()].trim().isEmpty()) {
            GuiUtils.drawHoveringText(stack, ImmutableList.of(ITextProperties.func_240652_a_(I18n.format(labels[hoveredArrow.ordinal()]))), mouseX, mouseY, screenW, screenH, 100, Minecraft.getInstance().fontRenderer);
        }
    }

    @Override
    public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
        if (hoveredArrow != null) {
            this.playDownSound(Minecraft.getInstance().getSoundHandler());
            onClick(p_mouseClicked_1_, p_mouseClicked_3_);
            return true;
        }

        return false;
    }

    @Override
    public void onClick(double p_onClick_1_, double p_onClick_3_) {
        super.onClick(p_onClick_1_, p_onClick_3_);

        onClick.onPress(this, hoveredArrow);
    }

    public enum Arrow {
        MID_DOWN(2, 0, 0, 0, 24 - ARROW_SIZE / 2 - 1, 48 - ARROW_SIZE + 1),
        MID_UP(2, 0, 0, 0, 24 - ARROW_SIZE / 2 - 1, ARROW_SIZE - ARROW_SMALL_SIZE - 1),
        UP(0, 0, 24 - ARROW_SIZE / 2, 0),
        DOWN(1, 0, 24 - ARROW_SIZE / 2, 48 - ARROW_SIZE),
        LEFT(0, 1, 0, 24 - ARROW_SIZE / 2),
        RIGHT(1, 1, 48 - ARROW_SIZE, 24 - ARROW_SIZE / 2),
        SMALL_UP(2, 0, 1, 0, 50, ARROW_SIZE - 5),
        SMALL_MID(2, 0, 0, 1, 50, ARROW_SIZE + 1),
        SMALL_DOWN(2, 0, 1, 1, 50, 48 - ARROW_SIZE + 1);

        public final int indexX, indexY, w, h, subX, subY, subW, subH;
        public final int x;
        public final int y;

        Arrow(int indexX, int indexY, int x, int y) {
            this(indexX, indexY, ARROW_SIZE, ARROW_SIZE, 0, 0, ARROW_SIZE, ARROW_SIZE, x, y);
        }

        Arrow(int indexX, int indexY, int subX, int subY, int x, int y) {
            this(indexX, indexY, ARROW_SIZE, ARROW_SIZE, subX, subY, ARROW_SMALL_SIZE, ARROW_SMALL_SIZE, x, y);
        }

        Arrow(int indexX, int indexY, int w, int h, int subX, int subY, int subW, int subH, int x, int y) {
            this.indexX = indexX;
            this.indexY = indexY;
            this.w = w;
            this.h = h;
            this.subX = subX;
            this.subY = subY;
            this.subW = subW;
            this.subH = subH;
            this.x = x;
            this.y = y;
        }

        public boolean isHovered(int xPosition, int yPosition, int mouseX, int mouseY) {
            return mouseX > xPosition + x && mouseX < xPosition + x + subW && mouseY > yPosition + y && mouseY < yPosition + y + subH;
        }
    }

    public enum ArrowState {
        ENABLED,
        DISABLED,
        HOVER,
        SELECTED,
        NO_DRAW
    }

    public interface IArrowPressed {
        void onPress(ArrowWidget widget, Arrow arrow);
    }
}
