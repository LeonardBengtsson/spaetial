package spaetial.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import spaetial.Spaetial;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Deprecated
public class DialogScreen extends NavigableScreen {
    private final int screenMinWidth;
    private final boolean drawBorder;
    private final boolean textShadow;
    private final Text[][] textTable;
    private final Text[] textLines;
    private final Button[] leftButtons;
    private final Button[] rightButtons;
    private final Runnable onCancel;

    private int guiTotalWidth;
    private int guiTotalHeight;
    private int titleSectionHeight;
    private List<Integer> tableColumnWidths = new ArrayList<>();
    private List<OrderedText> wrappedLines = new ArrayList<>();
    private int[] leftButtonsWidths;
    private int[] rightButtonsWidths;
    private int linesY;
    private int buttonsY;
    private int rightButtonsX;
    private boolean canceled = true;

    private static final int SPACING = 6;
    private static final int TEXT_VERTICAL_SPACING = 2;
    private static final int LEFT_AND_RIGHT_BUTTONS_HORIZONTAL_SPACING = 24;
    private static final int BUTTON_HORIZONTAL_INNER_PADDING = 8;
    private static final int BUTTON_HEIGHT = 15;

    public DialogScreen(int screenMinWidth, boolean drawBorder, boolean textShadow, int defaultButton, @Nullable Runnable onCancel, Text title, @Nullable Text[][] textTable, @Nullable Text[] textLines, @Nullable Button[] leftButtons, @Nullable Button[] rightButtons) {
        super(title,
            (leftButtons == null ? 0 : leftButtons.length) +
            (rightButtons == null ? 0 : rightButtons.length),
            defaultButton
        );
        this.screenMinWidth = screenMinWidth;
        this.drawBorder = drawBorder;
        this.textShadow = textShadow;
        this.onCancel = Objects.requireNonNullElse(onCancel, () -> {});
        this.textTable = Objects.requireNonNullElseGet(textTable, () -> new Text[0][]);
        this.textLines = Objects.requireNonNullElseGet(textLines, () -> new Text[0]);
        this.leftButtons = Objects.requireNonNullElseGet(leftButtons, () -> new Button[0]);
        this.rightButtons = Objects.requireNonNullElseGet(rightButtons, () -> new Button[0]);
    }

    @Override
    protected void init() {
        assert client != null;
        this.textRenderer = client.textRenderer;

        // width
        {
            int titleWidth = textRenderer.getWidth(title);

            tableColumnWidths = new ArrayList<>();
            for (Text[] row : textTable) {
                for (int i = 0; i < row.length; i++) {
                    if (i <= tableColumnWidths.size()) tableColumnWidths.add(0);
                    int width = textRenderer.getWidth(row[i]);
                    Integer max = tableColumnWidths.get(i);
                    if (max == null || width > max) {
                        tableColumnWidths.add(i, width);
                    }
                }
            }
            int tableWidth = tableColumnWidths.stream().reduce(0, Integer::sum);

            int totalButtonsWidth
                = SPACING * (leftButtons.length + rightButtons.length)
                + (leftButtons.length > 0 && rightButtons.length > 0 ? LEFT_AND_RIGHT_BUTTONS_HORIZONTAL_SPACING : SPACING);

            leftButtonsWidths = new int[leftButtons.length];
            for (int i = 0; i < leftButtons.length; i++) {
                int w = 2 * BUTTON_HORIZONTAL_INNER_PADDING + textRenderer.getWidth(leftButtons[i].text);
                leftButtonsWidths[i] = w;
                totalButtonsWidth += w;
            }

            int rightAlignedButtonsTotalWidth = SPACING * rightButtons.length;
            rightButtonsWidths = new int[rightButtons.length];
            for (int i = 0; i < rightButtons.length; i++) {
                int w = 2 * BUTTON_HORIZONTAL_INNER_PADDING + textRenderer.getWidth(rightButtons[i].text);
                rightButtonsWidths[i] = w;
                totalButtonsWidth += w;
                rightAlignedButtonsTotalWidth += w;
            }

            this.guiTotalWidth = Math.max(
                Math.max(
                    2 * SPACING + titleWidth,
                    2 * SPACING + tableWidth
                ),
                Math.max(
                    totalButtonsWidth,
                    screenMinWidth
                )
            );
            this.rightButtonsX = this.guiTotalWidth - rightAlignedButtonsTotalWidth;
        }

        // height
        {
            this.titleSectionHeight = textRenderer.fontHeight + 2 * SPACING;

            int tableHeight
                = textTable.length * textRenderer.fontHeight
                + (textTable.length > 0
                    ? TEXT_VERTICAL_SPACING * (textTable.length - 1) + SPACING
                    : 0
            );

            wrappedLines = new ArrayList<>();
            for (var contentLine : textLines) {
                wrappedLines.addAll(textRenderer.wrapLines(contentLine, this.guiTotalWidth - 2 * SPACING));
            }

            int textHeight
                = wrappedLines.size() * textRenderer.fontHeight
                + (wrappedLines.size() > 0
                    ? TEXT_VERTICAL_SPACING * (wrappedLines.size() - 1) + SPACING
                    : 0
                );

            int buttonsHeight
                = leftButtons.length > 0 || rightButtons.length > 0
                    ? BUTTON_HEIGHT + SPACING
                    : 0;

            this.linesY = titleSectionHeight + tableHeight + SPACING;
            this.buttonsY = titleSectionHeight + tableHeight + textHeight + SPACING;
            this.guiTotalHeight = titleSectionHeight + tableHeight + textHeight + buttonsHeight + SPACING;
        }
    }

    @Override
    public void removed() {
        if (canceled) onCancel.run();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    protected float pitchSound(int element, int elementCount) { return (float) ((Math.random() + .5) * .4); }

    @Override
    protected int getHoveredElement(int mouseX, int mouseY) {
        int localY = mouseY + guiTotalHeight / 2;
        if (localY < buttonsY || localY > buttonsY + BUTTON_HEIGHT) return -1;

        int localX = mouseX + guiTotalWidth / 2;

        int leftX = SPACING;
        for (int i = 0; i < leftButtons.length; i++) {
            int w = leftButtonsWidths[i];
            if (localX >= leftX && localX <= leftX + w) return i;
            leftX += w + SPACING;
        }
        int rightX = rightButtonsX;
        for (int i = 0; i < rightButtons.length; i++) {
            int w = rightButtonsWidths[i];
            if (localX >= rightX && localX <= rightX + w) return leftButtons.length + i;
            rightX += w + SPACING;
        }
        return -1;
    }

    @Override
    protected void onClick(MinecraftClient client, int element) {
        assert element >= 0 && element < leftButtons.length + rightButtons.length;
        Button button;
        if (element >= leftButtons.length) {
            button = rightButtons[element - leftButtons.length];
        } else {
            button = leftButtons[element];
        }
        if (!button.isCancelButton) canceled = false;
        button.click();
        canceled = true;
    }

    @Override
    protected boolean shouldCloseOnClick(int mouseX, int mouseY) {
        return mouseX < -guiTotalWidth / 2
            || mouseX > guiTotalWidth / 2
            || mouseY < -guiTotalHeight / 2
            || mouseY > guiTotalHeight / 2;
    }

    @Override
    public void render(DrawContext context, int mx, int my, float delta) {
        if (client == null || client.options == null || client.world == null) return;

        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1, 1, 1, 1);

        MatrixStack matrices = context.getMatrices();
        matrices.push();
        matrices.translate((width - guiTotalWidth) * .5, (height - guiTotalHeight) * .5, 0);

        // background
        context.fill(0, 0, guiTotalWidth, titleSectionHeight, new Color(32, 32, 32, 191).getRGB());
        context.fill(0, titleSectionHeight, guiTotalWidth, guiTotalHeight, new Color(32, 32, 32, 127).getRGB());

        // title
        context.drawText(textRenderer, title, SPACING, 1 + SPACING, Color.WHITE.getRGB(), false);

        // table
        for (int i = 0; i < textTable.length; i++) {
            Text[] row = textTable[i];
            int x = SPACING;
            int y = 1 + titleSectionHeight + SPACING + i * (textRenderer.fontHeight + TEXT_VERTICAL_SPACING);
            for (int j = 0; j < row.length; j++) {
                context.drawText(textRenderer, row[j], x, y, Color.WHITE.getRGB(), textShadow);
                x += tableColumnWidths.get(j);
            }
        }

        // lines
        for (int i = 0; i < wrappedLines.size(); i++) {
            int y = 1 + linesY + i * (textRenderer.fontHeight + TEXT_VERTICAL_SPACING);
            context.drawText(textRenderer, wrappedLines.get(i), SPACING, y, Color.WHITE.getRGB(), textShadow);
        }

        // left aligned buttons
        int leftX = SPACING;
        for (int i = 0; i < leftButtons.length; i++) {
            Button.State state = selectedElement == i
                ? pressing ? Button.State.PRESSED : Button.State.HOVERED
                : Button.State.NORMAL;
            int w = leftButtonsWidths[i];
            leftButtons[i].draw(
                context, textRenderer, leftX, buttonsY, w, BUTTON_HEIGHT,
                BUTTON_HORIZONTAL_INNER_PADDING, 1 + (BUTTON_HEIGHT - textRenderer.fontHeight) / 2,
                Color.WHITE, textShadow, state
            );
            leftX += w + SPACING;
        }

        // right aligned buttons
        int rightX = rightButtonsX;
        for (int i = 0; i < rightButtons.length; i++) {
            Button.State state = selectedElement - leftButtons.length == i
                ? pressing ? Button.State.PRESSED : Button.State.HOVERED
                : Button.State.NORMAL;
            int w = rightButtonsWidths[i];
            rightButtons[i].draw(
                context, textRenderer, rightX, buttonsY, w, BUTTON_HEIGHT,
                BUTTON_HORIZONTAL_INNER_PADDING, 1 + (BUTTON_HEIGHT - textRenderer.fontHeight) / 2,
                Color.WHITE, textShadow, state
            );
            rightX += w + SPACING;
        }

        // border
        if (drawBorder) {
            context.drawBorder(0, 0, guiTotalWidth, guiTotalHeight, new Color(32, 32, 32).getRGB());
        }

        // clean up
        matrices.pop();
    }
}
