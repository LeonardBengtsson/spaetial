package spaetial.editing.state;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import spaetial.ClientConfig;
import spaetial.editing.ClientManager;
import spaetial.editing.Filter;
import spaetial.editing.MovementInPlane;
import spaetial.editing.operation.CloneOperation;
import spaetial.editing.operation.CopyOperation;
import spaetial.editing.operation.LineStackOperation;
import spaetial.editing.operation.VolumeStackOperation;
import spaetial.editing.region.Region;
import spaetial.editing.selection.Selection;
import spaetial.editing.state.common.CommonCopyState;
import spaetial.editing.state.common.CommonEditingState;
import spaetial.input.InputAction;
import spaetial.input.ModInput;
import spaetial.networking.ClientRegionRequest;
import spaetial.render.RenderUtil;
import spaetial.util.StackUtil;
import spaetial.util.BoxUtil;
import spaetial.util.VecUtil;
import spaetial.util.math.RaycastUtil;

import java.util.List;

/**
 * Represents the mod's state of preparing to perform one of the following actions:
 * <ul>
 *     <li><b>Copy</b> a region to the clipboard</li>
 *     <li><b>Cut</b> a region to the clipboard, removing it in the process</li>
 *     <li><b>Clone</b> a region, placing a copy of it at another location</li>
 *     <li><b>Move</b> a region, removing it to place it at another location</li>
 *     <li><b>Stack</b> a region, placing multiple copies of it either in a row, or along one or more 3D axes</li>
 * </ul>
 *
 * <p><p>This state has three modes ({@link CopyStateMode});
 *
 * <p><b>Clone mode</b>, for cloning a region to another nearby location,
 * <p><b>Line stack mode</b>, for creating a row of objects in any direction using the selected region, and
 * <p><b>Volume stack mode</b>, for stacking the selected region along one or more axes.
 *
 * <p><p>This state can additionally be toggled between removing the source region (cut mode) or not (copy mode)
 *
 * <p><p>A region can also be instantly copied or cut to the clipboard without copying it right away
 *
 * <p><p>Inputs while in any mode:
 * <ul>
 *     <li><b>Z:</b> Cycle mode</li>
 *     <li><b>RMB:</b> Cancel and return to {@link NormalState}</li>
 * </ul>
 *
 * <p><p>Inputs while in clone mode:
 * <ul>
 *     <li><b>Ctrl scroll:</b> Move the destination</li>
 *     <li><b>Alt RMB:</b> Move the destination to the cursor</li>
 *     <li><b>C:</b> Switch to copy mode / perform copy </li>
 *     <li><b>X:</b> Switch to cut mode / perform cut</li>
 *     <li><b>LMB:</b> Perform clone/move</li>
 *     <li><b>V:</b> Perform clone/move and re-enter copy mode / cut mode with the pasted region as source</li>
 *     <li><b>V:</b> Perform clone/move according to the player's change in position</li>
 * </ul>
 *
 * <p><p>Inputs while in line stack mode or volume stack mode:
 * <ul>
 *     <li><b>Ctrl scroll:</b> Change the spacing between each element, the "{@code spacing}"</li>
 *     <li><b>Alt scroll:</b> Change the amount of objects pasted in the stack, the "{@code stackSize}"</li>
 *     <li><b>C:</b> Switch to copy mode </li>
 *     <li><b>X:</b> Switch to cut mode</li>
 *     <li><b>LMB:</b> Perform the stacking operation</li>
 *     <li><b>V:</b> Perform the stacking operation and re-enter copy mode / cut mode with the pasted region as source</li>
 * </ul>
 *
 * @see EditingState
 * @see CopyOperation
 * @see CloneOperation
 * @see LineStackOperation
 * @see VolumeStackOperation
 */
public class CopyState extends EditingState {
    private final RegistryKey<World> sourceDim;
    private final Selection selection;

    private CopyStateMode mode;
    private boolean isCutMode;

    private RegistryKey<World> destDim;
    private BlockPos delta;
    private int lineStackSize = 2;
    private boolean lineStackSizeReverseScroll = false;
    private BlockPos lineStackSpacing = BlockPos.ORIGIN;
    private Vec3i volumeStackSize = Vec3i.ZERO;
    private BlockPos volumeStackSpacing = BlockPos.ORIGIN;

    private @Nullable Region region = null;

    private @Nullable BlockPos originalPlayerPos = null;

    private @Nullable MovementInPlane movementInPlane = null;
    private @Nullable MovementInPlane stackSpacingInPlane = null;
    private @Nullable MovementInPlane volumeStackSizeInPlane = null;

    public CopyState(MinecraftClient client, RegistryKey<World> dim, Selection selection, boolean isCutMode) {
        this(client, dim, selection, isCutMode, BlockPos.ORIGIN);
    }

    private CopyState(MinecraftClient client, @NotNull RegistryKey<World> dim, Selection selection, boolean isCutMode, BlockPos delta) {
        this.sourceDim = dim;
        this.selection = selection;
        this.mode = CopyStateMode.CLONE;
        this.isCutMode = isCutMode;
        this.destDim = dim;
        this.delta = delta;
        if (client.player != null) {
            this.originalPlayerPos = client.player.getBlockPos();
        }
        if (ClientConfig.Persistent.shouldRequestRegions()) {
            ClientRegionRequest.requestRegion(client, selection, dim, false, region -> this.region = region);
        }
    }

    protected CopyState(@NotNull RegistryKey<World> sourceDim, Selection selection, CopyStateMode mode, boolean isCutMode, RegistryKey<World> destDim, BlockPos delta, int lineStackSize, BlockPos lineStackSpacing, Vec3i volumeStackSize, BlockPos volumeStackSpacing, @Nullable Region region) {
        this.sourceDim = sourceDim;
        this.selection = selection;
        this.mode = mode;
        this.isCutMode = isCutMode;
        this.destDim = destDim;
        this.delta = delta;
        this.lineStackSize = lineStackSize;
        this.lineStackSpacing = lineStackSpacing;
        this.volumeStackSize = volumeStackSize;
        this.volumeStackSpacing = volumeStackSpacing;
        this.region = region;
    }

    private BlockBox getOuterBounds(Selection selection, BlockPos lineStackSpacing, BlockPos volumeStackSpacing, Vec3i volumeStackSize) {
        return switch (mode) {
            case CLONE -> selection.getOuterBounds();
            case LINE_STACK -> StackUtil.lineStackBox(selection.getOuterBounds(), lineStackSize, lineStackSpacing);
            case VOLUME_STACK -> StackUtil.volumeStackBox(selection.getOuterBounds(), volumeStackSize, volumeStackSpacing);
        };
    }

    private BlockBox getOuterBounds() {
        return getOuterBounds(selection, lineStackSpacing, volumeStackSpacing, volumeStackSize);
    }

    private static BlockPos changeVolumeStackSpacing(BlockPos currentStackSpacing, Vec3i currentStackSize, Vec3i regionSize, Vec3i offset) {
        int x = currentStackSpacing.getX(), y = currentStackSpacing.getY(), z = currentStackSpacing.getZ();
        if (currentStackSize.getX() != 0) x += offset.getX() * (currentStackSize.getX() > 0 ? 1 : -1);
        if (currentStackSize.getY() != 0) y += offset.getY() * (currentStackSize.getY() > 0 ? 1 : -1);
        if (currentStackSize.getZ() != 0) z += offset.getZ() * (currentStackSize.getZ() > 0 ? 1 : -1);
        return new BlockPos(Math.max(x, -regionSize.getX()), Math.max(y, -regionSize.getY()), Math.max(z, -regionSize.getZ()));
    }

    private static final List<ModInput> INPUTS = List.of(
        ModInput.DEBUG_1, ModInput.DEBUG_2, ModInput.DEBUG_3,

        ModInput.CANCEL,

        ModInput.INCREASE_LINE_STACK_SIZE, ModInput.DECREASE_LINE_STACK_SIZE,
        ModInput.CHANGE_VOLUME_STACK_SIZE_IN_PLANE,
        ModInput.CHANGE_VOLUME_STACK_SIZE_FORWARDS, ModInput.CHANGE_VOLUME_STACK_SIZE_BACKWARDS, ModInput.CHANGE_VOLUME_STACK_SIZE_RIGHT, ModInput.CHANGE_VOLUME_STACK_SIZE_LEFT,

        ModInput.CHANGE_STACK_SPACING_IN_PLANE,
        ModInput.CHANGE_STACK_SPACING_FORWARDS, ModInput.CHANGE_STACK_SPACING_BACKWARDS, ModInput.CHANGE_STACK_SPACING_RIGHT, ModInput.CHANGE_STACK_SPACING_LEFT,

        ModInput.MOVE_IN_PLANE,
        ModInput.MOVE_FORWARDS, ModInput.MOVE_BACKWARDS, ModInput.MOVE_RIGHT, ModInput.MOVE_LEFT,
        ModInput.MOVE_TO_TARGET, ModInput.MOVE_TO_TARGET_SURFACE,

        ModInput.CONFIRM, ModInput.CONFIRM_ALTERNATIVE,
        ModInput.PASTE, ModInput.PASTE_SURFACE, ModInput.PASTE_ALTERNATIVE, ModInput.PASTE_ALTERNATIVE_SURFACE,
        ModInput.CUT, ModInput.CUT_WITH_AIR, ModInput.COPY, ModInput.COPY_WITH_AIR,

        ModInput.CYCLE_COPY_MODE, ModInput.CLONE_MODE, ModInput.LINE_STACK_MODE, ModInput.VOLUME_STACK_MODE
    );

    @Override
    public List<ModInput> getApplicableInputs() { return INPUTS; }

    @Override
    public boolean onInput(MinecraftClient client, ClientPlayerEntity player, ModInput binding, InputAction action, int repeats) {
        return switch (binding) {
            case CYCLE_COPY_MODE -> {
                if (action != InputAction.BEGIN) yield false;
                if (movementInPlane != null || stackSpacingInPlane != null || volumeStackSizeInPlane != null) yield false;
                mode = mode.cycle();
                ClientManager.onEditingStateUpdate();
                yield true;
            }
            case CLONE_MODE -> {
                if (action != InputAction.BEGIN) yield false;
                if (movementInPlane != null || stackSpacingInPlane != null || volumeStackSizeInPlane != null) yield false;
                mode = CopyStateMode.CLONE;
                ClientManager.onEditingStateUpdate();
                yield true;
            }
            case LINE_STACK_MODE -> {
                if (action != InputAction.BEGIN) yield false;
                if (movementInPlane != null || stackSpacingInPlane != null || volumeStackSizeInPlane != null) yield false;
                mode = CopyStateMode.LINE_STACK;
                ClientManager.onEditingStateUpdate();
                yield true;
            }
            case VOLUME_STACK_MODE -> {
                if (action != InputAction.BEGIN) yield false;
                if (movementInPlane != null || stackSpacingInPlane != null || volumeStackSizeInPlane != null) yield false;
                mode = CopyStateMode.VOLUME_STACK;
                ClientManager.onEditingStateUpdate();
                yield true;
            }
            case MOVE_TO_TARGET, MOVE_TO_TARGET_SURFACE -> {
                if (action != InputAction.BEGIN) yield false;
                if (movementInPlane != null || stackSpacingInPlane != null || volumeStackSizeInPlane != null) yield false;
                var surface = ClientConfig.getTargetSurface(binding == ModInput.MOVE_TO_TARGET_SURFACE);
                var pos = RaycastUtil.raycastOrAtBlockReach(
                    player,
                    ClientConfig.Persistent.getMaxRaycastRange(),
                    surface,
                    ClientConfig.Local.getTargetFluids(),
                    RaycastContext.ShapeType.OUTLINE
                );
                var outerBounds = selection.getOuterBounds();
                var move = pos.subtract(outerBounds.getCenter().withY(outerBounds.getMinY()));
                selection.move(move);
                delta = delta.add(move);
                destDim = player.getWorld().getRegistryKey();
                ClientManager.onEditingStateUpdate();
                yield true;
            }
            case CUT, CUT_WITH_AIR, COPY, COPY_WITH_AIR -> {
                if (action != InputAction.BEGIN) yield false;
                if (movementInPlane != null || stackSpacingInPlane != null || volumeStackSizeInPlane != null) yield false;
                var cut = binding == ModInput.CUT || binding == ModInput.CUT_WITH_AIR;
                if (isCutMode != cut) {
                    isCutMode = cut;
                    ClientManager.onEditingStateUpdate();
                    yield true;
                } else if (mode == CopyStateMode.CLONE && delta.equals(Vec3i.ZERO)) {
                    var ignoreAir = ClientConfig.getIgnoreAir(binding == ModInput.CUT || binding == ModInput.COPY);
                    ClientManager.copyRegion(client, selection, player.clientWorld.getRegistryKey(), cut, ignoreAir ? Filter.DENY_AIR : Filter.ALLOW_ALL);
                    ClientManager.switchEditingState(new NormalState());
                    yield true;
                } else {
                    yield false;
                }
            }
            case CONFIRM, CONFIRM_ALTERNATIVE, PASTE, PASTE_SURFACE, PASTE_ALTERNATIVE, PASTE_ALTERNATIVE_SURFACE -> {
                if (action != InputAction.BEGIN) yield false;
                if (movementInPlane != null) {
                    var offset = movementInPlane.consumeOffset();
                    if (!offset.equals(Vec3i.ZERO)) {
                        selection.move(offset);
                        delta = delta.add(offset);
                        ClientManager.onEditingStateUpdate();
                    }
                    movementInPlane = null;
                    yield true;
                } else if (stackSpacingInPlane != null) {
                    var offset = stackSpacingInPlane.consumeOffset();
                    if (!offset.equals(Vec3i.ZERO)) {
                        if (mode == CopyStateMode.LINE_STACK) {
                            lineStackSpacing = lineStackSpacing.add(offset);
                            ClientManager.onEditingStateUpdate();
                        } else {
                            var newSpacing = changeVolumeStackSpacing(volumeStackSpacing, volumeStackSize, selection.getOuterBounds().getDimensions(), offset);
                            if (volumeStackSpacing.equals(newSpacing)) yield false;
                            volumeStackSpacing = newSpacing;
                            ClientManager.onEditingStateUpdate();
                        }
                    }
                    stackSpacingInPlane = null;
                    yield true;
                } else if (volumeStackSizeInPlane != null) {
                    var size = selection.getOuterBounds().getDimensions().add(volumeStackSpacing);
                    var offset = VecUtil.componentWiseDivision(volumeStackSizeInPlane.getCurrentOffset(), size);
                    if (!offset.equals(Vec3i.ZERO)) {
                        volumeStackSize = volumeStackSize.add(offset);
                        ClientManager.onEditingStateUpdate();
                    }
                    volumeStackSizeInPlane = null;
                    yield true;
                }
                var andContinue =
                    binding == ModInput.PASTE
                    || binding == ModInput.PASTE_SURFACE
                    || binding == ModInput.PASTE_ALTERNATIVE
                    || binding == ModInput.PASTE_ALTERNATIVE_SURFACE;
                var ignoreAir = ClientConfig.getIgnoreAir(
                    binding == ModInput.CONFIRM
                    || binding == ModInput.PASTE
                    || binding == ModInput.PASTE_SURFACE
                );
                var sourceFilter = ClientConfig.getIgnoreAir(ignoreAir) ? Filter.DENY_AIR : Filter.ALLOW_ALL;
                switch (mode) {
                    case CLONE -> ClientManager.cloneRegion(selection, sourceDim, destDim, delta, isCutMode, sourceFilter, Filter.ALLOW_ALL);
                    case LINE_STACK -> ClientManager.lineStackRegion(selection, sourceDim, destDim, delta, isCutMode, sourceFilter, Filter.ALLOW_ALL, lineStackSize, lineStackSpacing);
                    case VOLUME_STACK -> ClientManager.volumeStackRegion(selection, sourceDim, destDim, delta, isCutMode, sourceFilter, Filter.ALLOW_ALL, volumeStackSize, volumeStackSpacing);
                }
                if (andContinue) {
                    var keepDelta = binding == ModInput.PASTE_SURFACE || binding == ModInput.PASTE_ALTERNATIVE_SURFACE;
                    var newSelection = keepDelta ? selection.copyAndOffset(delta) : selection;
                    var newDelta = keepDelta ? delta : BlockPos.ORIGIN;
                    ClientManager.switchEditingState(new CopyState(client, destDim, newSelection, isCutMode, newDelta));
                } else {
                    ClientManager.switchEditingState(new NormalState());
                }
                yield true;
            }
            case CANCEL -> {
                if (action != InputAction.BEGIN) yield false;
                if (movementInPlane != null || stackSpacingInPlane != null || volumeStackSizeInPlane != null) {
                    movementInPlane = null;
                    stackSpacingInPlane = null;
                    volumeStackSizeInPlane = null;
                    yield true;
                } else {
                    ClientManager.switchEditingState(new NormalState());
                    yield true;
                }
            }
            case MOVE_FORWARDS, MOVE_BACKWARDS, MOVE_RIGHT, MOVE_LEFT -> {
                if (action != InputAction.BEGIN && action != InputAction.CONTINUOUS) yield false;
                if (movementInPlane != null || stackSpacingInPlane != null || volumeStackSizeInPlane != null) yield false;
                if (binding.inherentDirection == null) yield false;

                Vec3i offset = VecUtil.scrollVector(player, binding.inherentDirection).multiply(repeats);
                if (offset.equals(Vec3i.ZERO)) yield false;
                selection.move(offset);
                delta = delta.add(offset);
                ClientManager.onEditingStateUpdate();
                yield true;
            }
            case MOVE_IN_PLANE -> {
                if (stackSpacingInPlane != null || volumeStackSizeInPlane != null) yield false;
                if (action == InputAction.END && ClientConfig.Persistent.getMoveInPlaneReleaseCancel()) {
                    if (movementInPlane == null) yield false;
                    movementInPlane = null;
                    yield true;
                } else if (action == InputAction.BEGIN) {
                    movementInPlane = MovementInPlane.create(player, getOuterBounds());
                    yield true;
                } else {
                    yield false;
                }
            }
            case CHANGE_STACK_SPACING_IN_PLANE -> {
                if (mode != CopyStateMode.LINE_STACK && mode != CopyStateMode.VOLUME_STACK) yield false;
                if (movementInPlane != null || volumeStackSizeInPlane != null) yield false;
                if (action == InputAction.END && ClientConfig.Persistent.getMoveInPlaneReleaseCancel()) {
                    if (stackSpacingInPlane == null) yield false;
                    stackSpacingInPlane = null;
                    yield true;
                } else if (action == InputAction.BEGIN) {
                    stackSpacingInPlane = MovementInPlane.create(player, getOuterBounds());
                    yield true;
                } else {
                    yield false;
                }
            }
            case CHANGE_VOLUME_STACK_SIZE_IN_PLANE -> {
                if (mode != CopyStateMode.VOLUME_STACK) yield false;
                if (movementInPlane != null || stackSpacingInPlane != null) yield false;
                if (action == InputAction.END && ClientConfig.Persistent.getMoveInPlaneReleaseCancel()) {
                    if (volumeStackSizeInPlane == null) yield false;
                    volumeStackSizeInPlane = null;
                    yield true;
                } else if (action == InputAction.BEGIN) {
                    volumeStackSizeInPlane = MovementInPlane.create(player, getOuterBounds());
                    yield true;
                } else {
                    yield false;
                }
            }
            case INCREASE_LINE_STACK_SIZE, DECREASE_LINE_STACK_SIZE -> {
                if (action != InputAction.BEGIN && action != InputAction.CONTINUOUS) yield false;
                if (movementInPlane != null || stackSpacingInPlane != null || volumeStackSizeInPlane != null) yield false;
                if (mode != CopyStateMode.LINE_STACK) yield false;
                if (lineStackSpacing.equals(BlockPos.ORIGIN)) yield true; // still consume action

                int amount = repeats;
                if (lineStackSizeReverseScroll) amount *= -1;
                if (binding == ModInput.DECREASE_LINE_STACK_SIZE) amount *= -1;

                lineStackSize += amount;
                if (lineStackSize < 2) {
                    lineStackSizeReverseScroll = !lineStackSizeReverseScroll;
                    lineStackSize = 1 + (2 - lineStackSize);
                    lineStackSpacing = lineStackSpacing.multiply(-1);
                }

                ClientManager.onEditingStateUpdate();
                yield true;
            }
            case CHANGE_VOLUME_STACK_SIZE_FORWARDS, CHANGE_VOLUME_STACK_SIZE_BACKWARDS, CHANGE_VOLUME_STACK_SIZE_RIGHT, CHANGE_VOLUME_STACK_SIZE_LEFT -> {
                if (action != InputAction.BEGIN && action != InputAction.CONTINUOUS) yield false;
                if (movementInPlane != null || stackSpacingInPlane != null || volumeStackSizeInPlane != null) yield false;
                if (mode != CopyStateMode.VOLUME_STACK) yield false;
                if (binding.inherentDirection == null) yield false;

                Vec3i vec = VecUtil.scrollVector(player, binding.inherentDirection).multiply(repeats);
                if (vec.equals(Vec3i.ZERO)) yield false;

                volumeStackSize = volumeStackSize.add(vec);
                ClientManager.onEditingStateUpdate();
                yield true;
            }
            case CHANGE_STACK_SPACING_FORWARDS, CHANGE_STACK_SPACING_BACKWARDS, CHANGE_STACK_SPACING_RIGHT, CHANGE_STACK_SPACING_LEFT -> {
                if (action != InputAction.BEGIN && action != InputAction.CONTINUOUS) yield false;
                if (movementInPlane != null || stackSpacingInPlane != null || volumeStackSizeInPlane != null) yield false;
                if (mode != CopyStateMode.LINE_STACK && mode != CopyStateMode.VOLUME_STACK) yield false;
                if (binding.inherentDirection == null) yield false;

                Vec3i vec = VecUtil.scrollVector(player, binding.inherentDirection).multiply(repeats);
                if (vec.equals(Vec3i.ZERO)) yield false;

                if (mode == CopyStateMode.LINE_STACK) {
                    lineStackSpacing = lineStackSpacing.add(vec);
                    ClientManager.onEditingStateUpdate();
                } else {
                    var newSpacing = changeVolumeStackSpacing(volumeStackSpacing, volumeStackSize, selection.getOuterBounds().getDimensions(), vec);
                    if (volumeStackSpacing.equals(newSpacing)) yield false;
                    volumeStackSpacing = newSpacing;
                    ClientManager.onEditingStateUpdate();
                }
                yield true;
            }
            default -> false;
        };
    }

    @Override
    public void onClientTick(MinecraftClient client) {
        if (client.world != null) {
            if (destDim != client.world.getRegistryKey()) {
                movementInPlane = null;
                stackSpacingInPlane = null;
                volumeStackSizeInPlane = null;
            }
            destDim = client.world.getRegistryKey();
        }
    }

    @Override
    public void onRenderWorld(boolean isOtherPlayer, MinecraftClient client, MatrixStack matrices, Camera camera) {
        if (client.world == null) return;

        var selection = this.selection;
        var delta = this.delta;
        var lineStackSpacing = this.lineStackSpacing;
        var volumeStackSpacing = this.volumeStackSpacing;
        var volumeStackSize = this.volumeStackSize;
        if (!isOtherPlayer && client.player != null) {
            if (movementInPlane != null) {
                movementInPlane.update(client.player);
                var offset = movementInPlane.getCurrentOffset();
                selection = this.selection.copyAndOffset(offset);
                delta = delta.add(offset);
                movementInPlane.drawGrid(client, matrices, camera.getPos(), getOuterBounds(selection, lineStackSpacing, volumeStackSpacing, volumeStackSize), ClientConfig.Persistent.getSelectionColor(false));
            } else if (stackSpacingInPlane != null) {
                stackSpacingInPlane.update(client.player);
                var offset = stackSpacingInPlane.getCurrentOffset();
                switch (mode) {
                    case LINE_STACK -> {
                        lineStackSpacing = lineStackSpacing.add(offset);
                    }
                    case VOLUME_STACK -> {
                        volumeStackSpacing = changeVolumeStackSpacing(volumeStackSpacing, volumeStackSize, selection.getOuterBounds().getDimensions(), offset);
                    }
                }
                stackSpacingInPlane.drawGrid(client, matrices, camera.getPos(), getOuterBounds(selection, lineStackSpacing, volumeStackSpacing, volumeStackSize), ClientConfig.Persistent.getSelectionColor(false));
            } else if (volumeStackSizeInPlane != null) {
                volumeStackSizeInPlane.update(client.player);
                var size = selection.getOuterBounds().getDimensions().add(volumeStackSpacing);
                var offset = VecUtil.componentWiseDivision(volumeStackSizeInPlane.getCurrentOffset(), size);
                volumeStackSize = volumeStackSize.add(offset);
                volumeStackSizeInPlane.drawGrid(client, matrices, camera.getPos(), getOuterBounds(selection, lineStackSpacing, volumeStackSpacing, volumeStackSize), ClientConfig.Persistent.getSelectionColor(false));
            }
        }

        var origin = this.selection.copyAndOffset(this.delta.multiply(-1));
        if (client.world != null && client.world.getRegistryKey() == sourceDim) {
            RenderUtil.drawBox(
                client,
                matrices,
                camera.getPos(),
                BoxUtil.viewAdjustBox(BoxUtil.toBox(origin.getOuterBounds()), camera.getPos()),
                isCutMode
                    ? ClientConfig.Persistent.getRemoveBlocksColor(isOtherPlayer)
                    : ClientConfig.Persistent.getSelectionColor(isOtherPlayer)
            );
        }

        switch (mode) {
            case CLONE -> {
                if (!delta.equals(Vec3i.ZERO)) {
                    if (client.world != null && client.world.getRegistryKey() == destDim) {
                        RenderUtil.drawBox(
                            client,
                            matrices,
                            camera.getPos(),
                            BoxUtil.viewAdjustBox(BoxUtil.toBox(selection.getOuterBounds()), camera.getPos()),
                            ClientConfig.Persistent.getAddBlocksColor(isOtherPlayer)
                        );
                        if (region != null && region.volume <= ClientConfig.Persistent.getBlockRenderLimit()) {
                            RenderUtil.renderSingleRegion(client, camera, matrices, region, selection.getOuterBounds(), 1f, 1f, 1f);
                        }
                    }
                }
            }
            case LINE_STACK -> {
                if (client.world != null && client.world.getRegistryKey() == destDim) {
                    var sel = selection.getOuterBounds();
                    var bounds = StackUtil.lineStackBox(sel, lineStackSize, lineStackSpacing);
                    RenderUtil.drawBox(
                        client,
                        matrices,
                        camera.getPos(),
                        BoxUtil.viewAdjustBox(BoxUtil.toBox(bounds), camera.getPos()),
                        ClientConfig.Persistent.getSelectionColor(isOtherPlayer)
                    );
                    if (lineStackSize <= ClientConfig.Persistent.getLineRenderLimit()) {
                        StackUtil.lineStackIterate(sel, lineStackSize, lineStackSpacing, false, (index, box) -> {
                            RenderUtil.drawBox(
                                client,
                                matrices,
                                camera.getPos(),
                                BoxUtil.viewAdjustBox(BoxUtil.toBox(box), camera.getPos()),
                                ClientConfig.Persistent.getAddBlocksColor(isOtherPlayer)
                            );
                        });
                    }
                    if (region != null && region.volume * lineStackSize <= ClientConfig.Persistent.getBlockRenderLimit()) {
                        var camPos = camera.getPos();
                        BlockRenderManager manager = client.getBlockRenderManager();

                        var immediate = RenderUtil.preRenderRegion(client, camera, matrices, 1f, 1f, 1f);
                        StackUtil.lineStackIterate(sel, lineStackSize, lineStackSpacing, false, (index, box) -> {
                            RenderUtil.internalRenderRegion(client, camPos, matrices, immediate, manager, region, box);
                        });
                        RenderUtil.postRenderRegion(immediate);
                    }
                }
            }
            case VOLUME_STACK -> {
                if (client.world != null && client.world.getRegistryKey() == destDim) {
                    var sel = selection.getOuterBounds();
                    var bounds = StackUtil.volumeStackBox(sel, volumeStackSize, volumeStackSpacing);
                    RenderUtil.drawBox(
                        client,
                        matrices,
                        camera.getPos(),
                        BoxUtil.viewAdjustBox(BoxUtil.toBox(bounds), camera.getPos()),
                        ClientConfig.Persistent.getSelectionColor(isOtherPlayer)
                    );
                    var stackSizeVolume = (Math.abs(volumeStackSize.getX()) + 1) * (Math.abs(volumeStackSize.getY()) + 1) * (Math.abs(volumeStackSize.getZ()) + 1);
                    if (stackSizeVolume <= ClientConfig.Persistent.getLineRenderLimit()) {
                        StackUtil.volumeStackIterate(sel, volumeStackSize, volumeStackSpacing, false, (index, box) -> {
                            RenderUtil.drawBox(
                                client,
                                matrices,
                                camera.getPos(),
                                BoxUtil.viewAdjustBox(BoxUtil.toBox(box), camera.getPos()),
                                ClientConfig.Persistent.getAddBlocksColor(isOtherPlayer)
                            );
                        });
                    }
                    if (region == null) break;
                    if (region.volume * stackSizeVolume <= ClientConfig.Persistent.getBlockRenderLimit()) {
                        var camPos = camera.getPos();
                        BlockRenderManager manager = client.getBlockRenderManager();

                        var immediate = RenderUtil.preRenderRegion(client, camera, matrices, 1f, 1f, 1f);
                        StackUtil.volumeStackIterate(sel, volumeStackSize, volumeStackSpacing, false, (index, box) -> {
                            RenderUtil.internalRenderRegion(client, camPos, matrices, immediate, manager, region, box);
                        });
                        RenderUtil.postRenderRegion(immediate);
                    }
                }
            }
        }
    }

    @Override
    public EditingStateType getType() {
        return EditingStateType.COPY;
    }

    @Override
    public String getDebugText() {
        return "CopyState [" + mode.toString() + (isCutMode ? " CUT" : " COPY") + " delta: " + delta.toString() + " sourceDim: " + sourceDim.getValue() + " destDim: " + destDim.getValue() +
            (mode == CopyStateMode.LINE_STACK ? (" size: " + lineStackSize + " spacing: " + lineStackSpacing.toString()) : "") +
            (mode == CopyStateMode.VOLUME_STACK ? (" size: " + volumeStackSize.toString() + " spacing: " + volumeStackSpacing.toString()) : "") +
            (movementInPlane != null ? (" moving: " + movementInPlane.getCurrentOffset().toString()) : "") +
            (stackSpacingInPlane != null ? (" changing_spacing: " + stackSpacingInPlane.getCurrentOffset().toString()) : "") +
            (volumeStackSizeInPlane != null ? (" changing_stacking: " + volumeStackSizeInPlane.getCurrentOffset().toString()) : "") +
            " region: " + region + "]";
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
    public CommonEditingState convertToCommon() { return new CommonCopyState(sourceDim, selection, mode, isCutMode, destDim, delta, lineStackSize, lineStackSpacing, volumeStackSize, volumeStackSpacing); }
}
