package spaetial.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import spaetial.ClientConfig;
import spaetial.Spaetial;
import spaetial.editing.OperationAction;
import spaetial.schematic.ClientSchematicPlacements;
import spaetial.editing.ClientManager;
import spaetial.networking.c2s.SendAddressedStateUpdateC2SPacket;
import spaetial.networking.s2c.*;

public final class ClientNetworking {
    private ClientNetworking() {}

    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(MessageS2CPacket.ID, ClientNetworking::receiveMessageS2CPacket);
        ClientPlayNetworking.registerGlobalReceiver(OperationActionVolumeConfirmationPromptS2CPacket.ID, ClientNetworking::receiveOperationActionNeedsConfirmationS2CPacket);

        ClientPlayNetworking.registerGlobalReceiver(RegionPartS2CPacket.ID, ClientNetworking::receiveRegionPartS2CPacket);
        ClientPlayNetworking.registerGlobalReceiver(RegionDimensionsS2CPacket.ID, ClientNetworking::receiveRegionDimensionsS2CPacket);

        ClientPlayNetworking.registerGlobalReceiver(SharedSchematicPlacementAddedS2CPacket.ID, ClientNetworking::receiveSharedSchematicPlacementAddedS2CPacket);
        ClientPlayNetworking.registerGlobalReceiver(SharedSchematicPlacementRemovedS2CPacket.ID, ClientNetworking::receiveSharedSchematicPlacementRemovedS2CPacket);

        ClientPlayNetworking.registerGlobalReceiver(MultiplayerCacheUpdatedS2CPacket.ID, ClientNetworking::receiveMultiplayerCacheUpdatedS2CPacket);
        ClientPlayNetworking.registerGlobalReceiver(MultiplayerCacheRemovedS2CPacket.ID, ClientNetworking::receiveMultiplayerCacheRemovedS2CPacket);

        ClientPlayNetworking.registerGlobalReceiver(RequestAddressedStateUpdateS2CPacket.ID, ClientNetworking::receiveRequestAddressedStateUpdateS2CPacket);
    }

    private static void receiveMessageS2CPacket(MessageS2CPacket packet, ClientPlayNetworking.Context context) {
        ClientManager.receiveMessage(context.client(), packet.message());
    }

    private static void receiveOperationActionNeedsConfirmationS2CPacket(OperationActionVolumeConfirmationPromptS2CPacket packet, ClientPlayNetworking.Context context) {
        assert packet.action() != OperationAction.EXECUTE || packet.operationType() != null;
        ClientManager.openConfirmOperationActionScreen(context.client(), packet.volume(), packet.maxVolume(), packet.action(), packet.operationType(), packet.needsOperatorLevel());
    }

    private static void receiveMultiplayerCacheUpdatedS2CPacket(MultiplayerCacheUpdatedS2CPacket packet, ClientPlayNetworking.Context context) {
        if (ClientConfig.Persistent.shouldParticipateInMultiplayerCache()) {
            MultiplayerCache.receiveUpdatePacket(context.client(), packet);
        }
    }

    private static void receiveMultiplayerCacheRemovedS2CPacket(MultiplayerCacheRemovedS2CPacket packet, ClientPlayNetworking.Context context) {
        if (ClientConfig.Persistent.shouldParticipateInMultiplayerCache()) {
            MultiplayerCache.receiveRemovePacket(context.client(), packet);
        }
    }

    private static void receiveRequestAddressedStateUpdateS2CPacket(RequestAddressedStateUpdateS2CPacket packet, ClientPlayNetworking.Context context) {
        if (ClientConfig.Persistent.shouldParticipateInMultiplayerCache()) {
            try {
                if (packet.addressee() != null) {
                    ClientPlayNetworking.send(new SendAddressedStateUpdateC2SPacket(packet.addressee(), ClientManager.getEditingState().convertToCommon()));
                } else {
                    Spaetial.warn("Received invalid request addressed state update packet: addressee is null");
                }
            } catch (Throwable e) {
                Spaetial.warn("Received invalid request addressed state update packet", e.toString());
            }
        }
    }

    private static void receiveRegionPartS2CPacket(RegionPartS2CPacket packet, ClientPlayNetworking.Context context) {
        ClientRegionRequest.receivePartPacket(packet);
    }

    private static void receiveRegionDimensionsS2CPacket(RegionDimensionsS2CPacket packet, ClientPlayNetworking.Context context) {
        ClientRegionRequest.receiveDimensionsPacket(packet);
    }

    private static void receiveSharedSchematicPlacementAddedS2CPacket(SharedSchematicPlacementAddedS2CPacket packet, ClientPlayNetworking.Context context) {
        ClientSchematicPlacements.handleSharedSchematicPlacementAddedS2CPacket(packet);
    }

    private static void receiveSharedSchematicPlacementRemovedS2CPacket(SharedSchematicPlacementRemovedS2CPacket packet, ClientPlayNetworking.Context context) {
        ClientSchematicPlacements.handleSharedSchematicPlacementRemovedPacket(packet);
    }
}
