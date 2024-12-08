package spaetial.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import spaetial.Spaetial;
import spaetial.input.ModInput;
import spaetial.util.sound.SoundUtil;

public abstract class NavigableScreen extends Screen {
    private final int elementCount;
    private final int defaultElement;

    protected int selectedElement = -1;

    private int mouseX = 0;
    private int mouseY = 0;
    protected boolean pressing = false;
    private int tabPressDuration = 0;
    private boolean tabMode = false;
    private boolean untouched = true;


    private static final int TAB_HOLD_DELAY = 6;
    private static final int TAB_HOLD_SPEED = 2;

    public static final SoundEvent HOVER_SOUND = SoundEvent.of(Spaetial.id("gui_hover"));
    public static final SoundEvent PRESS_SOUND = SoundEvent.of(Spaetial.id("gui_press")); // TODO
    public static final SoundEvent CLICK_SOUND = SoundEvent.of(Spaetial.id("gui_click")); // TODO

    protected NavigableScreen(Text title, int elementCount, int defaultButton) {
        super(title);
        this.elementCount = elementCount;
        this.defaultElement = defaultButton;
    }

    protected abstract float pitchSound(int element, int elementCount);
    protected abstract int getHoveredElement(int mouseX, int mouseY);
    protected abstract void onClick(MinecraftClient client, int element);
    protected abstract boolean shouldCloseOnClick(int mouseX, int mouseY);

    @Override
    public void tick() {
        if (client == null) return;

        int x = (int) (client.mouse.getX() / client.options.getGuiScale().getValue()) - width / 2;
        int y = (int) (client.mouse.getY() / client.options.getGuiScale().getValue()) - height / 2;

        boolean mouseMoved = x != mouseX || y != mouseY;

        boolean tab = InputUtil.isKeyPressed(client.getWindow().getHandle(), InputUtil.GLFW_KEY_TAB);
        if (tab) tabMode = true;

        int hoveredElement = getHoveredElement(x, y);
        if (mouseMoved && hoveredElement != selectedElement && (hoveredElement >= 0 || !tabMode)) {
            if (hoveredElement >= 0) {
                playHoverSound(client);
                untouched = false;
            }
            if (hoveredElement >= 0 || !untouched) {
                selectedElement = hoveredElement;
                tabMode = false;
            }
        } else if (tab) {
            untouched = false;
            boolean tabHoldTiming = (tabPressDuration - TAB_HOLD_DELAY) % TAB_HOLD_SPEED == 0;
            boolean tabHoldActivation = tabPressDuration >= TAB_HOLD_DELAY && tabHoldTiming;
            if (tabPressDuration == 0 || tabHoldActivation) {
                if (selectedElement == -1) {
                    selectedElement = 0;
                } else {
                    selectedElement += (Screen.hasControlDown() || Screen.hasShiftDown() ? -1 : 1);
                    selectedElement = (selectedElement + elementCount) % elementCount;
                }
                playHoverSound(client);
            }
        }

        boolean lmb = ModInput.lmb();
        boolean lmbStart = lmb && !pressing;
        boolean lmbEnd = !lmb && pressing;
        boolean rmb = ModInput.rmb();
        boolean enter = InputUtil.isKeyPressed(client.getWindow().getHandle(), InputUtil.GLFW_KEY_ENTER);

        if (enter) {
            if (untouched) {
                onClick(client, defaultElement);
            } else {
                onClick(client, selectedElement);
            }
            playClickSound(client);
        } else if (rmb) {
            client.setScreen(null);
            return;
        } else if (lmbEnd) {
            if (hoveredElement >= 0 && !untouched) {
                onClick(client, hoveredElement);
                playClickSound(client);
            } else if (shouldCloseOnClick(x, y)) {
                client.setScreen(null);
                playClickSound(client);
            }
        } else if (lmbStart) {
            playPressSound(client);
        }

        mouseX = x;
        mouseY = y;
        pressing = lmb;
        tabPressDuration = tab ? tabPressDuration + 1 : 0;
    }

    private void playHoverSound(MinecraftClient client) {
        SoundUtil.playSoundClient(client, HOVER_SOUND, SoundCategory.PLAYERS, .07f, pitchSound(selectedElement, elementCount));
    }

    private void playPressSound(MinecraftClient client) {
        SoundUtil.playSoundClient(client, PRESS_SOUND, SoundCategory.PLAYERS, .07f, pitchSound(selectedElement, elementCount));
    }

    private void playClickSound(MinecraftClient client) {
        SoundUtil.playSoundClient(client, CLICK_SOUND, SoundCategory.PLAYERS, .07f, pitchSound(selectedElement, elementCount));
    }
}
