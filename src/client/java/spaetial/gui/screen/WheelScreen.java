package spaetial.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Quaternionf;
import spaetial.ClientConfig;
import spaetial.Spaetial;
import spaetial.util.hud.AnimationUtil;
import spaetial.util.sound.MusicUtil;
import spaetial.util.sound.SoundUtil;

import java.util.Arrays;

// TODO extends NavigableScreen
public abstract class WheelScreen extends Screen {
    private static final Identifier[] WHEEL_TEXTURES = new Identifier[] {
            Spaetial.id("textures/gui/selection_wheel/selection_wheel_1.png"),
            Spaetial.id("textures/gui/selection_wheel/selection_wheel_2.png"),
            Spaetial.id("textures/gui/selection_wheel/selection_wheel_3.png"),
            Spaetial.id("textures/gui/selection_wheel/selection_wheel_4.png"),
            Spaetial.id("textures/gui/selection_wheel/selection_wheel_5.png"),
            Spaetial.id("textures/gui/selection_wheel/selection_wheel_6.png"),
            Spaetial.id("textures/gui/selection_wheel/selection_wheel_7.png"),
            Spaetial.id("textures/gui/selection_wheel/selection_wheel_8.png"),
            Spaetial.id("textures/gui/selection_wheel/selection_wheel_9.png"),
            Spaetial.id("textures/gui/selection_wheel/selection_wheel_10.png"),
            Spaetial.id("textures/gui/selection_wheel/selection_wheel_11.png"),
            Spaetial.id("textures/gui/selection_wheel/selection_wheel_12.png"),
            Spaetial.id("textures/gui/selection_wheel/selection_wheel_13.png"),
            Spaetial.id("textures/gui/selection_wheel/selection_wheel_14.png"),
            Spaetial.id("textures/gui/selection_wheel/selection_wheel_15.png"),
            Spaetial.id("textures/gui/selection_wheel/selection_wheel_16.png"),
    };

    private static final Identifier[] WHEEL_TEXTURES_HOVER = new Identifier[] {
            Spaetial.id("textures/gui/selection_wheel/selection_wheel_1_hover.png"),
            Spaetial.id("textures/gui/selection_wheel/selection_wheel_2_hover.png"),
            Spaetial.id("textures/gui/selection_wheel/selection_wheel_3_hover.png"),
            Spaetial.id("textures/gui/selection_wheel/selection_wheel_4_hover.png"),
            Spaetial.id("textures/gui/selection_wheel/selection_wheel_5_hover.png"),
            Spaetial.id("textures/gui/selection_wheel/selection_wheel_6_hover.png"),
            Spaetial.id("textures/gui/selection_wheel/selection_wheel_7_hover.png"),
            Spaetial.id("textures/gui/selection_wheel/selection_wheel_8_hover.png"),
            Spaetial.id("textures/gui/selection_wheel/selection_wheel_9_hover.png"),
            Spaetial.id("textures/gui/selection_wheel/selection_wheel_10_hover.png"),
            Spaetial.id("textures/gui/selection_wheel/selection_wheel_11_hover.png"),
            Spaetial.id("textures/gui/selection_wheel/selection_wheel_12_hover.png"),
            Spaetial.id("textures/gui/selection_wheel/selection_wheel_13_hover.png"),
            Spaetial.id("textures/gui/selection_wheel/selection_wheel_14_hover.png"),
            Spaetial.id("textures/gui/selection_wheel/selection_wheel_15_hover.png"),
            Spaetial.id("textures/gui/selection_wheel/selection_wheel_16_hover.png"),
    };

    private static final int MAX_WHEEL_SIZE = 16;
    static {
        assert WHEEL_TEXTURES.length == MAX_WHEEL_SIZE && WHEEL_TEXTURES_HOVER.length == MAX_WHEEL_SIZE;
    }

    private int animation = 0;
    private int hoveredElement = -1;
    private int mouseX = 0;
    private int mouseY = 0;
    private int tabPressDuration = 0;
    private boolean tabMode = false;

    private final int elements;
    private final Identifier[] elementTextures;
    private final int[] elementTextureSizes;

    private static final int WHEEL_SIZE = 300;
    private static final int OUTER_RADIUS = WHEEL_SIZE / 2;
    private static final int INNER_RADIUS = (int) (OUTER_RADIUS * (10d / 16));
    private static final int ELEMENT_RADIUS = (INNER_RADIUS + OUTER_RADIUS) / 2;

    private static final double ANIMATION_SCALE = .01;
    private static final double ANIMATION_ELEMENT_SCALE = .25;
    private static final double ANIMATION_DURATION = 6;

    private static final int TAB_HOLD_DELAY = 6;
    private static final int TAB_HOLD_SPEED = 2;

    public static final Identifier HOVER_SOUND_ID = Spaetial.id("gui_hover");
    public static final SoundEvent HOVER_SOUND = SoundEvent.of(HOVER_SOUND_ID);

    public WheelScreen(Text title, Identifier[] elements, int elementTextureSize) {
        super(title);
        assert elements.length > 0 && elements.length <= MAX_WHEEL_SIZE;
        this.elementTextures = elements;
        this.elements = elements.length;
        this.elementTextureSizes = new int[elements.length];
        Arrays.fill(this.elementTextureSizes, elementTextureSize);
    }

    private void click(MinecraftClient client, int element) {
        if (element >= 0) {
            onClick(client, element, Screen.hasControlDown(), Screen.hasShiftDown(), Screen.hasAltDown());
        }
        client.setScreen(null);
    }

    protected abstract void onClick(MinecraftClient client, int element, boolean ctrl, boolean shift, boolean alt);

    private void playHoverSound(MinecraftClient client, int selected) {
        SoundUtil.playSoundClient(client, HOVER_SOUND, SoundCategory.PLAYERS, .07f, (float) MusicUtil.pitchMajorScale(.6, selected));
    }

    @Override
    protected void init() {
        this.addDrawableChild(new WheelScreen.Button(0, 0, width, height, NarratorManager.EMPTY, button -> {
            click(client, hoveredElement);
        }));
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void tick() {
        if (client == null) return;

        if (InputUtil.isKeyPressed(client.getWindow().getHandle(), InputUtil.GLFW_KEY_ENTER)) {
            click(client, hoveredElement);
            return;
        } else if (client.options.useKey.isPressed()) {
            click(client, -1);
            return;
        }

        int x = (int) (client.mouse.getX() / client.options.getGuiScale().getValue()) - width / 2;
        int y = (int) (client.mouse.getY() / client.options.getGuiScale().getValue()) - height / 2;
        boolean tab = InputUtil.isKeyPressed(client.getWindow().getHandle(), InputUtil.GLFW_KEY_TAB);

        boolean mouseMoved = x != mouseX || y != mouseY;

        if (tab) tabMode = true;

        double radius_sq = x*x + y*y;
        if (radius_sq >= 1/*INNER_RADIUS*INNER_RADIUS*/ && radius_sq <= OUTER_RADIUS*OUTER_RADIUS && (!tabMode || mouseMoved)) {
            double angle = Math.atan2(y, x) + Math.PI * .5;
            if (elements == 2) angle += Math.PI * .5;
            int selected = (int) (Math.round(angle / (Math.PI * 2 / elements)) + 2 * elements) % elements;

            if (hoveredElement == selected) {
                animation++;
            } else {
                animation = 0;
                playHoverSound(client, selected);
            }
            hoveredElement = selected;
            tabMode = false;
        } else if (tab && (tabPressDuration == 0 || (tabPressDuration >= TAB_HOLD_DELAY && (tabPressDuration - TAB_HOLD_DELAY) % TAB_HOLD_SPEED == 0))) {
            hoveredElement = hoveredElement == -1 ? 0 : (hoveredElement + (Screen.hasControlDown() || Screen.hasShiftDown() ? -1 : 1) + elements) % elements;
            playHoverSound(client, hoveredElement);
        } else if (!tabMode) {
            hoveredElement = -1;
        }
        mouseX = x;
        mouseY = y;
        tabPressDuration = tab ? tabPressDuration + 1 : 0;
    }

    @Override
    public void render(DrawContext context, int mx, int my, float delta) {
        // TODO dynamic scale so the menu renders correctly even when the window is smaller

        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1, 1, 1, 1);

        MatrixStack matrices = context.getMatrices();
        matrices.push();
        matrices.translate(width * .5, height * .5, 0);

        for (int i = 0; i < elements; i++) {
            if (i != hoveredElement) {
                // background
                double scale = 1;

                double wheelSize = WHEEL_SIZE * scale;
                int intWheelSize = (int) wheelSize;
                double wheelOffset = ELEMENT_RADIUS * (1 - scale);

                float rotation = (float) (Math.PI * 2 * i / elements);
                if (elements == 2) rotation -= Math.PI * .5;
                matrices.multiply(new Quaternionf().rotateZ(rotation));

                context.drawTexture(WHEEL_TEXTURES[elements - 1], (int) (-wheelSize * .5), (int) (-wheelSize * .5 - wheelOffset), 0, 0, intWheelSize, intWheelSize, intWheelSize, intWheelSize);

                matrices.multiply(new Quaternionf().rotateZ(-rotation));

                // element
                double elementScale = 1;
                double angle = -Math.PI * .5 + Math.PI * 2 * i / elements;
                if (elements == 2) angle -= Math.PI * .5;

                int textureSize = (int) (elementTextureSizes[i] * elementScale);
                double elementRadius = ELEMENT_RADIUS;
                int elementX = (int) (elementRadius * Math.cos(angle));
                int elementY = (int) (elementRadius * Math.sin(angle));

                context.drawTexture(elementTextures[i], elementX - textureSize / 2, elementY - textureSize / 2, 0, 0, textureSize, textureSize, textureSize, textureSize);
            }
        }

        // hovered element
        if (hoveredElement >= 0) {
            // background
            double scale = elements < 3 ? 1 : 1 + elements * ANIMATION_SCALE * AnimationUtil.easeOut(animation / ANIMATION_DURATION);

            double wheelSize = WHEEL_SIZE * scale;
            int intWheelSize = (int) wheelSize;
            double wheelOffset = ELEMENT_RADIUS * (1 - scale);

            float rotation = (float) (Math.PI * 2 * hoveredElement / elements);
            if (elements == 2) rotation -= Math.PI * .5;
            matrices.multiply(new Quaternionf().rotateZ(rotation));

            RenderSystem.setShaderColor(ClientConfig.Persistent.getPrimaryColor().getRed() * 2 / 255f, ClientConfig.Persistent.getPrimaryColor().getGreen() * 2 / 255f, ClientConfig.Persistent.getPrimaryColor().getBlue() * 2 / 255f, 1);
            context.drawTexture(WHEEL_TEXTURES_HOVER[elements - 1], (int) (-wheelSize * .5), (int) (-wheelSize * .5 - wheelOffset), 0, 0, intWheelSize, intWheelSize, intWheelSize, intWheelSize);
            RenderSystem.setShaderColor(1, 1, 1, 1);

            matrices.multiply(new Quaternionf().rotateZ(-rotation));

            // element
            double elementScale = 1 + ANIMATION_ELEMENT_SCALE;
            double angle = -Math.PI * .5 + Math.PI * 2 * hoveredElement / elements;
            if (elements == 2) angle -= Math.PI * .5;

            int textureSize = (int) (elementTextureSizes[hoveredElement] * elementScale);
            double elementRadius = ELEMENT_RADIUS;
            int elementX = (int) (elementRadius * Math.cos(angle));
            int elementY = (int) (elementRadius * Math.sin(angle));

            context.drawTexture(elementTextures[hoveredElement], elementX - textureSize / 2, elementY - textureSize / 2, 0, 0, textureSize, textureSize, textureSize, textureSize);
        }

        matrices.pop();
    }

    private static class Button extends ButtonWidget {
        protected Button(int x, int y, int width, int height, Text message, PressAction onPress) {
            super(x, y, width, height, message, onPress, DEFAULT_NARRATION_SUPPLIER);
        }
    }
}
