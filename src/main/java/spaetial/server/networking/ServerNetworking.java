package spaetial.server.networking;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import spaetial.Spaetial;
import spaetial.networking.message.InsufficientPermissionsErrorMessage;
import spaetial.networking.message.NoClipboardErrorMessage;
import spaetial.networking.message.RequestedRegionDimensionDoesntExistErrorMessage;
import spaetial.networking.message.SchematicDoesntExistErrorMessage;
import spaetial.schematic.SchematicPlacement;
import spaetial.schematic.Schematic;
import spaetial.schematic.SchematicMetadata;
import spaetial.editing.operation.*;
import spaetial.editing.region.Region;
import spaetial.networking.c2s.*;
import spaetial.networking.s2c.*;
import spaetial.server.editing.ServerManager;
import spaetial.server.permissions.EditingPermissions;

public final class ServerNetworking {
    private ServerNetworking() {}

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(ConfigUpdateC2SPacket.ID, ServerNetworking::receiveConfigUpdateC2SPacket);
        ServerPlayNetworking.registerGlobalReceiver(StateUpdateC2SPacket.ID, ServerNetworking::receiveStateUpdateC2SPacket);
        ServerPlayNetworking.registerGlobalReceiver(RequestMultiplayerCacheUpdateC2SPacket.ID, ServerNetworking::receiveRequestMultiplayerCacheUpdateC2SPacket);
        ServerPlayNetworking.registerGlobalReceiver(SendAddressedStateUpdateC2SPacket.ID, ServerNetworking::receiveSendAddressedStateUpdateC2SPacket);

        ServerPlayNetworking.registerGlobalReceiver(SchematicUploadHeadC2SPacket.ID, ServerNetworking::receiveSchematicUploadHeadC2SPacket);
        ServerPlayNetworking.registerGlobalReceiver(SchematicUploadPartC2SPacket.ID, ServerNetworking::receiveSchematicUploadPartC2SPacket);
        ServerPlayNetworking.registerGlobalReceiver(PasteClipboardC2SPacket.ID, ServerNetworking::receivePasteClipboardC2SPacket);
        ServerPlayNetworking.registerGlobalReceiver(RemoveSchematicPlacementC2SPacket.ID, ServerNetworking::receiveRemoveSchematicPlacementC2SPacket);
        ServerPlayNetworking.registerGlobalReceiver(MoveSchematicPlacementC2SPacket.ID, ServerNetworking::receiveMoveSchematicPlacementC2SPacket);
        ServerPlayNetworking.registerGlobalReceiver(ToggleSharingSchematicPlacementC2SPacket.ID, ServerNetworking::receiveToggleSharingSchematicPlacementC2SPacket);
        ServerPlayNetworking.registerGlobalReceiver(ToggleParticipationSharedSchematicPlacementC2SPacket.ID, ServerNetworking::receiveToggleParticipatingSharedSchematicPlacementC2SPacket);

        ServerPlayNetworking.registerGlobalReceiver(RequestRegionC2SPacket.ID, ServerNetworking::receiveRequestRegionC2SPacket);
        ServerPlayNetworking.registerGlobalReceiver(RequestSharedSchematicPlacementRegionC2SPacket.ID, ServerNetworking::receiveRequestSharedSchematicPlacementRegionC2SPacket);
        ServerPlayNetworking.registerGlobalReceiver(RequestClipboardRegionC2SPacket.ID, ServerNetworking::receiveRequestClipboardRegionC2SPacket);

        ServerPlayNetworking.registerGlobalReceiver(ConfirmOperationActionC2SPacket.ID, ServerNetworking::receiveConfirmOperationActionC2SPacket);
        ServerPlayNetworking.registerGlobalReceiver(RequestUndoC2SPacket.ID, ServerNetworking::receiveRequestUndoC2SPacket);
        ServerPlayNetworking.registerGlobalReceiver(RequestRedoC2SPacket.ID, ServerNetworking::receiveRequestRedoC2SPacket);

        ServerPlayNetworking.registerGlobalReceiver(RequestCopyOperationC2SPacket.ID, ServerNetworking::receiveRequestCopyOperationC2SPacket);
        ServerPlayNetworking.registerGlobalReceiver(RequestCloneOperationC2SPacket.ID, ServerNetworking::receiveRequestCloneOperationC2SPacket);
        ServerPlayNetworking.registerGlobalReceiver(RequestLineStackOperationC2SPacket.ID, ServerNetworking::receiveRequestLineStackOperationC2SPacket);
        ServerPlayNetworking.registerGlobalReceiver(RequestVolumeStackOperationC2SPacket.ID, ServerNetworking::receiveRequestVolumeStackOperationC2SPacket);
        ServerPlayNetworking.registerGlobalReceiver(RequestReplaceOperationC2SPacket.ID, ServerNetworking::receiveRequestReplaceOperationC2SPacket);
        ServerPlayNetworking.registerGlobalReceiver(RequestCompleteSchematicOperationC2SPacket.ID, ServerNetworking::receiveRequestCompleteSchematicOperationC2SPacket);
    }

    private static void receiveConfirmOperationActionC2SPacket(ConfirmOperationActionC2SPacket packet, ServerPlayNetworking.Context context) {
        ServerManager.confirmOperationAction(context.server(), context.player(), packet.skipHistory());
    }

    private static void receiveRequestUndoC2SPacket(RequestUndoC2SPacket packet, ServerPlayNetworking.Context context) {
        ServerManager.requestUndo(context.server(), context.player());
    }

    private static void receiveRequestRedoC2SPacket(RequestRedoC2SPacket packet, ServerPlayNetworking.Context context) {
        ServerManager.requestRedo(context.server(), context.player());
    }

    private static void receiveRequestCopyOperationC2SPacket(RequestCopyOperationC2SPacket packet, ServerPlayNetworking.Context context) {
        ServerManager.requestOperation(context.server(), context.player(), new CopyOperation(packet.dim(), context.player(), packet.selection(), packet.cut(), packet.filter(), packet.regionRequestId()));
    }

    private static void receiveRequestCloneOperationC2SPacket(RequestCloneOperationC2SPacket packet, ServerPlayNetworking.Context context) {
        ServerManager.requestOperation(context.server(), context.player(), new CloneOperation(packet.sourceDim(), packet.destDim(), context.player(), packet.selection(), packet.delta(), packet.move(), packet.sourceFilter(), packet.destinationFilter()));
    }

    private static void receiveRequestLineStackOperationC2SPacket(RequestLineStackOperationC2SPacket packet, ServerPlayNetworking.Context context) {
        ServerManager.requestOperation(context.server(), context.player(), new LineStackOperation(packet.sourceDim(), packet.destDim(), context.player(), packet.selection(), packet.delta(), packet.move(), packet.stackSize(), packet.spacing(), packet.sourceFilter(), packet.destinationFilter()));
    }

    private static void receiveRequestVolumeStackOperationC2SPacket(RequestVolumeStackOperationC2SPacket packet, ServerPlayNetworking.Context context) {
        ServerManager.requestOperation(context.server(), context.player(), new VolumeStackOperation(packet.sourceDim(), packet.destDim(), context.player(), packet.selection(), packet.delta(), packet.move(), packet.stackSize(), packet.spacing(), packet.sourceFilter(), packet.destinationFilter()));
    }

    private static void receiveRequestReplaceOperationC2SPacket(RequestReplaceOperationC2SPacket packet, ServerPlayNetworking.Context context) {
        ServerManager.requestOperation(context.server(), context.player(), new ReplaceOperation(packet.dim(), context.player(), packet.selection(), packet.filter(), packet.material()));
    }

    private static void receiveRequestCompleteSchematicOperationC2SPacket(RequestCompleteSchematicOperationC2SPacket packet, ServerPlayNetworking.Context context) {
        SchematicPlacement schematicPlacement = ServerManager.getOwnSchematicPlacement(context.player().getUuid(), packet.placementId());
        if (schematicPlacement != null) {
            ServerManager.requestOperation(context.server(), context.player(),
                new CompleteSchematicOperation(
                    schematicPlacement.dim,
                    context.player(),
                    context.server(),
                    packet.placementId(),
                    schematicPlacement.schematic.region(),
                    schematicPlacement.minPos,
                    packet.filter()
                )
            );
        } else {
            ServerPlayNetworking.send(context.player(), new MessageS2CPacket(new SchematicDoesntExistErrorMessage()));
            Spaetial.warn("Received invalid schematic completion operation request: schematic with id " + packet.placementId() + " doesn't exist");
        }
    }

    private static void receiveStateUpdateC2SPacket(StateUpdateC2SPacket packet, ServerPlayNetworking.Context context) {
        for (var otherPlayer : context.server().getPlayerManager().getPlayerList()) {
            if (otherPlayer == context.player()) continue;
            var config = ServerManager.getPlayerConfig(otherPlayer.getUuid());
            if (config != null) {
                if (!config.participatesInMultiplayerCache()) return;
            }
            ServerPlayNetworking.send(otherPlayer, new MultiplayerCacheUpdatedS2CPacket(context.player().getUuid(), packet.state()));
        }
    }

    private static void receiveSendAddressedStateUpdateC2SPacket(SendAddressedStateUpdateC2SPacket packet, ServerPlayNetworking.Context context) {
        var addressee = context.server().getPlayerManager().getPlayer(packet.addressee());
        if (addressee != null) {
            var config = ServerManager.getPlayerConfig(packet.addressee());
            if (config != null) {
                if (!config.participatesInMultiplayerCache()) return;
            }
            ServerPlayNetworking.send(addressee, new MultiplayerCacheUpdatedS2CPacket(context.player().getUuid(), packet.state()));
        } else {
            Spaetial.warn("Received addressed state update packet for player that isn't on the server (id: " + packet.addressee().toString() + ")");
        }
    }

    public static void sendMultiplayerCacheRemovedPackets(MinecraftServer server, ServerPlayerEntity removedPlayer) {
        for (var otherPlayer : server.getPlayerManager().getPlayerList()) {
            if (otherPlayer == removedPlayer) continue;
            var config = ServerManager.getPlayerConfig(otherPlayer.getUuid());

            if (config != null) {
                if (!config.participatesInMultiplayerCache()) return;
                ServerPlayNetworking.send(otherPlayer, new MultiplayerCacheRemovedS2CPacket(removedPlayer.getUuid()));
            }
        }
    }

    private static void receiveConfigUpdateC2SPacket(ConfigUpdateC2SPacket packet, ServerPlayNetworking.Context context) {
        ServerManager.setPlayerConfig(context.player().getUuid(), packet.config());
    }

    private static void receiveRequestMultiplayerCacheUpdateC2SPacket(RequestMultiplayerCacheUpdateC2SPacket packet, ServerPlayNetworking.Context context) {
        for (var otherPlayer : context.server().getPlayerManager().getPlayerList()) {
            if (otherPlayer == context.player()) continue;
            var config = ServerManager.getPlayerConfig(otherPlayer.getUuid());
            if (config != null) {
                if (!config.participatesInMultiplayerCache()) return;
                ServerPlayNetworking.send(otherPlayer, new RequestAddressedStateUpdateS2CPacket(context.player().getUuid()));
            }
        }
    }

    private static void receiveRequestRegionC2SPacket(RequestRegionC2SPacket packet, ServerPlayNetworking.Context context) {
        if (EditingPermissions.mayRequestRegion(context.player())) {
            var world = context.server().getWorld(packet.dim());
            if (world == null) {
                ServerPlayNetworking.send(context.player(), new MessageS2CPacket(new RequestedRegionDimensionDoesntExistErrorMessage()));
                Spaetial.warn("Received invalid region request packet: world " + packet.dim().getValue().toString() + " doesn't exist");
                return;
            }
            var region = Region.create(world, packet.selection());
            ServerRegionTransmitter.sendRegion(context.server(), packet.requestId(), region, context.player(), false);
        } else {
            ServerPlayNetworking.send(context.player(), new MessageS2CPacket(new InsufficientPermissionsErrorMessage()));
        }
    }

    private static void receiveRequestSharedSchematicPlacementRegionC2SPacket(RequestSharedSchematicPlacementRegionC2SPacket packet, ServerPlayNetworking.Context context) {
        // TODO maybe add permissions for this too?
        if (true/*EditingPermissions.mayRequestRegion(player)*/) {
            var sharedPlacement = ServerManager.getSharedSchematicPlacement(packet.placementId());
            if (sharedPlacement == null) {
                ServerPlayNetworking.send(context.player(), new MessageS2CPacket(new SchematicDoesntExistErrorMessage()));
            } else {
                var region = sharedPlacement.schematicPlacement.schematic.region();
                ServerRegionTransmitter.sendRegion(context.server(), packet.requestId(), region, context.player(), false);
            }
        }
    }

    private static void receiveRequestClipboardRegionC2SPacket(RequestClipboardRegionC2SPacket packet, ServerPlayNetworking.Context context) {
        // TODO maybe add permissions for this too?
        if (true/*EditingPermissions.mayRequestRegion(player)*/) {
            var region = ServerManager.getCurrentClipboard(context.player().getUuid());
            if (region == null) {
                ServerPlayNetworking.send(context.player(), new MessageS2CPacket(new NoClipboardErrorMessage()));
            } else {
                ServerRegionTransmitter.sendRegion(context.server(), packet.requestId(), region, context.player(), true);
            }
        }
    }

    private static void receiveSchematicUploadHeadC2SPacket(SchematicUploadHeadC2SPacket packet, ServerPlayNetworking.Context context) {
        ServerRegionReceiver.receiveHeadPacket(packet.uploadId(), packet.schematicId(), context.player().getUuid(), packet.minPos(), packet.dim(), packet.meta());
    }

    private static void receiveSchematicUploadPartC2SPacket(SchematicUploadPartC2SPacket packet, ServerPlayNetworking.Context context) {
        ServerRegionReceiver.receivePartPacket(packet);
    }

    private static void receivePasteClipboardC2SPacket(PasteClipboardC2SPacket packet, ServerPlayNetworking.Context context) {
        var clipboard = ServerManager.getCurrentClipboard(context.player().getUuid());
        if (clipboard == null) {
            ServerPlayNetworking.send(context.player(), new MessageS2CPacket(new NoClipboardErrorMessage()));
            Spaetial.warn(
                "Received paste schematic packet with placement id " + packet.placementId().toString() + " from player " +
                context.player().getGameProfile().getName() + " with id " + context.player().getUuid() + ", but they had no clipboard to paste"
            );
            return;
        }
        var placement = new SchematicPlacement(
            new Schematic(
                clipboard,
                SchematicMetadata.createClipboardInfo(context.player().getUuid())
            ),
            packet.minPos(),
            packet.dim()
        );
        ServerManager.addSchematicPlacement(context.player().getUuid(), packet.placementId(), placement);
    }

    private static void receiveRemoveSchematicPlacementC2SPacket(RemoveSchematicPlacementC2SPacket packet, ServerPlayNetworking.Context context) {
        ServerManager.removeSchematicPlacement(context.server(), context.player(), packet.placementId());
    }

    private static void receiveMoveSchematicPlacementC2SPacket(MoveSchematicPlacementC2SPacket packet, ServerPlayNetworking.Context context) {
        ServerManager.moveSchematicPlacement(context.player().getUuid(), packet.placementId(), packet.minPos(), packet.dim());
    }

    private static void receiveToggleSharingSchematicPlacementC2SPacket(ToggleSharingSchematicPlacementC2SPacket packet, ServerPlayNetworking.Context context) {
        ServerManager.toggleSchematicPlacementSharing(context.server(), packet.placementId(), packet.toggle(), context.player().getUuid());
    }

    private static void receiveToggleParticipatingSharedSchematicPlacementC2SPacket(ToggleParticipationSharedSchematicPlacementC2SPacket packet, ServerPlayNetworking.Context context) {
        ServerManager.toggleParticipationSharedSchematicPlacement(packet.placementId(), packet.toggle(), context.player().getUuid());
    }
}
