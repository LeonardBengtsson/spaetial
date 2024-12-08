package spaetial.editing.state;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import spaetial.editing.ClientManager;
import spaetial.editing.state.common.CommonTurnedOffState;
import spaetial.input.InputAction;
import spaetial.input.ModInput;

import java.util.List;

/**
 * Represents the state of most functions of the mod being turned off on the client side, specifically:
 *
 * <p><p><b>Disables:</b>
 * <ul>
 *     <li>Rendering of selection boxes, regions, schematics, etc.</li>
 *     <li>Rendering of the HUD, system messages, etc.</li>
 *     <li>All keybindings except {@link ModInput#TOGGLE_MOD} and {@link ModInput#CONFIG}</li>
 *     <li>Builder allays being active</li>
 * </ul>
 *
 * <p><p><b>Does not disable:</b>
 * <ul>
 *     <li>Networking; sending config and state data to the server when requested, and receiving data from the server</li>
 *     <li>The server's actions. The server will still perform queued operations and the mod will function as usual for other players</li>
 *     <li>Other players' builder allays are still visible and active</li>
 * </ul>
 */
public class TurnedOffState extends EditingState {
    private final EditingState previousState;

    public TurnedOffState(EditingState previousState) {
        assert !previousState.isTurnedOff();
        this.previousState = previousState;
    }

    @Override
    public List<ModInput> getApplicableInputs() {
        return List.of();
    }

    @Override
    public boolean onInput(MinecraftClient client, ClientPlayerEntity player, ModInput binding, InputAction action, int repeats) {
        return false;
    }

    @Override
    public void onClientTick(MinecraftClient client) { }

    @Override
    public void onRenderWorld(boolean isOtherPlayer, MinecraftClient client, MatrixStack matrices, Camera camera) { }

    @Override
    public EditingStateType getType() {
        return EditingStateType.TURNED_OFF;
    }

    @Override
    public String getDebugText() {
        return "TurnedOffState []";
    }

    @Override
    public Text getInfoText() {
        return Text.empty();
    }

    @Override
    public InteractionType getInteractionType() {
        return InteractionType.OFF;
    }

    @Override
    public CommonTurnedOffState convertToCommon() { return new CommonTurnedOffState(); }

    @Override
    public void receiveToggleModCommand(boolean toggle) {
        if (toggle) ClientManager.switchEditingState(previousState);
    }

    @Override
    public boolean isTurnedOff() { return true; }
}
