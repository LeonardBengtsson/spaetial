package spaetial.util.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

public final class ScreenUtil {
    private ScreenUtil() {}

    public static void openScreen(MinecraftClient client, Screen screen) {
        screen.init(client, client.getWindow().getScaledWidth(), client.getWindow().getScaledHeight());
        client.setScreen(screen);
    }
}
