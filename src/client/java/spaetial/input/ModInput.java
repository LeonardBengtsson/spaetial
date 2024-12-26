package spaetial.input;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3i;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import spaetial.ClientEvents;
import spaetial.Spaetial;
import spaetial.editing.ClientManager;
import spaetial.gui.ModScreen;
import spaetial.util.Translatable;

import java.util.*;
import java.util.function.BiFunction;

public enum ModInput implements Translatable {
    //// MOD ////
    TOGGLE_MOD("toggle_mod", Category.COMMON, "key.backslash", KeyModifiers.NONE),
    CONFIG("config", Category.COMMON, "key.e", KeyModifiers.ALT),

    //// COMMON ////
    CONFIRM("confirm", Category.COMMON, "mouse.left", KeyModifiers.NONE),
    CONFIRM_ALTERNATIVE("confirm_alternative", Category.COMMON, "mouse.left", KeyModifiers.ALT),
    CANCEL("cancel", Category.COMMON, "mouse.right", KeyModifiers.NONE),
    UNDO("undo", Category.COMMON, "key.z", KeyModifiers.NONE),
    REDO("redo", Category.COMMON, "key.z", KeyModifiers.ALT),

    //// COMMON OPERATIONS ////
    CUT("cut", Category.OPERATION, "key.x", KeyModifiers.NONE),
    CUT_WITH_AIR("cut_with_air", Category.OPERATION, "key.x", KeyModifiers.ALT),
    COPY("copy", Category.OPERATION, "key.c", KeyModifiers.NONE),
    COPY_WITH_AIR("copy_with_air", Category.OPERATION, "key.c", KeyModifiers.ALT),
    REPLACE("replace", Category.OPERATION, "key.v", KeyModifiers.NONE),
    PASTE("paste", Category.PASTE, "key.v", KeyModifiers.NONE),
    PASTE_SURFACE("paste_surface", Category.PASTE, "key.v", KeyModifiers.CTRL),
    PASTE_ALTERNATIVE("paste_alternative", Category.PASTE, "key.v", KeyModifiers.ALT),
    PASTE_ALTERNATIVE_SURFACE("paste_alternative_surface", Category.PASTE, "key.v", KeyModifiers.CTRL_ALT),

    //// SELECTIONS ////
    MOVE_FORWARDS("move_forwards", Category.SELECTION, "scroll.up", KeyModifiers.CTRL, true, new Vec3i(0, 0, 1)),
    MOVE_BACKWARDS("move_backwards", Category.SELECTION, "scroll.down", KeyModifiers.CTRL, true, new Vec3i(0, 0, -1)),
    MOVE_RIGHT("move_right", Category.SELECTION, "scroll.right", KeyModifiers.CTRL, true, new Vec3i(1, 0, 0)),
    MOVE_LEFT("move_left", Category.SELECTION, "scroll.left", KeyModifiers.CTRL, true, new Vec3i(-1, 0, 0)),
    MOVE_TO_TARGET("move_to_target", Category.SELECTION, "mouse.right", KeyModifiers.ALT),
    MOVE_TO_TARGET_SURFACE("move_to_target_surface", Category.SELECTION, "mouse.right", KeyModifiers.CTRL_ALT),
    MOVE_IN_PLANE("move_in_plane", Category.SELECTION, "key.g", KeyModifiers.NONE),
    RESIZE_FORWARDS("resize_forwards", Category.SELECTION, "scroll.up", KeyModifiers.ALT, true, new Vec3i(0, 0, 1)),
    RESIZE_BACKWARDS("resize_backwards", Category.SELECTION, "scroll.down", KeyModifiers.ALT, true, new Vec3i(0, 0, -1)),
    RESIZE_RIGHT("resize_right", Category.SELECTION, "scroll.right", KeyModifiers.ALT, true, new Vec3i(1, 0, 0)),
    RESIZE_LEFT("resize_left", Category.SELECTION, "scroll.left", KeyModifiers.ALT, true, new Vec3i(-1, 0, 0)),
    RESIZE_FORWARDS_ALTERNATIVE("resize_forwards_alternative", Category.SELECTION, "scroll.up", KeyModifiers.CTRL_ALT, true, new Vec3i(0, 0, 1)),
    RESIZE_BACKWARDS_ALTERNATIVE("resize_backwards_alternative", Category.SELECTION, "scroll.down", KeyModifiers.CTRL_ALT, true, new Vec3i(0, 0, -1)),
    RESIZE_IN_PLANE("resize_in_plane", Category.SELECTION, "key.g", KeyModifiers.ALT),

    //// NORMAL STATE ////
    QUICK_REPLACE("quick_replace", Category.NORMAL, "key.y", KeyModifiers.NONE),
    QUICK_REPLACE_SURFACE("quick_replace_surface", Category.NORMAL, "key.y", KeyModifiers.CTRL),
    QUICK_SET("quick_set", Category.NORMAL, "key.y", KeyModifiers.ALT),
    QUICK_SET_SURFACE("quick_set_surface", Category.NORMAL, "key.y", KeyModifiers.CTRL_ALT),
    CUBOID_SELECTION("cuboid_selection", Category.NORMAL, "mouse.left", KeyModifiers.ALT),
    CUBOID_SELECTION_SURFACE("cuboid_selection_surface", Category.NORMAL, "mouse.left", KeyModifiers.CTRL_ALT),

    //// CUBOID SELECTION STATE ////
    EXTEND_SELECTION("extend_selection", Category.SELECTION, "mouse.left", KeyModifiers.NONE),
    EXTEND_SELECTION_SURFACE("extend_selection_surface", Category.SELECTION, "mouse.left", KeyModifiers.CTRL),

    //// COPY STATE ////
    CYCLE_COPY_MODE("cycle_copy_mode", Category.COPY, "key.tab", KeyModifiers.NONE),
    CLONE_MODE("clone_mode", Category.COPY, "key.1", KeyModifiers.NONE),
    LINE_STACK_MODE("line_stack_mode", Category.COPY, "key.2", KeyModifiers.NONE),
    VOLUME_STACK_MODE("volume_stack_mode", Category.COPY, "key.3", KeyModifiers.NONE),
    CHANGE_STACK_SPACING_IN_PLANE("change_stack_spacing_in_plane", Category.COPY, "key.g", KeyModifiers.CTRL_ALT),
    CHANGE_STACK_SPACING_FORWARDS("change_stack_spacing_forwards", Category.COPY, "scroll.up", KeyModifiers.CTRL_ALT, true, new Vec3i(0, 0, 1)),
    CHANGE_STACK_SPACING_BACKWARDS("change_stack_spacing_backwards", Category.COPY, "scroll.down", KeyModifiers.CTRL_ALT, true, new Vec3i(0, 0, -1)),
    CHANGE_STACK_SPACING_RIGHT("change_stack_spacing_right", Category.COPY, "scroll.right", KeyModifiers.CTRL_ALT, true, new Vec3i(1, 0, 0)),
    CHANGE_STACK_SPACING_LEFT("change_stack_spacing_left", Category.COPY, "scroll.left", KeyModifiers.CTRL_ALT, true, new Vec3i(-1, 0, 0)),
    INCREASE_LINE_STACK_SIZE("increase_stack_size", Category.COPY, "scroll.up", KeyModifiers.ALT, true),
    DECREASE_LINE_STACK_SIZE("decrease_stack_size", Category.COPY, "scroll.down", KeyModifiers.ALT, true),
    CHANGE_VOLUME_STACK_SIZE_IN_PLANE("change_volume_stack_size_in_plane", Category.COPY, "key.g", KeyModifiers.ALT),
    CHANGE_VOLUME_STACK_SIZE_FORWARDS("change_volume_stack_size_forwards", Category.COPY, "scroll.up", KeyModifiers.ALT, true, new Vec3i(0, 0, 1)),
    CHANGE_VOLUME_STACK_SIZE_BACKWARDS("change_volume_stack_size_backwards", Category.COPY, "scroll.down", KeyModifiers.ALT, true, new Vec3i(0, 0, -1)),
    CHANGE_VOLUME_STACK_SIZE_RIGHT("change_volume_stack_size_right", Category.COPY, "scroll.right", KeyModifiers.ALT, true, new Vec3i(1, 0, 0)),
    CHANGE_VOLUME_STACK_SIZE_LEFT("change_volume_stack_size_left", Category.COPY, "scroll.left", KeyModifiers.ALT, true, new Vec3i(-1, 0, 0)),

    //////// DEBUG ////////
    DEBUG_1("debug_1", Category.DEBUG, "key.1", KeyModifiers.CTRL_SHIFT_ALT),
    DEBUG_2("debug_2", Category.DEBUG, "key.2", KeyModifiers.CTRL_SHIFT_ALT),
    DEBUG_3("debug_3", Category.DEBUG, "key.3", KeyModifiers.CTRL_SHIFT_ALT);

    private static final Map<String, ModInput> KEYS_BY_ID = new HashMap<>();
    static {
        for (var binding : ModInput.values()) {
            KEYS_BY_ID.put(binding.name, binding);
        }
    }
    public static @Nullable ModInput getKeyBindingFromId(String id) { return KEYS_BY_ID.get(id); }

    private static boolean shiftIsPressed = false;
    private static boolean ctrlIsPressed = false;
    private static boolean altIsPressed = false;
    private static boolean f3IsPressed = false;

    private static boolean lmbIsPresed = false;
    private static boolean mmbIsPresed = false;
    private static boolean rmbIsPresed = false;

    /**
     * @return Whether either shift keyCode is being pressed
     */
    public static boolean shift() { return shiftIsPressed; }

    /**
     * @return Whether either ctrl keyCode is being pressed
     */
    public static boolean ctrl() { return ctrlIsPressed; }

    /**
     * @return Whether either alt keyCode is being pressed
     */
    public static boolean alt() { return altIsPressed; }

    /**
     * @return Whether the f3 keyCode is being pressed
     */
    public static boolean f3() { return f3IsPressed; }

    /**
     * @return Whether the left mouse button is being pressed
     */
    public static boolean lmb() { return lmbIsPresed; }

    /**
     * @return Whether the middle mouse button is being pressed
     */
    public static boolean mmb() { return mmbIsPresed; }

    /**
     * @return Whether the right mouse button is being pressed
     */
    public static boolean rmb() { return rmbIsPresed; }


    public final String name;
    public final Category category;
    public final boolean repeatable;
    /**
     * A {@link Vec3i} representing the binding's inherent direction. For example, a binding representing a downwards
     * movement would have an inherent down direction. The vector's positive {@code x}, {@code y}, and {@code z} axes
     * represent the {@code right}, {@code up}, and {@code forwards} directions, respectively. Negative values indicate
     * the opposite direction.
     */
    public final @Nullable Vec3i inherentDirection;

    private final KeyInfo defaultBinding;
    private final boolean f3Key;

    private KeyInfo binding;

    ModInput(String name, Category category, String defaultKey, KeyModifiers defaultModifiers) {
        this(name, category, defaultKey, defaultModifiers, false);
    }
    ModInput(String name, Category category, String defaultKey, KeyModifiers defaultModifiers, boolean repeatable) {
        this(name, category, defaultKey, defaultModifiers, repeatable, null);
    }
    ModInput(String name, Category category, String defaultKey, KeyModifiers defaultModifiers, boolean repeatable, @Nullable Vec3i inherentDirection) {
        this(name, category, defaultKey, defaultModifiers, repeatable, inherentDirection, false);
    }
    ModInput(String name, Category category, String defaultKey, KeyModifiers defaultModifiers, boolean repeatable, @Nullable Vec3i inherentDirection, boolean f3Key) {
        this.name = name;
        this.category = category;
        this.defaultBinding = KeyInfo.fromStringAndMods(defaultKey, defaultModifiers.shift, defaultModifiers.ctrl, defaultModifiers.alt);
        this.repeatable = repeatable;
        this.inherentDirection = inherentDirection;
        this.f3Key = f3Key;
        this.setDefault();
    }

    public static boolean handleKeyPress(MinecraftClient client, int keyCode, int scancode, InputAction action, boolean shift, boolean ctrl, boolean alt) {
        if (keyCode == InputUtil.GLFW_KEY_LEFT_SHIFT || keyCode == InputUtil.GLFW_KEY_RIGHT_SHIFT) {
            if (action != InputAction.INVALID) {
                shiftIsPressed = action != InputAction.END;
            }
        } else if (keyCode == InputUtil.GLFW_KEY_LEFT_CONTROL || keyCode == InputUtil.GLFW_KEY_RIGHT_CONTROL) {
            if (action != InputAction.INVALID) {
                ctrlIsPressed = action != InputAction.END;
            }
        } else if (keyCode == InputUtil.GLFW_KEY_LEFT_ALT || keyCode == InputUtil.GLFW_KEY_RIGHT_ALT) {
            if (action != InputAction.INVALID) {
                altIsPressed = action != InputAction.END;
            }
        } else if (keyCode == InputUtil.GLFW_KEY_F3) {
            if (action != InputAction.INVALID) {
                f3IsPressed = action != InputAction.END;
            }
        }
        var keyInfo = new KeyInfo(InputUtil.fromKeyCode(keyCode, scancode), shift, ctrl, alt);
        return handleInput(client, keyInfo, action, 1);
    }

    public static boolean handleMouseInput(MinecraftClient client, int button, InputAction action, boolean shift, boolean ctrl, boolean alt) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            if (action != InputAction.INVALID) {
                lmbIsPresed = action != InputAction.END;
            }
        } else if (button == GLFW.GLFW_MOUSE_BUTTON_MIDDLE) {
            if (action != InputAction.INVALID) {
                mmbIsPresed = action != InputAction.END;
            }
        } else if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            if (action != InputAction.INVALID) {
                rmbIsPresed = action != InputAction.END;
            }
        }
        var keyInfo = new KeyInfo(InputUtil.Type.MOUSE.createFromCode(button), shift, ctrl, alt);
        return handleInput(client, keyInfo, action, 1);
    }

    public static boolean handleScrollWheelInput(MinecraftClient client, double horizontal, double vertical) {
        KeyInfo.ScrollDirection dir;
        int repeats;
        double abs_h = Math.abs(horizontal);
        double abs_v = Math.abs(vertical);
        if (abs_h > abs_v) {
            dir = horizontal > 0 ? KeyInfo.ScrollDirection.LEFT : KeyInfo.ScrollDirection.RIGHT;
            repeats = (int) Math.ceil(abs_h);
        } else {
            dir = vertical > 0 ? KeyInfo.ScrollDirection.UP : KeyInfo.ScrollDirection.DOWN;
            repeats = (int) Math.ceil(abs_v);
        }
        return handleInput(client, KeyInfo.fromScrollAndMods(dir, shiftIsPressed, ctrlIsPressed, altIsPressed), InputAction.BEGIN, repeats);
    }

    private static boolean handleInput(MinecraftClient client, KeyInfo input, InputAction action, int repeats) {
        var player = client.player;
        if (player == null) return false;
        if (tryListener(client, player, input, action, repeats, ClientEvents.INPUT_LISTENER)) return true;
        if (client.currentScreen instanceof ModScreen screen) {
            if (tryListener(client, player, input, action, repeats, screen)) return true;
        } else if (client.currentScreen == null) {
            var state = ClientManager.getEditingState();
            if (state.getInteractionType().processInput) {
                if (tryListener(client, player, input, action, repeats, state)) return true;
            }
        }
        return false;
    }

    private static boolean tryListener(MinecraftClient client, ClientPlayerEntity player, KeyInfo input, InputAction action, int repeats, InputListener listener) {
        // if action is END, pass event by every binding. if action is not end, skip by every binding after the first one
        BiFunction<Boolean, ModInput, Boolean> accumulator = action == InputAction.END
            ? (b, i) -> listener.onInput(client, player, i, action, repeats)
            : (b, i) -> b || listener.onInput(client, player, i, action, repeats);
        return listener
            .getApplicableInputs().stream()
            .map(i -> new Pair<>(i, input.getMatchPriority(i.binding)))
            .filter(pair -> pair.getRight() >= 0 && pair.getLeft().f3Key == f3IsPressed)
            .sorted(Comparator.comparingInt(Pair::getRight))
            .map(Pair::getLeft)
            .reduce(false, accumulator, Boolean::logicalOr);

//        return listener
//            .getApplicableInputs().stream()
//            .filter(pair -> pair.f3Key == f3IsPressed)
//            .sorted(Comparator.comparingInt(pair -> pair.binding.getMatchPriority(input))
//            .reduce(false, acc, Boolean::logicalOr);

//        var values = ModInput.values();
//        for (int pair = values.length - 1; pair >= 0; pair--) {
//            var binding = values[pair];
//            if (binding.isTriggeredBy(input, action) && f3IsPressed == binding.f3Key) {
//                boolean result = ClientEvents.onInput(client, binding, action);
//                if (result) {
//                    return action != InputAction.END;
//                }
//            }
//        }
    }

    /**
     * @param noModsAlwaysOn If {@code true}, a binding set to have no modifiers will always return {@code true}.
     * @return               {@code true} if the specified binding's required modifier keys are currently being pressed,
     *                       or the binding requires no modifiers and any modifier is being pressed
     */
    public boolean isPressingModifierKeys(boolean noModsAlwaysOn) {
        if (!binding.shift() && !binding.ctrl() && !binding.alt() && !this.f3Key) {
            if (noModsAlwaysOn) {
                return true;
            } else {
                return shiftIsPressed || ctrlIsPressed || altIsPressed;
            }
        }
        if (!shiftIsPressed && binding.shift()) return false;
        if (!ctrlIsPressed && binding.ctrl()) return false;
        if (!altIsPressed && binding.alt()) return false;
        return true;
    }

    public void setDefault() {
        binding = defaultBinding;
    }

    public void setBinding(@NotNull KeyInfo keyInfo) {
        binding = Objects.requireNonNull(keyInfo);
    }

    public boolean isTriggeredBy(KeyInfo keyInfo, InputAction action) {
        return binding.equals(keyInfo) || (action == InputAction.END && binding.key() == keyInfo.key());
    }

    @Override
    public String getTranslationKey() {
        return Spaetial.translationKey(null, "key", name, "name");
    }

    public Text getDefaultBindingText() { return defaultBinding.getText(); }
    public Text getBindingText() { return binding.getText(); }

    public enum Category implements Translatable {
        COMMON("common"),
        QUICK_CONFIG("quick_config"),
        OPERATION("operation"),
        PASTE("paste"),
        SELECTION("selection"),
        NORMAL("normal"),
        COPY("copy"),
        DEBUG("debug");
        private final String name;
        Category(String name) {
            this.name = name;
        }

        @Override
        public String getTranslationKey() {
            return Spaetial.translationKey(null, "key_category", name, "name");
        }
    }
}
