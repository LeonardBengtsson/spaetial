package spaetial.editing;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import spaetial.ClientConfig;
import spaetial.editing.operation.OperationType;
import spaetial.editing.selection.CuboidSelection;
import spaetial.gui.Button;
import spaetial.gui.DialogScreen;
import spaetial.networking.message.Message;
import spaetial.networking.message.NoClipboardErrorMessage;
import spaetial.schematic.ClientSchematicPlacements;
import spaetial.Spaetial;
import spaetial.schematic.*;
import spaetial.editing.selection.Selection;
import spaetial.editing.state.EditingState;
import spaetial.gui.screen.PasteOptionsWheelScreen;
import spaetial.networking.c2s.*;
import spaetial.util.BoxUtil;
import spaetial.util.encoding.InvalidSignatureException;
import spaetial.util.hud.ScreenUtil;

import java.util.*;

/**
 * Manages the client's core editing functionality
 */
public final class ClientManager {
    private ClientManager() {}

    private static @Nullable EditingState previousEditingState = null;
    private static @NotNull EditingState editingState = EditingState.getDefault();
    private static @NotNull MirrorSettings mirrorSettings = new MirrorSettings();
    private static long inactiveTimeStamp = 0;

    private static @Nullable String connectionName = null;
    private static @NotNull ConnectionState connectionState = ConnectionState.NONE;

    public static @NotNull EditingState getEditingState() {
        return editingState;
    }

    public static void switchEditingState(@NotNull EditingState state) {
        previousEditingState = editingState;
        editingState = state;
        onEditingStateUpdate();
    }

    public static void onEditingStateUpdate() {
        resetInactiveTimer();
        if (ClientConfig.Persistent.shouldParticipateInMultiplayerCache()) {
            ClientPlayNetworking.send(new StateUpdateC2SPacket(editingState.convertToCommon()));
        }
    }

    public static void resetInactiveTimer() {
        inactiveTimeStamp = System.currentTimeMillis();
    }

    public static boolean isInactive() {
        return ClientConfig.Persistent.getInactiveTimeMillis() >= 0 && ClientConfig.Persistent.getInactiveTimeMillis() < System.currentTimeMillis() - inactiveTimeStamp;
    }

    public static @NotNull ConnectionState getConnectionState() {
        return connectionState;
    }

    public static void copyRegion(MinecraftClient client, Selection selection, RegistryKey<World> dim, boolean cut, Filter filter) {
        resetInactiveTimer();
        var id = UUID.randomUUID();
        ClientClipboardCache.prepareToReceive(client, id, null);
        ClientPlayNetworking.send(new RequestCopyOperationC2SPacket(selection, dim, cut, filter, id));
    }

    public static void cloneRegion(Selection selection, RegistryKey<World> sourceDim, RegistryKey<World> destDim, BlockPos delta, boolean move, Filter sourceFilter, Filter destinationFilter) {
        resetInactiveTimer();
        ClientPlayNetworking.send(new RequestCloneOperationC2SPacket(selection, sourceDim, destDim, delta, move, sourceFilter, destinationFilter));
    }

    public static void lineStackRegion(Selection selection, RegistryKey<World> sourceDim, RegistryKey<World> destDim, BlockPos delta, boolean move, Filter sourceFilter, Filter destinationFilter, int stackSize, BlockPos spacing) {
        resetInactiveTimer();
        ClientPlayNetworking.send(new RequestLineStackOperationC2SPacket(selection, sourceDim, destDim, delta, move, sourceFilter, destinationFilter, stackSize, spacing));
    }

    public static void volumeStackRegion(Selection selection, RegistryKey<World> sourceDim, RegistryKey<World> destDim, BlockPos delta, boolean move, Filter sourceFilter, Filter destinationFilter, Vec3i stackSize, BlockPos spacing) {
        resetInactiveTimer();
        ClientPlayNetworking.send(new RequestVolumeStackOperationC2SPacket(selection, sourceDim, destDim, delta, move, sourceFilter, destinationFilter, stackSize, spacing));
    }

    public static boolean pasteAction(MinecraftClient client, ClientPlayerEntity player, boolean surface) {
        boolean hasInGameClipboard = ClientClipboardCache.hasClipboardRegion();

        // TODO add support for more formats, including different file formats
        Schematic systemClipboard = null;
        try {
            systemClipboard = Schematic.decode(Base64.getDecoder().decode(client.keyboard.getClipboard().trim()));
        } catch (InvalidSignatureException | IllegalArgumentException ignored) {
        } catch (Throwable e) {
            Spaetial.warn("Error while decoding schematic", e);
        }

        if (systemClipboard != null && hasInGameClipboard) {
            ScreenUtil.openScreen(client, new PasteOptionsWheelScreen(systemClipboard));
            resetInactiveTimer();
            return true;
        } else if (systemClipboard != null) {
            ClientSchematicPlacements.addSchematicPlacement(player, surface, true, systemClipboard, true);
            resetInactiveTimer();
            return true;
        } else if (hasInGameClipboard){
            var result = ClientClipboardCache.placeClipboard(player, surface, true);
            if (result) resetInactiveTimer();
            return result;
        } else {
            ClientManager.receiveMessage(client, new NoClipboardErrorMessage());
            return true;
        }
    }

    public static void quickSet(ClientPlayerEntity player, BlockPos pos1, BlockPos pos2, Material material, Filter filter) {
        resetInactiveTimer();
        var selection = new CuboidSelection(BoxUtil.fromPositions(pos1, pos2));
        ClientPlayNetworking.send(new RequestReplaceOperationC2SPacket(selection, player.clientWorld.getRegistryKey(), filter, material));
    }

    public static void undo(MinecraftClient client) {
        if (!isInactive()) {
            resetInactiveTimer();
            ClientPlayNetworking.send(new RequestUndoC2SPacket());
        } else {
            openInactiveConfirmUndoRedoScreen(client, false);
        }
    }

    public static void redo(MinecraftClient client) {
        if (!isInactive()) {
            resetInactiveTimer();
            ClientPlayNetworking.send(new RequestRedoC2SPacket());
        } else {
            openInactiveConfirmUndoRedoScreen(client, true);
        }
    }

    public static void receiveMessage(MinecraftClient client, Message message) {
        if (client.player == null) return;
        // TODO integrate into mod overlay
        switch (message.getType()) {
            case VOLUME_LIMIT_REACHED_ERROR, INSUFFICIENT_PERMISSIONS_ERROR, NOTHING_TO_UNDO_ERROR, NOTHING_TO_REDO_ERROR,
                NO_CLIPBOARD_ERROR, REGION_REQUEST_TIMED_OUT_ERROR
            -> {
                client.player.sendMessage(message.getTranslation(), true);
            }
            case REQUESTED_REGION_DIMENSION_DOESNT_EXIST_ERROR, SCHEMATIC_DOESNT_EXIST_ERROR, OUTDATED_REQUEST_ERROR -> {
                // ignore for now
            }
        }
    }

    @Deprecated
    private static void openInactiveConfirmUndoRedoScreen(MinecraftClient client, boolean redo) {
        var title = Spaetial.translate("gui", "screen.confirm_action.title", redo ? "redo" : "undo");
        var cancelButton = new Button(
            Spaetial.translate("gui", "cancel"),
            true,
            () -> client.setScreen(null),
            Button.ColorScheme.GRAY,
            Button.DrawMode.OUTLINE
        );
        var confirmButton = new Button(
            Spaetial.translate("gui", "confirm"),
            false,
            () -> {
                resetInactiveTimer();
                ClientPlayNetworking.send(
                    redo
                        ? new RequestRedoC2SPacket()
                        : new RequestUndoC2SPacket()
                );
                client.setScreen(null);
            },
            Button.ColorScheme.PRIMARY,
            Button.DrawMode.FILLED_WHEN_HOVERED
        );
        client.setScreen(new DialogScreen(
            100, false, true, 1, ClientManager::resetInactiveTimer,
            title, null, null,
            new Button[]{ cancelButton }, new Button[]{ confirmButton }
        ));
    }

    @Deprecated
    public static void openConfirmOperationActionScreen(MinecraftClient client, int volume, int maxVolume, OperationAction action, @Nullable OperationType operationType, boolean showOperatorWarning) {
        assert !(operationType == null && (action == OperationAction.EXECUTE || action == OperationAction.EXECUTE_WITHOUT_HISTORY));
        boolean isSkippable = action == OperationAction.EXECUTE;

        var whiteStyle = Style.EMPTY.withColor(Formatting.WHITE);
        var grayStyle = Style.EMPTY.withColor(Formatting.GRAY);
        var redTextStyle = Style.EMPTY.withColor(Formatting.RED);

        var title = switch (action) {
            case EXECUTE, EXECUTE_WITHOUT_HISTORY -> Spaetial.translateWithArgs(
                    "gui",
                    "screen.confirm_action.title.operation",
                    operationType.getTranslation()
                ).setStyle(whiteStyle);
            case UNDO -> Spaetial.translate("gui", "screen.confirm_action.title.undo")
                .setStyle(whiteStyle);
            case REDO -> Spaetial.translate("gui", "screen.confirm_action.title.redo")
                .setStyle(whiteStyle);
        };

        var table = new Text[][] {
            new Text[] {
                Spaetial.translateWithArgs("gui", "screen.confirm_action.max_volume").setStyle(whiteStyle),
                Text.literal(String.format("%,d", maxVolume)).setStyle(whiteStyle)
            },
            new Text[] {
                Spaetial.translateWithArgs("gui", "screen.confirm_action.volume").setStyle(whiteStyle),
                Text.literal(String.format("%,d", volume)).setStyle(redTextStyle)
            }
        };

        var content = new Text[(showOperatorWarning ? 1 : 0) + (isSkippable ? 1 : 0)];
        if (isSkippable) {
            content[0] = Spaetial.translate("gui", "screen.confirm_action.is_skippable").setStyle(grayStyle.withItalic(true));
        }
        if (showOperatorWarning) {
            content[isSkippable ? 1 : 0] = Spaetial.translate("gui", "screen.confirm_action.op_warning").setStyle(redTextStyle);
        }

        var cancelButton = new Button(
            Spaetial.translate("gui", "cancel"),
            true,
            () -> client.setScreen(null),
            Button.ColorScheme.GRAY,
            Button.DrawMode.OUTLINE
        );

        var rightAlignedButtons = new Button[isSkippable ? 2 : 1];
        if (isSkippable) {
            rightAlignedButtons[0] = new Button(
                Spaetial.translate("gui", "screen.confirm_action.dont_save"),
                false,
                () -> {
                    resetInactiveTimer();
                    ClientPlayNetworking.send(new ConfirmOperationActionC2SPacket(true));
                    client.setScreen(null);
                },
                Button.ColorScheme.GRAY,
                Button.DrawMode.OUTLINE
            );
        }
        rightAlignedButtons[isSkippable ? 1 : 0] = new Button(
            Spaetial.translate("gui", "confirm"),
            false,
            () -> {
                resetInactiveTimer();
                ClientPlayNetworking.send(new ConfirmOperationActionC2SPacket(false));
                client.setScreen(null);
            },
            Button.ColorScheme.PRIMARY,
            Button.DrawMode.FILLED_WHEN_HOVERED
        );

        Runnable cancelAction;
        if (action == OperationAction.UNDO || action == OperationAction.REDO) {
            cancelAction = () -> {};
        } else {
            cancelAction = () -> {
                if (previousEditingState != null) {
                    switchEditingState(previousEditingState);
                }
                previousEditingState = null;
            };
        }

        client.setScreen(new DialogScreen(
            100,
            false,
            true,
            isSkippable ? 2 : 1,
            cancelAction,
            title,
            table,
            content,
            new Button[] {cancelButton},
            rightAlignedButtons
        ));
    }

    public enum ConnectionState {
        NONE,
        LOCAL,
        SERVER,
        @Deprecated
        REALMS
    }

    public static void onTick(MinecraftClient client) {
        editingState.onClientTick(client);
    }

    public static void onJoinWorld(ClientPlayNetworkHandler handler, MinecraftClient client) {
        if (client.isIntegratedServerRunning()) {
            assert client.getServer() != null;
            connectionName = client.getServer().getSavePath(WorldSavePath.ROOT).getParent().getFileName().toString();
            connectionState = ConnectionState.LOCAL;
        } else {
            // might be of use: client.server.isRemote()
            assert handler != null && handler.getServerInfo() != null;
            connectionName = handler.getServerInfo().address;
            connectionState = ConnectionState.SERVER;
        }

        // TODO LOAD DATA
        if (false /* data found on file */) {
            // load schematic placements
            // load clipboard
        } else {
            ClientSchematicPlacements.clear();
            switchEditingState(EditingState.getDefault());
            mirrorSettings = new MirrorSettings();
        }
        // TODO remove if redundant
        resetInactiveTimer();
    }

    public static void onLeaveWorld(ClientPlayNetworkHandler handler, MinecraftClient client) {
        // TODO SAVE DATA

        editingState = EditingState.getDefault();
        mirrorSettings = new MirrorSettings();

        connectionName = null;
        connectionState = ConnectionState.NONE;
    }
}
