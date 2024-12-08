package spaetial.input;

import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;

public record KeyInfo(InputUtil.Key key, boolean shift, boolean ctrl, boolean alt) {
    private static final String SCROLL_UP_ID = "100";
    private static final String SCROLL_DOWN_ID = "101";
    private static final String SCROLL_RIGHT_ID = "102";
    private static final String SCROLL_LEFT_ID = "103";

    public static KeyInfo fromStringAndMods(String s, boolean shift, boolean ctrl, boolean alt) throws IllegalArgumentException {
        var i1 = s.indexOf('.');
        if (i1 < 1 || i1 >= s.length() - 1) throw new IllegalArgumentException("Correct format: <type>.<name>");
        var type = s.substring(0, i1);
        var name = s.substring(i1 + 1);
        var translationKey = switch (type) {
            case "key" -> "key.keyboard." + name;
            case "scancode" -> "scancode." + name;
            case "mouse" -> "key.mouse." + name;
            case "scroll" -> {
                for (var dir : ScrollDirection.values()) {
                    if (dir.name.equals(name)) {
                        yield "key.mouse." + dir.id;
                    }
                }
                throw new IllegalArgumentException("Invalid scroll direction '" + name + "'");
            }
            default -> throw new IllegalArgumentException("Invalid key type '" + type + "'");
        };
        try {
            return new KeyInfo(InputUtil.fromTranslationKey(translationKey), shift, ctrl, alt);
        } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Invalid key type and name pair " + type + ", " + name, e);
        }
    }

    public static KeyInfo fromScrollAndMods(ScrollDirection dir, boolean shift, boolean ctrl, boolean alt) {
        return new KeyInfo(InputUtil.fromTranslationKey("key.mouse." + dir.id), shift, ctrl, alt);
    }

    public String keyToString() {
        return switch (key.getCategory()) {
            case KEYSYM -> "key." + key.toString().substring("key.keyboard.".length());
            case SCANCODE -> key.toString();
            case MOUSE -> {
                var name = key.toString().substring("key.mouse.".length());
                yield switch (name) {
                    case SCROLL_UP_ID -> "scroll.up";
                    case SCROLL_DOWN_ID -> "scroll.down";
                    case SCROLL_LEFT_ID -> "scroll.left";
                    case SCROLL_RIGHT_ID -> "scroll.right";
                    default -> "mouse." + name;
                };
            }
        };
    }

    /**
     * @return A score of how closely {@code this} matches the given {@code binding}. A value of {@code -1} indicates no
     *         match and is given if the keys are different or the binding requires more modifier keys to be pressed,
     *         and values of {@code >=0} are given based on how many modifier keys are matching
     */
    public int getMatchPriority(KeyInfo binding) {
        if (key != binding.key) return -1;
        if (!shift && binding.shift) return -1;
        if (!ctrl && binding.ctrl) return -1;
        if (!alt && binding.alt) return -1;
        return 3 - (shift == binding.shift ? 1 : 0) - (ctrl == binding.ctrl ? 1 : 0) - (alt == binding.alt ? 1 : 0);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o instanceof KeyInfo other)
            return this.key.equals(other.key) && this.shift == other.shift && this.ctrl == other.ctrl && this.alt == other.alt;
        return false;
    }

    public Text getText() {
        var t = Text.empty();
        if (ctrl) t.append("Ctrl + ");
        if (alt) t.append("Alt + ");
        if (shift) t.append("Shift + ");
        t.append(Text.translatable(key.getTranslationKey()));
        return t;
    }

    public enum ScrollDirection {
        UP("up", SCROLL_UP_ID),
        DOWN("down", SCROLL_DOWN_ID),
        RIGHT("right", SCROLL_RIGHT_ID),
        LEFT("left", SCROLL_LEFT_ID);

        private final String name;
        private final String id;
        ScrollDirection(String name, String id) {
            this.name = name;
            this.id = id;
        }
    }
}
