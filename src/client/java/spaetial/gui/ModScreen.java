package spaetial.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import spaetial.input.InputAction;
import spaetial.input.InputListener;
import spaetial.input.ModInput;

public abstract class ModScreen extends Screen implements InputListener {
    protected ModScreen(Text title) {
        super(title);
    }
}
