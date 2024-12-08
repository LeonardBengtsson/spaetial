package spaetial.server.editing;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import spaetial.Spaetial;
import spaetial.editing.OperationAction;
import spaetial.editing.operation.Operation;
import spaetial.networking.message.InsufficientPermissionsErrorMessage;
import spaetial.networking.message.NothingToRedoErrorMessage;
import spaetial.networking.message.NothingToUndoErrorMessage;
import spaetial.networking.message.VolumeLimitReachedErrorMessage;
import spaetial.networking.s2c.MessageS2CPacket;
import spaetial.networking.s2c.OperationActionVolumeConfirmationPromptS2CPacket;
import spaetial.schematic.SchematicPlacement;
import spaetial.schematic.SharedSchematicPlacement;
import spaetial.schematic.SharedSchematicPlacementInfo;
import spaetial.editing.region.Region;
import spaetial.networking.s2c.SharedSchematicPlacementAddedS2CPacket;
import spaetial.networking.s2c.SharedSchematicPlacementRemovedS2CPacket;
import spaetial.server.ServerConfig;
import spaetial.server.networking.ServerRegionTransmitter;
import spaetial.server.networking.ServerSideClientConfig;
import spaetial.server.permissions.EditingPermissions;
import spaetial.server.permissions.VolumePermissionStatus;

import java.util.HashMap;
import java.util.UUID;

public final class ServerManager {
    private ServerManager() {}

    private static final HashMap<UUID, PlayerData> playerData = new HashMap<>();
    private static final HashMap<UUID, ServerSideClientConfig> playerConfig = new HashMap<>();

    private static final HashMap<UUID, SharedSchematicPlacement> sharedSchematicPlacements = new HashMap<>();

    public static void onTick(MinecraftServer server) {
        playerData.forEach((uuid, playerData) -> playerData.onTick(server));
    }

    public static void onServerStopping(MinecraftServer server) {
        playerData.forEach((uuid, playerData) -> playerData.onServerStopping(server));
    }

    public static void loadPlayerDataFromFile() {

    }

    public static void savePlayerDataToFile() {

    }

    public static @Nullable ServerSideClientConfig getPlayerConfig(@NotNull UUID playerId) {
        return playerConfig.get(playerId);
    }

    public static ServerSideClientConfig getPlayerConfigOrDefault(@NotNull UUID playerId) {
        var config = playerConfig.get(playerId);
        return config == null ? ServerSideClientConfig.DEFAULT : config;
    }

    public static void setPlayerConfig(@NotNull UUID playerId, @Nullable ServerSideClientConfig config) {
        playerConfig.put(playerId, config);
    }

    @Nullable
    public static Region getCurrentClipboard(@NotNull UUID playerId) {
        return getOrCreatePlayerData(playerId).getClipboard();
    }

    @Deprecated
    @Nullable
    public static UUID getCurrentClipboardId(@NotNull UUID playerId) {
        return getOrCreatePlayerData(playerId).getClipboardId();
    }

    public static void setClipboard(MinecraftServer server, @NotNull UUID playerId, Region region, @Nullable UUID regionRequestId) {
        getOrCreatePlayerData(playerId).setClipboard(region);
        if (regionRequestId != null) {
            var player = server.getPlayerManager().getPlayer(playerId);
            if (player != null) {
                ServerRegionTransmitter.sendRegion(server, regionRequestId, region, player, true);
            }
        }
    }

    @Deprecated
    public static void purgeExpiredHistoricalClipboards() {
        // TODO when would this actually be used
        playerData.forEach((uuid, playerData) -> playerData.purgeExpiredHistoricalClipboards());
    }

    @Deprecated
    @Nullable
    public static Region getHistoricalClipboard(@NotNull UUID player, UUID clipboardId) {
        return getOrCreatePlayerData(player).retrieveHistoricalClipboard(clipboardId);
    }

    private static @NotNull PlayerData getOrCreatePlayerData(@NotNull UUID player) {
        if (!playerData.containsKey(player)) {
            playerData.put(player, new PlayerData());
        }
        return playerData.get(player);
    }

    public static void requestOperation(MinecraftServer server, ServerPlayerEntity player, Operation operation) {
        if (EditingPermissions.mayRequestOperation(player, operation)) {
            if (operation.dimension == null) {
                Spaetial.warn("Player " + player.getGameProfile().getName() + " requested an operation of type '" + operation.getType().name + "', but the operation's dimension couldn't be read");
                return;
            }
            var volumeStatus = ServerConfig.checkOperationVolume(player, operation);
            var playerData = getOrCreatePlayerData(player.getUuid());
            switch (volumeStatus.status()) {
                case OK -> {
                    Spaetial.info("Player " + player.getGameProfile().getName() + " queued an operation of type '" + operation.getType().name + "', affecting a volume of " + operation.getVolume() + " blocks");
                    playerData.queueExecution(server, operation, false);
                }
                case NEEDS_CONFIRM, OPERATOR_CONFIRM -> {
                    Spaetial.info("Player " + player.getGameProfile().getName() + " tried to perform operation of type '" + operation.getType().name + "', affecting a volume of " + operation.getVolume() + " blocks, but was put on hold");
                    playerData.putOperationOnHold(operation);
                    ServerPlayNetworking.send(
                        player,
                        new OperationActionVolumeConfirmationPromptS2CPacket(
                            operation.canBeSavedToHistory() ? OperationAction.EXECUTE : OperationAction.EXECUTE_WITHOUT_HISTORY,
                            volumeStatus.volume(),
                            volumeStatus.maxVolume(),
                            volumeStatus.status() == VolumePermissionStatus.Status.OPERATOR_CONFIRM,
                            operation.getType()
                        )
                    );
                }
                case DENIED -> {
                    ServerPlayNetworking.send(player, new MessageS2CPacket(new VolumeLimitReachedErrorMessage()));
                }
            }
        } else {
            ServerPlayNetworking.send(player, new MessageS2CPacket(new InsufficientPermissionsErrorMessage()));
            Spaetial.warn("Player " + player.getGameProfile().getName() + " did not receive permission to perform operation " + operation.toString());
        }
    }

    public static void confirmOperationAction(MinecraftServer server, ServerPlayerEntity player, boolean skipHistory) {
        var data = getOrCreatePlayerData(player.getUuid());
        OperationQueue.Entry action = data.getOnHoldAction();
        if (action != null) {
            boolean allowed = switch (action.action()) {
                case EXECUTE, EXECUTE_WITHOUT_HISTORY -> {
                    yield EditingPermissions.mayRequestOperation(player, action.operation());
                }
                case UNDO -> {
                    var next = data.getNextUndo();
                    if (next == null) yield false;
                    yield EditingPermissions.mayRequestUndo(player, next);
                }
                case REDO -> {
                    var next = data.getNextRedo();
                    if (next == null) yield false;
                    yield EditingPermissions.mayRequestRedo(player, next);
                }
            };
            if (allowed) {
                Spaetial.info("Player " + player.getGameProfile().getName() + " confirmed operation action" + (skipHistory ? ", skipping history" : ""));
                data.confirmOnHoldAction(server, skipHistory);
            } else {
                ServerPlayNetworking.send(player, new MessageS2CPacket(new InsufficientPermissionsErrorMessage()));
                Spaetial.warn("Player " + player.getGameProfile().getName() + " did not get permission to confirm an operation action");
            }
        }
    }

    public static void requestUndo(MinecraftServer server, ServerPlayerEntity player) {
        var data = getOrCreatePlayerData(player.getUuid());
        var nextUndo = data.getNextUndo();
        if (nextUndo == null) {
            ServerPlayNetworking.send(player, new MessageS2CPacket(new NothingToUndoErrorMessage()));
            Spaetial.warn("Player " + player.getGameProfile().getName() + " tried to undo an operation but there was nothing to undo");
        } else if (EditingPermissions.mayRequestUndo(player, nextUndo)) {
            var volumeStatus = ServerConfig.checkOperationVolume(player, nextUndo);
            var playerData = getOrCreatePlayerData(player.getUuid());
            switch (volumeStatus.status()) {
                case OK -> {
                    Spaetial.info("Player " + player.getGameProfile().getName() + " queued undoing an operation of type '" + nextUndo.getType().name + "', affecting a volume of " + nextUndo.getVolume() + " blocks");
                    playerData.queueUndo(server);
                }
                case NEEDS_CONFIRM, OPERATOR_CONFIRM -> {
                    Spaetial.info("Player " + player.getGameProfile().getName() + " tried to undo an operation of type '" + nextUndo.getType().name + "', affecting a volume of " + nextUndo.getVolume() + " blocks, but was put on hold");
                    playerData.putUndoOnHold();
                    ServerPlayNetworking.send(
                        player,
                        new OperationActionVolumeConfirmationPromptS2CPacket(
                            OperationAction.UNDO,
                            volumeStatus.volume(),
                            volumeStatus.maxVolume(),
                            volumeStatus.status() == VolumePermissionStatus.Status.OPERATOR_CONFIRM,
                            null
                        )
                    );
                }
                case DENIED -> {
                    ServerPlayNetworking.send(player, new MessageS2CPacket(new VolumeLimitReachedErrorMessage()));
                    Spaetial.warn("Player " + player.getGameProfile().getName() + " did not receive permission to undo operation as it exceeds the operation volume limit");
                }
            }
        } else {
            ServerPlayNetworking.send(player, new MessageS2CPacket(new InsufficientPermissionsErrorMessage()));
            Spaetial.warn("Player " + player.getGameProfile().getName() + " did not receive permission to undo operation");
        }
    }

    public static void requestRedo(MinecraftServer server, ServerPlayerEntity player) {
        var data = getOrCreatePlayerData(player.getUuid());
        var nextRedo = data.getNextRedo();
        if (nextRedo == null) {
            ServerPlayNetworking.send(player, new MessageS2CPacket(new NothingToRedoErrorMessage()));
            Spaetial.warn("Player " + player.getGameProfile().getName() + " tried to redo an operation but there was nothing to undo");
        } else if (EditingPermissions.mayRequestRedo(player, nextRedo)) {
            var volumeStatus = ServerConfig.checkOperationVolume(player, nextRedo);
            var playerData = getOrCreatePlayerData(player.getUuid());
            switch (volumeStatus.status()) {
                case OK -> {
                    Spaetial.info("Player " + player.getGameProfile().getName() + " queued redoing an operation of type '" + nextRedo.getType().name + "', affecting a volume of " + nextRedo.getVolume() + " blocks");
                    playerData.queueRedo(server);
                }
                case NEEDS_CONFIRM, OPERATOR_CONFIRM -> {
                    Spaetial.info("Player " + player.getGameProfile().getName() + " tried to redo an operation of type '" + nextRedo.getType().name + "', affecting a volume of " + nextRedo.getVolume() + " blocks, but was put on hold");
                    playerData.putRedoOnHold();
                    ServerPlayNetworking.send(
                        player,
                        new OperationActionVolumeConfirmationPromptS2CPacket(
                            OperationAction.REDO,
                            volumeStatus.volume(),
                            volumeStatus.maxVolume(),
                            volumeStatus.status() == VolumePermissionStatus.Status.OPERATOR_CONFIRM,
                            null
                        )
                    );
                }
                case DENIED -> {
                    ServerPlayNetworking.send(player, new MessageS2CPacket(new VolumeLimitReachedErrorMessage()));
                    Spaetial.warn("Player " + player.getGameProfile().getName() + " did not receive permission to redo operation as it exceeds the operation volume limit");
                }
            }
        } else {
            ServerPlayNetworking.send(player, new MessageS2CPacket(new InsufficientPermissionsErrorMessage()));
            Spaetial.warn("Player " + player.getGameProfile().getName() + " did not receive permission to redo operation");
        }
    }

    public static void addSchematicPlacement(UUID playerId, UUID placementId, SchematicPlacement placement) {
        getOrCreatePlayerData(playerId).putSchematicPlacement(placementId, placement);
    }

    public static void removeSchematicPlacement(MinecraftServer server, ServerPlayerEntity requestingPlayer, UUID placementId) {
        var playerId = requestingPlayer.getUuid();
        var playerData = getOrCreatePlayerData(playerId);
        var ownPlacement = playerData.getSchematicPlacement(placementId);
        var sharedPlacement = sharedSchematicPlacements.get(placementId);
        if (ownPlacement != null) {
            playerData.removeSchematicPlacement(placementId);
        }
        if (sharedPlacement != null) {
            if (sharedPlacement.owner != playerId) {
                // TODO consider allowing this
                Spaetial.warn("Player " + requestingPlayer.getGameProfile().getName() + " tried to remove shared schematic placement with id " + placementId + " without being the owner of the placement");
                return;
            }
            var packet = new SharedSchematicPlacementRemovedS2CPacket(placementId);
            sharedPlacement.participants.forEach(otherPlayerId -> {
                if (otherPlayerId == sharedPlacement.owner) return;
                var otherPlayer = server.getPlayerManager().getPlayer(otherPlayerId);
                if (otherPlayer != null) ServerPlayNetworking.send(otherPlayer, packet);
            });
            sharedSchematicPlacements.remove(placementId);
        }
    }

    public static void moveSchematicPlacement(UUID playerId, UUID placementId, BlockPos minPos, RegistryKey<World> dim) {
        var data = getOrCreatePlayerData(playerId);
        var placement = data.getSchematicPlacement(placementId);
        if (placement == null) {
            var sharedPlacement = sharedSchematicPlacements.get(placementId);
            if (sharedPlacement.owner == playerId) {
                sharedSchematicPlacements.put(placementId, sharedPlacement.move(minPos, dim));
            } else {
                // TODO should this actually be allowed instead?
                Spaetial.warn("Player with id " + playerId + " tried to move shared schematic placement with id " + placementId + " despite not being the owner");
            }
        } else {
            data.putSchematicPlacement(placementId, placement.move(minPos, dim));
        }
    }

    public static @Nullable SchematicPlacement getOwnSchematicPlacement(UUID playerId, UUID placementId) {
        return getOrCreatePlayerData(playerId).getSchematicPlacement(placementId);
    }

    public static @Nullable SharedSchematicPlacement getSharedSchematicPlacement(UUID placementId) {
        return sharedSchematicPlacements.get(placementId);
    }

    public static void toggleSchematicPlacementSharing(MinecraftServer server, UUID placementId, boolean toggle, UUID playerId) {
        if (toggle) {
            var data = getOrCreatePlayerData(playerId);
            var placement = data.getSchematicPlacement(placementId);
            if (placement == null) {
                var player = server.getPlayerManager().getPlayer(playerId);
                Spaetial.warn("Can't toggle on sharing for schematic placement with placement id " + placementId + " as it does not exist in the list of unshared schematic placements for player " + (player == null ? "with id " + playerId : player.getGameProfile().getName()));
                return;
            }

            var sharedPlacement = new SharedSchematicPlacement(placement, playerId);
            sharedSchematicPlacements.put(placementId, sharedPlacement);
            data.removeSchematicPlacement(placementId);

            var packet = new SharedSchematicPlacementAddedS2CPacket(placementId, SharedSchematicPlacementInfo.create(sharedPlacement));
            server.getPlayerManager().getPlayerList().forEach(otherPlayer -> {
                if (otherPlayer.getUuid() == playerId) return;
                ServerPlayNetworking.send(otherPlayer, packet);
            });
        } else {
            var sharedPlacement = sharedSchematicPlacements.get(placementId);
            if (sharedPlacement == null) {
                Spaetial.warn("Can't toggle off sharing for schematic placement with id " + placementId + " as it does not exist in the list of shared schematic placements");
                return;
            }
            if (sharedPlacement.owner != playerId) {
                var player = server.getPlayerManager().getPlayer(playerId);
                Spaetial.warn("Player " + (player == null ? "with id " + playerId : player.getGameProfile().getName()) + " tried to toggle off sharing for schematic placement with id " + placementId + " but is not the owner of the schematic placement");
                return;
            }

            var data = getOrCreatePlayerData(playerId);
            data.putSchematicPlacement(placementId, sharedPlacement.schematicPlacement);

            var packet = new SharedSchematicPlacementRemovedS2CPacket(placementId);
            sharedPlacement.participants.forEach(otherPlayerId -> {
                if (otherPlayerId == playerId) return;
                var otherPlayer = server.getPlayerManager().getPlayer(otherPlayerId);
                if (otherPlayer == null) return;
                ServerPlayNetworking.send(otherPlayer, packet);
            });
        }
    }

    public static void toggleParticipationSharedSchematicPlacement(UUID placementId, boolean toggle, UUID playerId) {
        var placement = sharedSchematicPlacements.get(placementId);
        if (placement == null) {
            Spaetial.warn("Could not toggle participation for player with id " + playerId + " for shared schematic placement with id " + placementId + " as it does not exist in the list of shared schematic placements");
            return;
        }
        if (toggle) {
            placement.participants.add(playerId);
        } else {
            placement.participants.remove(playerId);
        }
    }
}
