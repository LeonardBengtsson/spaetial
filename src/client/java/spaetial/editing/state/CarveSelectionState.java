package spaetial.editing.state;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.apache.commons.lang3.NotImplementedException;
import spaetial.editing.region.MaskedBlockBox;
import spaetial.editing.selection.MaskedCuboidSelection;
import spaetial.editing.state.common.CommonCarveSelectionState;
import spaetial.input.InputAction;
import spaetial.input.ModInput;

import java.util.List;

/**
 * Represents the mod's state of "carving" out blocks from another selection. Carving can be done through removing
 * individual blocks or rows of blocks from a selection, and results in a {@link MaskedCuboidSelection}, where the removed
 * parts of the selection are ignored while editing.
 *
 * <p><p>Inputs:
 * <ul>
 *     <li><b>LMB:</b> Remove block</li>
 *     <li><b>Ctrl LMB:</b> Remove column of blocks</li>
 *     <li><b>RMB:</b> Restore selection to cuboid selection</li>
 *     <li><b>Ctrl Z:</b> Undo latest change</li>
 *     <li><b>Ctrl Alt Z:</b> Redo change</li>
 * </ul>
 *
 * @see EditingState
 * @see MaskedCuboidSelection
 * @see MaskedBlockBox
 */
public class CarveSelectionState extends EditingState {
    public CarveSelectionState() {
        //TODO
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
    public void onRenderWorld(boolean isOtherPlayer, MinecraftClient client, MatrixStack matrices, Camera camera) {

    }

    @Override
    public EditingStateType getType() {
        return EditingStateType.CARVE_SELECTION;
    }

    @Override
    public String getDebugText() {
        // TODO
        throw new NotImplementedException();
    }

    @Override
    public Text getInfoText() {
        // TODO
        throw new NotImplementedException();
    }

    @Override
    public InteractionType getInteractionType() {
        return InteractionType.ON;
    }

    @Override
    public CommonCarveSelectionState convertToCommon() {
        // TODO
        throw new NotImplementedException();
    }
}
