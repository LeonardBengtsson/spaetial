package spaetial.input;

import net.minecraft.client.util.InputUtil;

@Deprecated
public final class KeyBinds {
    private KeyBinds() {}

    // normal mode
    private static final InputUtil.Key UNDO = InputUtil.fromTranslationKey("key.keyboard.z");
    private static final InputUtil.Key SHAPE_MENU = InputUtil.fromTranslationKey("key.keyboard.x");
    private static final InputUtil.Key SCHEMATICS_MENU = InputUtil.fromTranslationKey("key.keyboard.c");
    private static final InputUtil.Key PASTE = InputUtil.fromTranslationKey("key.keyboard.v");
    private static final InputUtil.Key CUBOID_SELECTION_MODE = InputUtil.fromTranslationKey("key.mouse.left");
    private static final InputUtil.Key QUICK_SET = InputUtil.fromTranslationKey("key.keyboard.g");
    public static InputUtil.Key getUndoKey() { return UNDO; }
    public static InputUtil.Key getShapeModeKey() { return SHAPE_MENU; }
    public static InputUtil.Key getSchematicsMenuKey() { return SCHEMATICS_MENU; }
    public static InputUtil.Key getPasteKey() { return PASTE; }
    public static InputUtil.Key getCuboidSelectionModeKey() { return CUBOID_SELECTION_MODE; }
    public static InputUtil.Key getQuickSetKey() { return QUICK_SET; }

    // selection modes
    private static final InputUtil.Key EXTEND_SELECTION = InputUtil.fromTranslationKey("key.mouse.left");
    private static final InputUtil.Key CUT = InputUtil.fromTranslationKey("key.keyboard.x");
    private static final InputUtil.Key COPY = InputUtil.fromTranslationKey("key.keyboard.c");
    public static InputUtil.Key getExtendSelectionKey() { return EXTEND_SELECTION; }
    public static InputUtil.Key getCutKey() { return CUT; }
    public static InputUtil.Key getCopyKey() { return COPY; }

    // copy mode
    public static final InputUtil.Key CYCLE_CLONE_MODE = InputUtil.fromTranslationKey("key.keyboard.z");
    public static final InputUtil.Key MOVE_DESTINATION = InputUtil.fromTranslationKey("key.mouse.right");
    public static InputUtil.Key getCycleCloneModeKey() { return CYCLE_CLONE_MODE; }
    public static InputUtil.Key getConfirmCopyKey() { return COPY; }
    public static InputUtil.Key getConfirmCutKey() { return CUT; }
    public static InputUtil.Key getPasteAndContinueKey() { return PASTE; }
    public static InputUtil.Key getMoveDestinationKey() { return MOVE_DESTINATION; }

    // active schematic placement mode
    public static InputUtil.Key getConfirmPasteKey() { return PASTE; }

    // all modes
    private static final InputUtil.Key SETTINGS_MENU = InputUtil.fromTranslationKey("key.keyboard.b");
    private static final InputUtil.Key CONFIRM_ACTION = InputUtil.fromTranslationKey("key.mouse.left");
    private static final InputUtil.Key CANCEL_ACTION = InputUtil.fromTranslationKey("key.mouse.right");
    public static InputUtil.Key getSettingsMenuKey() { return SETTINGS_MENU; }
    public static InputUtil.Key getConfirmActionKey() { return CONFIRM_ACTION; }
    public static InputUtil.Key getCancelActionKey() { return CANCEL_ACTION; }

    // TODO debug; remove
    // debug
    private static final InputUtil.Key DEBUG_KEY_1 = InputUtil.fromTranslationKey("key.keyboard.u");
    private static final InputUtil.Key DEBUG_KEY_2 = InputUtil.fromTranslationKey("key.keyboard.i");
    private static final InputUtil.Key DEBUG_KEY_3 = InputUtil.fromTranslationKey("key.keyboard.o");
    public static InputUtil.Key getDebugKey1() { return DEBUG_KEY_1; }
    public static InputUtil.Key getDebugKey2() { return DEBUG_KEY_2; }
    public static InputUtil.Key getDebugKey3() { return DEBUG_KEY_3; }
}
