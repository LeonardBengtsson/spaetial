package spaetial.editing.state;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3i;
import org.jetbrains.annotations.Nullable;
import spaetial.editing.ClientManager;
import spaetial.editing.state.common.*;
import spaetial.ClientEvents;
import spaetial.gui.MainModHudOverlay;
import spaetial.input.InputListener;
import spaetial.input.ModInput;
import spaetial.util.functional.TypeSupplier;

import java.util.UUID;

/**
 * Represents a state the mod can be in while performing various actions and navigating the mod's different functions
 */
public abstract class EditingState implements TypeSupplier<EditingStateType>, InputListener {
    public abstract void onClientTick(MinecraftClient client);

    /**
     * @param isOtherPlayer {@code false} if rendering the client's own editing state, {@code true} if rendering another player's state
     */
    public abstract void onRenderWorld(boolean isOtherPlayer, MinecraftClient client, MatrixStack matrices, Camera camera);

    /**
     * @return A string representing all internal data in the state for debugging purposes. Large unnecessary data such
     *         as regions can be omitted
     */
    public abstract String getDebugText();

    public abstract Text getInfoText();

    public abstract InteractionType getInteractionType();

    /**
     * Man I love boilerplate just for the sake of boilerplate...
     * @return A representation of this state that can be shared between the client and the server
     */
    public abstract CommonEditingState convertToCommon();

    /**
     * @param state Has to match whatever {@link EditingStateType} its {@link CommonEditingState#getType()} method returns
     * @return The client's version of a state that can be shared between the client and the server
     * @throws ClassCastException If the state's {@link EditingStateType} does not match the expected class
     */
    public static EditingState convertToClientLossy(CommonEditingState state) throws ClassCastException {
        return switch (state.getType()) {
            case NORMAL -> {
                var normalState = (CommonNormalState) state;
                yield new NormalState(normalState.quickSetDim(), normalState.quickSetPos1(), normalState.quickSetPos2(), normalState.quickSetReplace());
            }
            case TURNED_OFF -> new TurnedOffState(new NormalState());
            case CUBOID_SELECTION -> new CuboidSelectionState(((CommonCuboidSelectionState) state).bounds());
            case CARVE_SELECTION -> new CarveSelectionState();
            case COPY -> {
                var copyCutState = (CommonCopyState) state;
                yield new CopyState(copyCutState.sourceDim(), copyCutState.selection(), copyCutState.mode(), copyCutState.isCutMode(), copyCutState.destDim(), copyCutState.delta(), copyCutState.lineStackSize(), copyCutState.lineStackSpacing(), copyCutState.volumeStackSize(), copyCutState.volumeStackSpacing(), null);
            }
            case ACTIVE_SCHEMATIC_PLACEMENT -> new ActiveSchematicPlacementState(((CommonActiveSchematicPlacementState) state).placementId());
        };
    }

    /**
     * @return The state the mod will start in when no saved state exists
     */
    // TODO change
    public static EditingState getDefault() {
        return new NormalState();
    }

    /**
     * Used for rendering schematic placements differently when active
     * @return A UUID value if the state represents a specific schematic placement being active, otherwise null
     *
     * @see ActiveSchematicPlacementState
     * @see ClientEvents#onRender(MinecraftClient, MatrixStack, Camera)
     */
    public @Nullable UUID getActiveSchematicPlacementId() {
        return null;
    }

    /**
     * Used for rendering schematic placements correctly while active and being moved
     *
     * @see ActiveSchematicPlacementState
     */
    public @Nullable Vec3i getActiveSchematicPlacementOffset() {
        return null;
    }

    /**
     * Runs when the client executes {@code /spaetial off} or {@code /spaetial on}. Override in editing states that
     * change this behaviour, such as {@link TurnedOffState}
     *
     * @see TurnedOffState
     */
    public void receiveToggleModCommand(boolean toggle) {
        if (!toggle) ClientManager.switchEditingState(new TurnedOffState(ClientManager.getEditingState()));
    }

    /**
     * @return {@code false} for states other than {@link TurnedOffState}
     *
     * @see TurnedOffState
     */
    public boolean isTurnedOff() { return false; }

    /**
     * Describes how a state should interact with rendering, input and builder allays
     */
    public enum InteractionType {
        OFF(false, false, false),
        ON(true, true, true);

        /**
         * Whether editing states of this type should render schematics
         */
        public final boolean renderSchematicPlacements;

        /**
         * Whether editing states of this type should render other players' states
         */
        public final boolean renderOthersStates;

        /**
         * Whether editing states of this type should render the {@link MainModHudOverlay} as well as system messages
         */
        public final boolean renderHud;

        /**
         * Whether editing states of this type should process keyboard key and mouse button presses other than
         * {@link ModInput#TOGGLE_MOD} and {@link ModInput#CONFIG}, as well as mouse scrolling
         */
        public final boolean processInput;

        /**
         * Whether editing states of this type should enable builder allays to work
         */
        public final boolean enableAllayBehaviour;

        InteractionType(boolean render, boolean input, boolean allays) {
            this.renderSchematicPlacements = render;
            this.renderOthersStates = render;
            this.renderHud = render;
            this.processInput = input;
            this.enableAllayBehaviour = allays;
        }
    }
}
