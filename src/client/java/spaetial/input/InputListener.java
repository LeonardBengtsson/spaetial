package spaetial.input;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

import java.util.List;

public interface InputListener {
    /**
     * @return A list of inputs that this listener subscribes to, ordered by their precedence
     */
    List<ModInput> getApplicableInputs();

    /**
     * Runs when the mod detects a keyboard key or mouse button press corresponding to any of the bindings returned by
     * {@link InputListener#getApplicableInputs()}
     *
     * @param binding The binding that triggered this event
     * @param action  Describes how the state of the key or mouse button changed
     * @param repeats How many times the binding was pressed
     * @return Whether the input was consumed (i.e. detected and used) by this event
     * @see ModInput
     * @see InputAction
     */
    boolean onInput(MinecraftClient client, ClientPlayerEntity player, ModInput binding, InputAction action, int repeats);
}
