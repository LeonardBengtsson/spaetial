package spaetial.editing.state;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import spaetial.ClientConfig;
import spaetial.Spaetial;
import spaetial.editing.MovementInPlane;
import spaetial.input.ModInput;
import spaetial.schematic.Schematic;
import spaetial.schematic.SchematicMetadata;
import spaetial.editing.ClientManager;
import spaetial.editing.selection.CuboidSelection;
import spaetial.editing.selection.Selection;
import spaetial.editing.state.common.CommonCuboidSelectionState;
import spaetial.input.InputAction;
import spaetial.gui.screen.SelectionContextWheelScreen;
import spaetial.networking.ClientRegionRequest;
import spaetial.render.RenderUtil;
import spaetial.util.BoxUtil;
import spaetial.util.VecUtil;
import spaetial.util.color.ColorUtil;
import spaetial.util.math.RaycastUtil;

import java.util.Base64;
import java.util.List;

/**
 * Represents the mod's state of selecting a cuboid (3D rectangular prism) volume of blocks for further editing.
 *
 * <p><p>Inputs:
 * <ul>
 *     <li><b>Alt LMB:</b> Extend the selection to include the targeted block (defaults to the player's feet position if no block is within range</li>
 *     <li><b>Ctrl Scroll</b> Move the selection</li>
 *     <li><b>Alt Scroll</b> Resize the closest side of the selection along an axis</li>
 *     <li><b>RMB:</b> Cancel the selection, returning to a {@link NormalState}</li>
 *     <li><b>X:</b> Enter {@link CopyState} in cut mode with the selected region</li>
 *     <li><b>C:</b> Enter {@link CopyState} in copy mode with the selected region</li>
 *     <li><b>B:</b> Open the {@link SelectionContextWheelScreen} to configure various aspects of the selection</li>
 *     <li><b>L:</b> Copy the selected region to the system clipboard as a string (<b>DEBUG, not permanent feature</b>)</li>
 * </ul>
 *
 * @see EditingState
 * @see Selection
 * @see CopyState
 * @see SelectionContextWheelScreen
 * @see CarveSelectionState
 * @see Schematic#encode()
 */
public class CuboidSelectionState extends EditingState {
    private RegistryKey<World> dim;
    private BlockBox bounds;

    private @Nullable MovementInPlane movementInPlane = null;

    public CuboidSelectionState(BlockPos origin, RegistryKey<World> dim) {
        this.bounds = new BlockBox(origin);
        this.dim = dim;
    }

    public CuboidSelectionState(BlockBox box) {
        this.bounds = box;
    }

    private void extend(BlockPos pos) {
        this.bounds = BoxUtil.extend(this.bounds, pos);
    }

    private static final List<ModInput> INPUTS = List.of(
        ModInput.DEBUG_1, ModInput.DEBUG_2, ModInput.DEBUG_3,

        ModInput.CANCEL,
        ModInput.CUT, ModInput.COPY,

        ModInput.MOVE_IN_PLANE,
        ModInput.MOVE_FORWARDS, ModInput.MOVE_BACKWARDS, ModInput.MOVE_RIGHT, ModInput.MOVE_LEFT,
        ModInput.MOVE_TO_TARGET, ModInput.MOVE_TO_TARGET_SURFACE,

        ModInput.RESIZE_IN_PLANE,
        ModInput.RESIZE_FORWARDS, ModInput.RESIZE_BACKWARDS, ModInput.RESIZE_RIGHT, ModInput.RESIZE_LEFT,
        ModInput.RESIZE_FORWARDS_ALTERNATIVE, ModInput.RESIZE_BACKWARDS_ALTERNATIVE,

        ModInput.EXTEND_SELECTION, ModInput.EXTEND_SELECTION_SURFACE,

        ModInput.CONFIRM
    );

    @Override
    public List<ModInput> getApplicableInputs() {
        return INPUTS;
    }

    @Override
    public boolean onInput(MinecraftClient client, ClientPlayerEntity player, ModInput binding, InputAction action, int repeats) {
        return switch (binding) {
            case EXTEND_SELECTION, EXTEND_SELECTION_SURFACE -> {
                if (action != InputAction.BEGIN) yield false;
                if (player.getWorld().getRegistryKey() != dim) yield false;
                if (movementInPlane != null) yield false;
                boolean surface = ClientConfig.getTargetSurface(binding == ModInput.EXTEND_SELECTION_SURFACE);
                BlockPos pos = RaycastUtil.raycastOrCurrentPos(player, ClientConfig.Persistent.getMaxRaycastRange(), surface, false, RaycastContext.ShapeType.OUTLINE);
                this.extend(pos);
                ClientManager.onEditingStateUpdate();
                yield true;
            }
            case CANCEL -> {
                if (action != InputAction.BEGIN) yield false;
                if (movementInPlane != null) {
                    movementInPlane = null;
                    yield true;
                } else {
                    ClientManager.switchEditingState(new NormalState());
                    yield true;
                }
            }
            case CUT, COPY -> {
                if (action != InputAction.BEGIN) yield false;
                if (player.getWorld().getRegistryKey() != dim) yield false;
                if (movementInPlane != null) yield false;
                var cut = binding == ModInput.CUT;
                ClientManager.switchEditingState(new CopyState(client, player.clientWorld.getRegistryKey(), new CuboidSelection(this.bounds), cut));
                yield true;
            }
            case MOVE_FORWARDS, MOVE_BACKWARDS, MOVE_RIGHT, MOVE_LEFT -> {
                if (action != InputAction.BEGIN && action != InputAction.CONTINUOUS) yield false;
                if (player.getWorld().getRegistryKey() != dim) yield false;
                if (binding.inherentDirection == null) yield false;
                if (movementInPlane != null) yield false;

                Vec3i offset = VecUtil.scrollVector(player, binding.inherentDirection).multiply(repeats);
                if (offset.equals(Vec3i.ZERO)) yield false;
                bounds = bounds.offset(offset.getX(), offset.getY(), offset.getZ());
                ClientManager.onEditingStateUpdate();
                yield true;
            }
            case MOVE_TO_TARGET, MOVE_TO_TARGET_SURFACE -> {
                if (action != InputAction.BEGIN) yield false;
                if (movementInPlane != null) yield false;
                var surface = ClientConfig.getTargetSurface(binding == ModInput.MOVE_TO_TARGET_SURFACE);
                bounds = BoxUtil.moveTo(bounds, BoxUtil.boxPositioning(player, ClientConfig.Persistent.getMaxRaycastRange(), ClientConfig.getTargetSurface(surface), bounds.getDimensions()));
                var pos = RaycastUtil.raycastOrAtBlockReach(
                    player,
                    ClientConfig.Persistent.getMaxRaycastRange(),
                    surface,
                    ClientConfig.Local.getTargetFluids(),
                    RaycastContext.ShapeType.OUTLINE
                );
                var offset = pos.subtract(bounds.getCenter().withY(bounds.getMinY()));
                bounds = bounds.offset(offset.getX(), offset.getY(), offset.getZ());
                dim = player.getWorld().getRegistryKey();
                ClientManager.onEditingStateUpdate();
                yield true;
            }
            case CONFIRM -> {
                if (movementInPlane == null) yield false;
                var offset = movementInPlane.consumeOffset();
                if (!offset.equals(Vec3i.ZERO)) {
                    bounds = BoxUtil.offset(bounds, offset);
                    ClientManager.onEditingStateUpdate();
                }
                movementInPlane = null;
                yield true;
            }
            case MOVE_IN_PLANE -> {
                if (action == InputAction.END && ClientConfig.Persistent.getMoveInPlaneReleaseCancel()) {
                    if (movementInPlane == null) yield false;
                    movementInPlane = null;
                    yield true;
                } else if (action == InputAction.BEGIN) {
                    if (player.getWorld().getRegistryKey() != dim) yield false;
                    movementInPlane = MovementInPlane.create(player, bounds);
                    yield true;
                } else {
                    yield false;
                }
            }
            case RESIZE_FORWARDS, RESIZE_BACKWARDS,
                 RESIZE_RIGHT, RESIZE_LEFT,
                 RESIZE_FORWARDS_ALTERNATIVE, RESIZE_BACKWARDS_ALTERNATIVE
            -> {
                if (action != InputAction.BEGIN && action != InputAction.CONTINUOUS) yield false;
                if (player.getWorld().getRegistryKey() != dim) yield false;
                if (binding.inherentDirection == null) yield false;
                if (movementInPlane != null) yield false;

                Vec3i vec = VecUtil.scrollVector(player, binding.inherentDirection).multiply(repeats);
                if (vec.equals(Vec3i.ZERO)) yield false;
                var otherSide = binding == ModInput.RESIZE_FORWARDS_ALTERNATIVE || binding == ModInput.RESIZE_BACKWARDS_ALTERNATIVE;
                bounds = BoxUtil.resizeAction(bounds, VecUtil.signum(player.getRotationVecClient()), vec, otherSide);
                ClientManager.onEditingStateUpdate();
                yield true;
            }
            case DEBUG_1 -> {
                if (action != InputAction.BEGIN) yield false;
                if (movementInPlane != null) yield false;
                ClientRegionRequest.requestRegion(
                    client,
                    new CuboidSelection(this.bounds),
                    player.clientWorld.getRegistryKey(),
                    true,
                    region -> {
                        var schem = new Schematic(region, SchematicMetadata.createClipboardInfo(player.getUuid()));
                        var string = Base64.getEncoder().encodeToString(schem.encode());
                        client.keyboard.setClipboard(string);
                        client.inGameHud.setOverlayMessage(Text.literal("Copied region to clipboard"), false);
                        Spaetial.info("Copied region to clipboard");
                    }
                );
                yield true;
            }
            default -> false;
        };
    }

    @Override
    public void onClientTick(MinecraftClient client) {
        if (client.world == null || client.world.getRegistryKey() != dim) {
            Spaetial.debug(client.world, client.world.getRegistryKey(), dim);
            ClientManager.switchEditingState(new NormalState());
        }
    }

    @Override
    public void onRenderWorld(boolean isOtherPlayer, MinecraftClient client, MatrixStack matrices, Camera camera) {
        if (client.world == null) return;
        if (client.world.getRegistryKey() != dim) return;
        BlockBox blockBox = this.bounds;
        if (!isOtherPlayer && movementInPlane != null) {
            if (client.player != null) {
                movementInPlane.update(client.player);
            }
            var offset = movementInPlane.getCurrentOffset();
            blockBox = blockBox.offset(offset.getX(), offset.getY(), offset.getZ());
            movementInPlane.drawGrid(client, matrices, camera.getPos(), blockBox, ClientConfig.Persistent.getSelectionColor(false));
        }
        RenderUtil.drawBox(client, matrices, camera.getPos(), BoxUtil.viewAdjustBox(BoxUtil.toBox(blockBox), camera.getPos()), ClientConfig.Persistent.getSelectionColor(isOtherPlayer));
        if (movementInPlane == null) {
            boolean es = ModInput.EXTEND_SELECTION.isPressingModifierKeys(false);
            boolean ess = ModInput.EXTEND_SELECTION_SURFACE.isPressingModifierKeys(false);
            if ((es || ess) && client.player != null) {
                BlockPos pos = RaycastUtil.raycastOrCurrentPos(client.player, ClientConfig.Persistent.getMaxRaycastRange(), ess, ClientConfig.Local.getTargetFluids(), RaycastContext.ShapeType.OUTLINE);
                BlockBox box = BoxUtil.extend(bounds, pos);
                RenderUtil.drawBox(
                    client,
                    matrices,
                    camera.getPos(),
                    BoxUtil.viewAdjustBox(
                        BoxUtil.toBox(box),
                        camera.getPos()
                    ),
                    ColorUtil.withPreviewAlpha(ClientConfig.Persistent.getSelectionColor(isOtherPlayer))
                );
            }
        }
    }

    @Override
    public EditingStateType getType() {
        return EditingStateType.CUBOID_SELECTION;
    }

    @Override
    public String getDebugText() {
        return "CuboidSelectionState [bounds: " + bounds + " dim: " + dim.getValue() + "]";
    }

    @Override
    public Text getInfoText() {
        // TODO
        return null;
    }

    @Override
    public InteractionType getInteractionType() {
        return InteractionType.ON;
    }

    @Override
    public CommonCuboidSelectionState convertToCommon() { return new CommonCuboidSelectionState(bounds); }
}
