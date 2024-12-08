package spaetial;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.Vec3i;
import org.jetbrains.annotations.Nullable;
import spaetial.editing.ClientClipboardCache;
import spaetial.input.*;
import spaetial.gui.screen.QuickConfigScreen;
import spaetial.schematic.ClientSchematicPlacements;
import spaetial.editing.ClientManager;
import spaetial.networking.ClientRegionRequest;
import spaetial.networking.MultiplayerCache;
import spaetial.render.RenderUtil;
import spaetial.util.BoxUtil;

import java.awt.*;
import java.util.List;
import java.util.Objects;

public final class ClientEvents {
    private ClientEvents() {}

    private static final List<ModInput> INPUTS = List.of(
        ModInput.TOGGLE_MOD,
        ModInput.CONFIG
    );

    public static final InputListener INPUT_LISTENER = new InputListener() {
        @Override
        public List<ModInput> getApplicableInputs() { return INPUTS; }
        @Override
        public boolean onInput(MinecraftClient client, ClientPlayerEntity player, ModInput binding, InputAction action, int repeats) {
            return ClientEvents.onInput(client, player, binding, action);
        }
    };

    /**
     * Runs at the start of every client tick
     */
    public static void onClientTick(MinecraftClient client) {
        ClientManager.onTick(client);
        ClientRegionRequest.onTick(client);
    }

    /**
     * Runs when it is time to render shapes and regions in the world
     */
    public static void onRender(MinecraftClient client, MatrixStack matrices, Camera camera) {
        if (client.world != null) {
            var editingState = ClientManager.getEditingState();
            var interactionType = editingState.getInteractionType();
            if (interactionType.renderOthersStates) {
                editingState.onRenderWorld(false, client, matrices, camera);
            }
            if (interactionType.renderSchematicPlacements) {
                ClientSchematicPlacements.iterateSchematicPlacements((id, placement) -> {
                    var region = placement.schematic.region();
                    if (region.volume > ClientConfig.Persistent.getBlockRenderLimit()) region = null;
                    var state = ClientManager.getEditingState();

                    BlockBox box;
                    Color color;
                    if (id == state.getActiveSchematicPlacementId()) {
                        var offset = Objects.requireNonNullElse(ClientManager.getEditingState().getActiveSchematicPlacementOffset(), Vec3i.ZERO);
                        box = BoxUtil.offset(placement.box, offset);
                        color = ClientConfig.Persistent.getSchematicColor(true);
                    } else {
                        box = placement.box;
                        color = ClientConfig.Persistent.getSchematicColor(false);
                    }

                    RenderUtil.renderRegionWithOutline(client, matrices, camera, box, region, color);
                });
                ClientSchematicPlacements.iterateOwnSharedSchematicPlacements((id, placement) -> {
                    var region = placement.schematicPlacement.schematic.region();
                    if (region.volume > ClientConfig.Persistent.getBlockRenderLimit()) region = null;
                    RenderUtil.renderRegionWithOutline(
                        client, matrices, camera, placement.schematicPlacement.box, region,
                        ClientConfig.Persistent.getSchematicColor(id == ClientManager.getEditingState().getActiveSchematicPlacementId())
                    );
                });
                ClientSchematicPlacements.iterateOthersSharedSchematicPlacements((id, placementInfo) -> {
                    var region = placementInfo.getRegion();
                    if (region != null && region.volume > ClientConfig.Persistent.getBlockRenderLimit()) region = null;
                    RenderUtil.renderRegionWithOutline(
                        client, matrices, camera, placementInfo.info.box(), region,
                        ClientConfig.Persistent.getSchematicColor(placementInfo.getParticipating())
                    );
                });
                MultiplayerCache.iterate((uuid, otherState) -> otherState.onRenderWorld(true, client, matrices, camera));
            }
        }
    }

    /**
     * Runs when the client joins a world or server
     */
    public static void onClientJoin(ClientPlayNetworkHandler handler, PacketSender packetSender, MinecraftClient client) {
        ClientManager.onJoinWorld(handler, client);
        ClientConfig.sendUpdatePacket();

        MultiplayerCache.clear();
        if (ClientConfig.Persistent.shouldParticipateInMultiplayerCache()) {
            MultiplayerCache.requestUpdate();
        }
    }

    /**
     * Runs when the client disconnects from a world or server
     */
    public static void onClientDisconnect(ClientPlayNetworkHandler handler, MinecraftClient client) {
        ClientClipboardCache.onLeaveWorld();
        ClientSchematicPlacements.clear();
        ClientManager.onLeaveWorld(handler, client);

        MultiplayerCache.clear();
    }

    /**
     * Runs when the game is closed
     */
    public static void onClientStopping(MinecraftClient client) {
        // save config
    }

    /**
     * Runs when the mod detects a keyboard key or mouse button press corresponding to any of the bindings specified in
     * {@code ModInputBinding}
     * @param binding The binding that triggered this event
     * @param action  Describes how the state of the key or mouse button changed
     * @return        Whether the mod should cancel any other action that would be triggered by the key press
     *
     * @see ModInput
     * @see InputAction
     */
    private static boolean onInput(MinecraftClient client, ClientPlayerEntity player, ModInput binding, InputAction action) {
        return switch (binding) {
            case TOGGLE_MOD -> {
                if (action == InputAction.BEGIN) {
                    var state = ClientManager.getEditingState();
                    boolean toggle = state.isTurnedOff();
                    state.receiveToggleModCommand(toggle);
                    player.sendMessage(
                        Spaetial.translateWithArgs("system", toggle ? "message.mod_on" : "message.mod_off"),
                        true
                    );
                    yield true;
                }
                yield false;
            }
            case CONFIG -> {
                if (action == InputAction.BEGIN && client.currentScreen == null) {
                    var screen = new QuickConfigScreen();
                    screen.init(client, client.getWindow().getScaledWidth(), client.getWindow().getScaledHeight());
                    client.setScreen(screen);
                    yield true;
                }
                yield false;
            }
            default -> false;
        };
    }
}
