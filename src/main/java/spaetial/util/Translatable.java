package spaetial.util;

import net.minecraft.text.Text;

public interface Translatable {
    String getTranslationKey();
    default Text getTranslation() {
        return Text.translatable(getTranslationKey());
    }
}
