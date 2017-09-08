package slimeknights.tmechworks.client.gui.components;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiUtils;
import org.lwjgl.util.vector.Vector2f;
import slimeknights.tmechworks.library.Util;

import java.util.Arrays;

public class GuiArrowSelection extends GuiButton {

    public static String[] LABELSET_DEFAULT = {"Down", "Up", "Front", "Back", "Left", "Right", "Place High", "Place Middle", "Place Low"};

    private static final int ARROW_SIZE = 20;
    private static final int ARROW_SMALL_SIZE = 6;
    private static final int ARROW_ROWS = 2;

    private static final ResourceLocation textures = Util.getResource("textures/gui/arrows.png");

    private String[] labelset;
    private ArrowState[] states = new ArrowState[Arrow.values().length];
    private Arrow hoveredArrow;

    private int screenW, screenH;

    public GuiArrowSelection(int buttonId, int x, int y, int screenW, int screenH) {
        this(buttonId, x, y, screenW, screenH, false);
    }

    public GuiArrowSelection(int buttonId, int x, int y, int screenW, int screenH, boolean drawAdditionalArrows) {
        super(buttonId, x, y, "");

        setLabelSet(LABELSET_DEFAULT);
        Arrays.fill(states, ArrowState.ENABLED);

        if (!drawAdditionalArrows) {
            for (int i = Arrow.MID_UP.ordinal(); i < Arrow.values().length; i++) {
                states[i] = ArrowState.NO_DRAW;
            }
        }

        this.screenW = screenW;
        this.screenH = screenH;
    }

    public GuiArrowSelection setLabelSet(String[] labels) {
        if (labels.length < Arrow.values().length) {
            labelset = new String[Arrow.values().length];
            Arrays.fill(labelset, "");

            for (int i = 0; i < labelset.length; i++) {
                if (i < labels.length)
                    labelset[i] = labels[i];
            }
        } else {
            this.labelset = labels;
        }

        return this;
    }

    public ArrowState getState(Arrow arrow) {
        return states[arrow.ordinal()];
    }

    public GuiArrowSelection setState(Arrow arrow, ArrowState state) {
        states[arrow.ordinal()] = state;

        return this;
    }

    public Arrow getHoveredArrow() {
        return hoveredArrow;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (!visible)
            return;

        Arrow.SMALL_DOWN.position.y = ARROW_SIZE + 8;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        mc.getTextureManager().bindTexture(textures);

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

            drawTexturedModalRect(arrow.position.getX(), arrow.position.getY(), indexX, indexY, arrow.subW, arrow.subH);
        }

        GlStateManager.popMatrix();
    }

    @Override
    public void drawButtonForegroundLayer(int mouseX, int mouseY) {
        if (hoveredArrow == null)
            return;

        if (labelset != null && states[hoveredArrow.ordinal()] == ArrowState.HOVER && !labelset[hoveredArrow.ordinal()].trim().isEmpty()) {
            GuiUtils.drawHoveringText(ImmutableList.of(labelset[hoveredArrow.ordinal()]), mouseX, mouseY, screenW, screenH, 100, Minecraft.getMinecraft().fontRenderer);
        }
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        return hoveredArrow != null;
    }

    public enum Arrow {
        MID_DOWN(2, 0, 0, 0, new Vector2f(24 - ARROW_SIZE / 2 - 1, 48 - ARROW_SIZE + 1)),
        MID_UP(2, 0, 0, 0, new Vector2f(24 - ARROW_SIZE / 2 - 1, ARROW_SIZE - ARROW_SMALL_SIZE - 1)),
        UP(0, 0, new Vector2f(24 - ARROW_SIZE / 2, 0)),
        DOWN(1, 0, new Vector2f(24 - ARROW_SIZE / 2, 48 - ARROW_SIZE)),
        LEFT(0, 1, new Vector2f(0, 24 - ARROW_SIZE / 2)),
        RIGHT(1, 1, new Vector2f(48 - ARROW_SIZE, 24 - ARROW_SIZE / 2)),
        SMALL_UP(2, 0, 1, 0, new Vector2f(50, ARROW_SIZE - 5)),
        SMALL_MID(2, 0, 0, 1, new Vector2f(50, ARROW_SIZE + 1)),
        SMALL_DOWN(2, 0, 1, 1, new Vector2f(50, 48 - ARROW_SIZE + 1));

        public final int indexX, indexY, w, h, subX, subY, subW, subH;
        public final Vector2f position;

        Arrow(int indexX, int indexY, Vector2f pos) {
            this(indexX, indexY, ARROW_SIZE, ARROW_SIZE, 0, 0, ARROW_SIZE, ARROW_SIZE, pos);
        }

        Arrow(int indexX, int indexY, int subX, int subY, Vector2f pos) {
            this(indexX, indexY, ARROW_SIZE, ARROW_SIZE, subX, subY, ARROW_SMALL_SIZE, ARROW_SMALL_SIZE, pos);
        }

        Arrow(int indexX, int indexY, int w, int h, int subX, int subY, int subW, int subH, Vector2f pos) {
            this.indexX = indexX;
            this.indexY = indexY;
            this.w = w;
            this.h = h;
            this.subX = subX;
            this.subY = subY;
            this.subW = subW;
            this.subH = subH;
            this.position = pos;
        }

        public boolean isHovered(int xPosition, int yPosition, int mouseX, int mouseY) {
            return mouseX > xPosition + position.getX() && mouseX < xPosition + position.getX() + subW && mouseY > yPosition + position.getY() && mouseY < yPosition + position.getY() + subH;
        }
    }

    public enum ArrowState {
        ENABLED,
        DISABLED,
        HOVER,
        SELECTED,
        NO_DRAW
    }
}
