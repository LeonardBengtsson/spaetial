package spaetial.editing.state;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import spaetial.ClientConfig;
import spaetial.Spaetial;
import spaetial.editing.ClientManager;
import spaetial.editing.Filter;
import spaetial.editing.Material;
import spaetial.editing.OperationAction;
import spaetial.editing.operation.OperationType;
import spaetial.editing.state.common.CommonNormalState;
import spaetial.gui.screen.QuickConfigScreen;
import spaetial.gui.screen.SchematicsMenuScreen;
import spaetial.gui.screen.ShapeWheelScreen;
import spaetial.input.InputAction;
import spaetial.input.ModInput;
import spaetial.render.RenderUtil;
import spaetial.server.editing.ServerManager;
import spaetial.util.BoxUtil;
import spaetial.util.color.ColorUtil;
import spaetial.util.math.RaycastUtil;

import java.awt.*;
import java.util.List;

/**
 * Represents the mod's default state when not doing any particular action, but still being active and showing all information on screen
 *
 * <p><p>Inputs:
 * <ul>
 *     <li><b>Alt LMB:</b> Start cuboid selection ({@link CuboidSelectionState})</li>
 *     <li><b>Ctrl Z:</b> Undo latest operation</li>
 *     <li><b>Ctrl Alt Z:</b> Redo operation</li>
 *     <li><b>X:</b> Open the {@link ShapeWheelScreen} to create various 3D shapes</li>
 *     <li><b>C:</b> Open the {@link SchematicsMenuScreen} to view all schematics on file and placed schematics in game</li>
 *     <li><b>Ctrl V:</b> Paste schematic from system clipboard or in-game clipboard</li>
 *     <li><b>B:</b> Open {@link QuickConfigScreen}</li>
 *     <li><b>H:</b> Hold to select a region of blocks to replace with your held item</li>
 * </ul>
 *
 * @see EditingState
 * @see CuboidSelectionState
 * @see ServerManager#requestUndo(MinecraftServer, ServerPlayerEntity)
 * @see ServerManager#requestRedo(MinecraftServer, ServerPlayerEntity)
 * @see ShapeWheelScreen
 * @see SchematicsMenuScreen
 * @see ClientManager#pasteAction(MinecraftClient, ClientPlayerEntity, boolean)
 * @see QuickConfigScreen
 */
public class NormalState extends EditingState {
    private @Nullable QuickSetInfo quickSetInfo = null;

    public NormalState() {}

    protected NormalState(RegistryKey<World> quickSetDim, BlockPos quickSetPos1, BlockPos quickSetPos2, boolean quickSetReplace) {
        if (quickSetPos1 == null || quickSetPos2 == null) {
            this.quickSetInfo = null;
        } else {
            this.quickSetInfo = new QuickSetInfo(quickSetDim, quickSetPos1, quickSetPos2, quickSetReplace, null);
        }
    }

    private static final List<ModInput> INPUTS = List.of(
        ModInput.DEBUG_1, ModInput.DEBUG_2, ModInput.DEBUG_3,

        ModInput.UNDO,
        ModInput.REDO,

        ModInput.PASTE, ModInput.PASTE_SURFACE,

        ModInput.QUICK_REPLACE, ModInput.QUICK_REPLACE_SURFACE, ModInput.QUICK_SET, ModInput.QUICK_SET_SURFACE,
        ModInput.CONFIRM,
        ModInput.CANCEL,

        ModInput.CUBOID_SELECTION, ModInput.CUBOID_SELECTION_SURFACE
    );

    @Override
    public List<ModInput> getApplicableInputs() { return INPUTS; }

    @Override
    public boolean onInput(MinecraftClient client, ClientPlayerEntity player, ModInput binding, InputAction action, int repeats) {
        return switch (binding) {
            case UNDO -> {
                if (quickSetInfo != null) yield false;
                if (action != InputAction.BEGIN) yield false;
                ClientManager.undo(client);
                yield true;
            }
            case REDO -> {
                if (quickSetInfo != null) yield false;
                if (action != InputAction.BEGIN) yield false;
                ClientManager.redo(client);
                yield true;
            }
            case PASTE, PASTE_SURFACE -> {
                if (quickSetInfo != null) yield false;
                if (action != InputAction.BEGIN) yield false;
                boolean surface = ClientConfig.getTargetSurface(binding == ModInput.PASTE_SURFACE);
                yield ClientManager.pasteAction(client, player, surface);
            }
            case CUBOID_SELECTION, CUBOID_SELECTION_SURFACE -> {
                if (quickSetInfo != null) yield false;
                if (action != InputAction.BEGIN) yield false;
                if (client.world == null) yield false;
                boolean surface = ClientConfig.getTargetSurface(binding == ModInput.CUBOID_SELECTION_SURFACE);
                BlockPos pos = RaycastUtil.raycastOrCurrentPos(player, ClientConfig.Persistent.getMaxRaycastRange(), surface, false, RaycastContext.ShapeType.OUTLINE);
                ClientManager.switchEditingState(new CuboidSelectionState(pos, client.world.getRegistryKey()));
                yield true;
            }
            case CANCEL -> {
                if (action != InputAction.BEGIN) yield false;
                if (quickSetInfo == null) yield false;
                quickSetInfo = null;
                yield true;
            }
            case QUICK_SET, QUICK_SET_SURFACE, QUICK_REPLACE, QUICK_REPLACE_SURFACE -> {
                if (action == InputAction.END) {
                    if (quickSetInfo == null) yield false;
                    if (!ClientConfig.Persistent.getQuickSetReleaseCancel()) yield false;
                    quickSetInfo = null;
                    yield true;
                } else if (action == InputAction.BEGIN) {
                    boolean surface = binding == ModInput.QUICK_SET_SURFACE || binding == ModInput.QUICK_REPLACE_SURFACE;
                    var pos = RaycastUtil.raycastOrAtBlockReach(
                        player,
                        ClientConfig.Persistent.getMaxRaycastRange(),
                        ClientConfig.getTargetSurface(surface),
                        ClientConfig.Local.getTargetFluids(),
                        RaycastContext.ShapeType.OUTLINE
                    );
                    boolean replace = binding == ModInput.QUICK_REPLACE || binding == ModInput.QUICK_REPLACE_SURFACE;
                    var material = Material.fromMainhandItem(player, false);
                    quickSetInfo = new QuickSetInfo(player.getWorld().getRegistryKey(), pos, pos, ClientConfig.getIgnoreAir(replace), material);
                    yield true;
                } else {
                    yield false;
                }
            }
            case CONFIRM -> {
                if (action != InputAction.BEGIN) yield false;
                if (quickSetInfo == null) yield false;
                if (client.world != null && quickSetInfo.dim != client.world.getRegistryKey()) yield false;
                if (quickSetInfo.material == null) {
                    quickSetInfo.material = Material.fromMainhandItem(player, false);
                }
                if (quickSetInfo.material != null) {
                    var filter = quickSetInfo.replaceMode ? Filter.DENY_AIR : Filter.ALLOW_ALL;
                    ClientManager.quickSet(player, quickSetInfo.pos1, quickSetInfo.pos2, quickSetInfo.material, filter);
                }
                quickSetInfo = null;
                yield true;
            }
            case DEBUG_1 -> {
                if (quickSetInfo != null) yield false;
                if (action != InputAction.BEGIN) yield false;
                ClientConfig.Local.DEBUG_toggleSuppressPlayerUpdates(!ClientConfig.Local.getSuppressPlayerUpdates());
                client.inGameHud.setOverlayMessage(Text.literal("Update suppression toggled " + (ClientConfig.Local.getSuppressPlayerUpdates() ? "ON" : "OFF")), false);
                Spaetial.info("Update suppression toggled " + (ClientConfig.Local.getSuppressPlayerUpdates() ? "ON" : "OFF"));
                ClientConfig.sendUpdatePacket();
                yield true;
            }
            case DEBUG_2 -> {
                if (quickSetInfo != null) yield false;
                if (action != InputAction.BEGIN) yield false;
                ClientManager.openConfirmOperationActionScreen(client, 130000, 4131673, OperationAction.EXECUTE, OperationType.COMPLETE_SCHEMATIC, true);
                yield true;
            }
            default -> false;
        };
    }

    @Override
    public void onClientTick(MinecraftClient client) {
        if (client.world == null || (quickSetInfo != null && quickSetInfo.dim != client.world.getRegistryKey())) {
            quickSetInfo = null;
        }
    }

    @Override
    public void onRenderWorld(boolean isOtherPlayer, MinecraftClient client, MatrixStack matrices, Camera camera) {
        {
            // TODO REMOVE DEBUG BLOCK

//            var hit = RaycastUtil.raycast(player, 50, false, RaycastContext.ShapeType.OUTLINE);
//            if (hit.getType() == HitResult.Type.BLOCK) {
//                var camPos = camera.getPos();
//                var dir = hit.getSide();
//                var axis = dir.getAxis();
//                var centerPos = PosUtil.roundAxis(hit.getPos(), axis).add(new Vec3d(dir.getUnitVector()).multiply(.01));
//
//                RenderSystem.disableDepthTest();
//                RenderUtil.drawFadingGrid(
//                    client,
//                    matrices,
//                    camPos,
//                    centerPos,
//                    axis,
//                    12,
//                    Color.MAGENTA
//                );
//                RenderSystem.enableDepthTest();
//            }
        }

        {
            // TODO REMOVE DEBUG BLOCK

//            var hit = RaycastUtil.raycastPlane(player, new Vec3d(0, 100, 0), new Vec3d(0, 1, 0), RaycastUtil.HitBehindBehaviour.MISS);
//
//            if (hit != null) {
//                var pos = PosUtil.floor(hit).toCenterPos();
//                double a = .05;
//                var vecs = new Vec3d[] {
//                    new Vec3d(a, a, a),
//                    new Vec3d(a, a, -a),
//                    new Vec3d(a, -a, a),
//                    new Vec3d(a, -a, -a),
//                    new Vec3d(-a, a, a),
//                    new Vec3d(-a, a, -a),
//                    new Vec3d(-a, -a, a),
//                    new Vec3d(-a, -a, -a),
//                };
//                for (var v : vecs) {
//                    RenderUtil.drawFading3dCross(
//                        client,
//                        matrices,
//                        camera.getPos(),
//                        pos.add(v),
//                        1.2,
//                        Color.WHITE
//                    );
//                }
//                RenderUtil.drawFading3dCross(
//                    client,
//                    matrices,
//                    camera.getPos(),
//                    pos,
//                    1.2,
//                    Color.BLACK
//                );
//                RenderUtil.drawSphereQuads(
//                    client,
//                    matrices,
//                    camera.getPos(),
//                    false, pos,
//                    .45,
//                    20,
//                    new Color(0x11ffffff, true)
//                );
//            }
        }
        if (isOtherPlayer) {
            if (quickSetInfo == null) return;
            var color = quickSetInfo.replaceMode
                ? ClientConfig.Persistent.getReplaceBlocksColor(false)
                : ClientConfig.Persistent.getAddBlocksColor(false);
            RenderUtil.drawBox(
                client,
                matrices,
                camera.getPos(),
                BoxUtil.viewAdjustBox(BoxUtil.toBox(BoxUtil.fromPositions(quickSetInfo.pos1, quickSetInfo.pos2)), camera.getPos()),
                color
            );
        } else if (quickSetInfo != null) {
            boolean qs = ModInput.QUICK_SET.isPressingModifierKeys(true);;
            boolean qss = ModInput.QUICK_SET_SURFACE.isPressingModifierKeys(true);
            boolean qr = ModInput.QUICK_REPLACE.isPressingModifierKeys(true);
            boolean qrs = ModInput.QUICK_REPLACE_SURFACE.isPressingModifierKeys(true);
            if (qs || qss || qr || qrs) {
                boolean surface = qss || qrs;
                var pos = RaycastUtil.raycastOrAtBlockReach(
                    client.player,
                    ClientConfig.Persistent.getMaxRaycastRange(),
                    ClientConfig.getTargetSurface(surface),
                    ClientConfig.Local.getTargetFluids(),
                    RaycastContext.ShapeType.OUTLINE
                );
                quickSetInfo.pos2 = pos;
                quickSetInfo.replaceMode = ClientConfig.getIgnoreAir(!(qs || qss));
                ClientManager.onEditingStateUpdate();
            }


            if (ModInput.CONFIRM.isPressingModifierKeys(true)) {
                Color color;
                if (client.player != null && client.player.getMainHandStack().isOf(Items.AIR)) {
                    color = ClientConfig.Persistent.getRemoveBlocksColor(false);
                } else {
                    color = quickSetInfo.replaceMode
                        ? ClientConfig.Persistent.getReplaceBlocksColor(false)
                        : ClientConfig.Persistent.getAddBlocksColor(false);
                }
                RenderUtil.drawBox(
                    client,
                    matrices,
                    camera.getPos(),
                    BoxUtil.viewAdjustBox(BoxUtil.toBox(BoxUtil.fromPositions(quickSetInfo.pos1, quickSetInfo.pos2)), camera.getPos()),
                    color
                );
            }
        } else {
            boolean cs = ModInput.CUBOID_SELECTION.isPressingModifierKeys(true);
            boolean css = ModInput.CUBOID_SELECTION_SURFACE.isPressingModifierKeys(true);
            if (cs || css) {
                boolean surface = css;
                BlockPos pos = RaycastUtil.raycastOrCurrentPos(client.player, ClientConfig.Persistent.getMaxRaycastRange(), ClientConfig.getTargetSurface(surface), false, RaycastContext.ShapeType.OUTLINE);
                RenderUtil.drawBox(
                    client,
                    matrices,
                    camera.getPos(),
                    BoxUtil.viewAdjustBox(
                        BoxUtil.toBox(new BlockBox(pos)),
                        camera.getPos()
                    ),
                    ColorUtil.withPreviewAlpha(ClientConfig.Persistent.getSelectionColor(false))
                );
            }
        }
    }

    @Override
    public EditingStateType getType() {
        return EditingStateType.NORMAL;
    }

    @Override
    public String getDebugText() {
        var s = new StringBuilder("NormalState[");
        if (quickSetInfo != null) {
            s.append("quick_set: (");
            s.append(quickSetInfo.replaceMode ? "REPLACE" : "SET");
            s.append(" pos1: ");
            s.append(quickSetInfo.pos1);
            s.append(" pos2: ");
            s.append(quickSetInfo.pos2);
            s.append(" material: ");
            if (quickSetInfo.material == null) {
                s.append("null");
            } else {
                s.append('(');
                s.append(quickSetInfo.material.getDebugText());
                s.append(')');
            }
            s.append(']');
        }
        s.append(']');
        return s.toString();
    }

    @Override
    public Text getInfoText() {
        return Text.empty();
    }

    @Override
    public InteractionType getInteractionType() {
        return InteractionType.ON;
    }

    @Override
    public CommonNormalState convertToCommon() {
        if (quickSetInfo == null) {
            return new CommonNormalState(null, null, null, false);
        }
        return new CommonNormalState(quickSetInfo.dim, quickSetInfo.pos1, quickSetInfo.pos2, quickSetInfo.replaceMode);
    }

    private static class QuickSetInfo {
        public final @NotNull RegistryKey<World> dim;
        public final @NotNull BlockPos pos1;
        public @NotNull BlockPos pos2;
        public boolean replaceMode;
        public @Nullable Material material;

        public QuickSetInfo(RegistryKey<World> dim, BlockPos pos1, BlockPos pos2, boolean replaceMode, Material material) {
            this.dim = dim;
            this.pos1 = pos1;
            this.pos2 = pos2;
            this.replaceMode = replaceMode;
            this.material = material;
        }
    }
}