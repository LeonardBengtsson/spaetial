package spaetial.editing.state;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.*;
import org.jetbrains.annotations.Nullable;
import spaetial.ClientConfig;
import spaetial.editing.ClientManager;
import spaetial.editing.Filter;
import spaetial.editing.MovementInPlane;
import spaetial.editing.state.common.CommonActiveSchematicPlacementState;
import spaetial.ClientEvents;
import spaetial.input.InputAction;
import spaetial.input.ModInput;
import spaetial.schematic.ClientSchematicPlacements;
import spaetial.schematic.SchematicPlacement;
import spaetial.util.BoxUtil;
import spaetial.util.VecUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents the mod's state of having selected a schematic placement for manipulation. Only one schematic placement
 * can be selected at a time, although other schematics can be worked on in the meantime. Note that this class overrides
 * the {@link EditingState#getActiveSchematicPlacementId()} method, which provides the selected schematic placemeent's ID
 * for rendering and other uses.
 *
 * <p><p>Inputs:
 * <ul>
 *     <li><b>V / LMB:</b> Completes the schematic and removes it from the client's list of schematic placements ({@link ClientSchematicPlacements#schematicPlacements})</li>
 *     <li><b>RMB:</b> Deselects the schematic placement and sets the state to a {@link NormalState}</li>
 *     <li><b>Ctrl Scroll:</b> Moves the active schematic placement</li>
 * </ul>
 *
 * @see EditingState
 * @see ClientSchematicPlacements#schematicPlacements
 * @see ClientEvents#onRender(MinecraftClient, MatrixStack, Camera)
 */
public class ActiveSchematicPlacementState extends EditingState {
    private final UUID placementId;
    private @Nullable SchematicPlacement placement;

    private @Nullable MovementInPlane movementInPlane = null;

    public ActiveSchematicPlacementState(UUID placementId) {
        this.placementId = placementId;
        update();
    }

    private static final List<ModInput> INPUTS = List.of(
        ModInput.CANCEL,

        ModInput.MOVE_IN_PLANE,
        ModInput.MOVE_FORWARDS, ModInput.MOVE_BACKWARDS, ModInput.MOVE_RIGHT, ModInput.MOVE_LEFT,
        ModInput.MOVE_TO_TARGET, ModInput.MOVE_TO_TARGET_SURFACE,

        ModInput.CONFIRM, ModInput.CONFIRM_ALTERNATIVE
    );

    @Override
    public @Nullable UUID getActiveSchematicPlacementId() {
        return placementId;
    }

    @Override
    public @Nullable Vec3i getActiveSchematicPlacementOffset() {
        return movementInPlane == null ? null : movementInPlane.getCurrentOffset();
    }

    @Override
    public List<ModInput> getApplicableInputs() { return INPUTS; }

    @Override
    public boolean onInput(MinecraftClient client, ClientPlayerEntity player, ModInput binding, InputAction action, int repeats) {
        return switch (binding) {
            case CONFIRM, CONFIRM_ALTERNATIVE -> {
                if (action != InputAction.BEGIN) yield false;
                if (placement == null) yield false;
                if (player.getWorld().getRegistryKey() != placement.dim) yield false;
                if (movementInPlane != null) {
                    var offset = movementInPlane.consumeOffset();
                    if (!offset.equals(Vec3i.ZERO) && placement != null) {
                        var pos = placement.minPos.add(movementInPlane.getCurrentOffset());
                        ClientSchematicPlacements.moveSchematicPlacement(placementId, pos, placement.dim);
                    }
                    movementInPlane = null;
                    update();
                    yield true;
                }

                var ignoreAir = ClientConfig.getIgnoreAir(binding == ModInput.CONFIRM);
                ClientSchematicPlacements.completeSchematicPlacement(placementId, ignoreAir ? Filter.DENY_AIR : Filter.ALLOW_ALL);
                ClientManager.switchEditingState(new NormalState());

                // TODO remove
                var removeBp = new ArrayList<UUID>();
                var removeCbp = new ArrayList<UUID>();
                ClientSchematicPlacements.iterateSchematicPlacements((id, bp) -> removeBp.add(id));
                ClientSchematicPlacements.iterateOwnSharedSchematicPlacements((id, bp) -> removeCbp.add(id));
                removeBp.forEach(ClientSchematicPlacements::removeSchematicPlacement);
                removeCbp.forEach(ClientSchematicPlacements::removeSchematicPlacement);
                yield true;
            }
            case CANCEL -> {
                if (action != InputAction.BEGIN) yield false;
                if (placement == null) yield false;

                if (movementInPlane != null) {
                    movementInPlane = null;
                } else {
                    ClientManager.switchEditingState(new NormalState());

                    // TODO remove
                    var removeBp = new ArrayList<UUID>();
                    var removeCbp = new ArrayList<UUID>();
                    ClientSchematicPlacements.iterateSchematicPlacements((id, bp) -> removeBp.add(id));
                    ClientSchematicPlacements.iterateOwnSharedSchematicPlacements((id, bp) -> removeCbp.add(id));
                    removeBp.forEach(ClientSchematicPlacements::removeSchematicPlacement);
                    removeCbp.forEach(ClientSchematicPlacements::removeSchematicPlacement);
                }
                yield true;
            }
            case MOVE_FORWARDS, MOVE_BACKWARDS, MOVE_RIGHT, MOVE_LEFT -> {
                if (action != InputAction.BEGIN && action != InputAction.CONTINUOUS) yield false;
                if (placement == null) yield false;
                if (player.getWorld().getRegistryKey() != placement.dim) yield false;
                if (binding.inherentDirection == null) yield false;
                if (movementInPlane != null) yield false;

                Vec3i vec = VecUtil.scrollVector(player, binding.inherentDirection).multiply(repeats);
                if (vec.equals(Vec3i.ZERO)) yield false;
                var currentPos = BoxUtil.minPos(placement.box);
                ClientSchematicPlacements.moveSchematicPlacement(placementId, currentPos.add(vec.getX(), vec.getY(), vec.getZ()), placement.dim);
                update();
                yield true;
            }
            case MOVE_TO_TARGET, MOVE_TO_TARGET_SURFACE -> {
                if (action != InputAction.BEGIN) yield false;
                if (placement == null) yield false;
                if (player.getWorld().getRegistryKey() != placement.dim) yield false;
                if (movementInPlane != null) yield false;

                var surface = binding == ModInput.MOVE_TO_TARGET_SURFACE;
                var pos = BoxUtil.boxPositioning(player, ClientConfig.Persistent.getMaxRaycastRange(), ClientConfig.getTargetSurface(surface), placement.box.getDimensions());
                ClientSchematicPlacements.moveSchematicPlacement(placementId, pos, placement.dim);
                update();
                yield true;
            }
            case MOVE_IN_PLANE -> {
                if (action == InputAction.END) {
                    if (!ClientConfig.Persistent.getMoveInPlaneReleaseCancel()) yield false;
                    if (placement == null || movementInPlane == null) {
                        movementInPlane = null;
                        yield false;
                    }
                    var pos = placement.minPos.add(movementInPlane.getCurrentOffset());
                    ClientSchematicPlacements.moveSchematicPlacement(placementId, pos, placement.dim);
                    movementInPlane = null;
                    yield true;
                } else if (action == InputAction.BEGIN) {
                    if (placement == null) yield false;
                    if (player.getWorld().getRegistryKey() != placement.dim) yield false;
                    movementInPlane = MovementInPlane.create(player, placement.box);
                    yield true;
                } else {
                    yield false;
                }
            }
            default -> false;
        };
    }

    private void update() {
        placement = ClientSchematicPlacements.getSchematicPlacement(placementId);
    }

    @Override
    public void onClientTick(MinecraftClient client) {
        update();
        if (placement == null || client.world == null || placement.dim != client.world.getRegistryKey()) {
            ClientManager.switchEditingState(new NormalState());
        }
    }

    @Override
    public void onRenderWorld(boolean isOtherPlayer, MinecraftClient client, MatrixStack matrices, Camera camera) {
        if (client.world == null) return;
        if (!isOtherPlayer && placement != null && movementInPlane != null) {
            if (client.world.getRegistryKey() != placement.dim) return;
            if (client.player != null) {
                movementInPlane.update(client.player);
            }
            movementInPlane.drawGrid(client, matrices, camera.getPos(), BoxUtil.offset(placement.box, movementInPlane.getCurrentOffset()), ClientConfig.Persistent.getSelectionColor(false));
        }
    }

    @Override
    public EditingStateType getType() {
        return EditingStateType.ACTIVE_SCHEMATIC_PLACEMENT;
    }

    @Override
    public String getDebugText() {
        var s = new StringBuilder(this.getClass().getSimpleName() + "[");
        s.append(placementId);
        if (movementInPlane != null) {
            s.append(" moving: (");
            s.append(movementInPlane.getDebugText());
            s.append(")");
        }
        return s + "]";
    }

    @Override
    public Text getInfoText() {
        // TODO
        return Text.empty();
    }

    @Override
    public InteractionType getInteractionType() {
        return InteractionType.ON;
    }

    @Override
    public CommonActiveSchematicPlacementState convertToCommon() {
        return new CommonActiveSchematicPlacementState(placementId);
    }
}
