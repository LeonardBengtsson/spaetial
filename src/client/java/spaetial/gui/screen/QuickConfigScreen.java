package spaetial.gui.screen;

import net.minecraft.client.gui.screen.Screen;
import spaetial.Spaetial;

/**
 * TODO
 */
public class QuickConfigScreen extends Screen {
    public QuickConfigScreen() {
        super(Spaetial.translate("gui", "quick_config.title"));
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
