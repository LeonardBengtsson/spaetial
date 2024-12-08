package spaetial.networking;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import spaetial.networking.c2s.*;
import spaetial.networking.s2c.*;

public final class PacketTypes {
    private PacketTypes() {}

    public static void register() {
        registerC2S();
        registerS2C();
    }

    private static void registerC2S() {
        PayloadTypeRegistry.playC2S().register(ConfigUpdateC2SPacket.ID, ConfigUpdateC2SPacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(StateUpdateC2SPacket.ID, StateUpdateC2SPacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(RequestMultiplayerCacheUpdateC2SPacket.ID, RequestMultiplayerCacheUpdateC2SPacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(SendAddressedStateUpdateC2SPacket.ID, SendAddressedStateUpdateC2SPacket.PACKET_CODEC);

        PayloadTypeRegistry.playC2S().register(SchematicUploadHeadC2SPacket.ID, SchematicUploadHeadC2SPacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(SchematicUploadPartC2SPacket.ID, SchematicUploadPartC2SPacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(PasteClipboardC2SPacket.ID, PasteClipboardC2SPacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(RemoveSchematicPlacementC2SPacket.ID, RemoveSchematicPlacementC2SPacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(MoveSchematicPlacementC2SPacket.ID, MoveSchematicPlacementC2SPacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(ToggleSharingSchematicPlacementC2SPacket.ID, ToggleSharingSchematicPlacementC2SPacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(ToggleParticipationSharedSchematicPlacementC2SPacket.ID, ToggleParticipationSharedSchematicPlacementC2SPacket.PACKET_CODEC);

        PayloadTypeRegistry.playC2S().register(RequestRegionC2SPacket.ID, RequestRegionC2SPacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(RequestSharedSchematicPlacementRegionC2SPacket.ID, RequestSharedSchematicPlacementRegionC2SPacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(RequestClipboardRegionC2SPacket.ID, RequestClipboardRegionC2SPacket.PACKET_CODEC);

        PayloadTypeRegistry.playC2S().register(ConfirmOperationActionC2SPacket.ID, ConfirmOperationActionC2SPacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(RequestUndoC2SPacket.ID, RequestUndoC2SPacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(RequestRedoC2SPacket.ID, RequestRedoC2SPacket.PACKET_CODEC);

        PayloadTypeRegistry.playC2S().register(RequestCopyOperationC2SPacket.ID, RequestCopyOperationC2SPacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(RequestCloneOperationC2SPacket.ID, RequestCloneOperationC2SPacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(RequestLineStackOperationC2SPacket.ID, RequestLineStackOperationC2SPacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(RequestVolumeStackOperationC2SPacket.ID, RequestVolumeStackOperationC2SPacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(RequestReplaceOperationC2SPacket.ID, RequestReplaceOperationC2SPacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(RequestCompleteSchematicOperationC2SPacket.ID, RequestCompleteSchematicOperationC2SPacket.PACKET_CODEC);
    }

    private static void registerS2C() {
        PayloadTypeRegistry.playS2C().register(MessageS2CPacket.ID, MessageS2CPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(OperationActionVolumeConfirmationPromptS2CPacket.ID, OperationActionVolumeConfirmationPromptS2CPacket.PACKET_CODEC);

        PayloadTypeRegistry.playS2C().register(RegionPartS2CPacket.ID, RegionPartS2CPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(RegionDimensionsS2CPacket.ID, RegionDimensionsS2CPacket.PACKET_CODEC);

        PayloadTypeRegistry.playS2C().register(SharedSchematicPlacementAddedS2CPacket.ID, SharedSchematicPlacementAddedS2CPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(SharedSchematicPlacementRemovedS2CPacket.ID, SharedSchematicPlacementRemovedS2CPacket.PACKET_CODEC);

        PayloadTypeRegistry.playS2C().register(MultiplayerCacheUpdatedS2CPacket.ID, MultiplayerCacheUpdatedS2CPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(MultiplayerCacheRemovedS2CPacket.ID, MultiplayerCacheRemovedS2CPacket.PACKET_CODEC);

        PayloadTypeRegistry.playS2C().register(RequestAddressedStateUpdateS2CPacket.ID, RequestAddressedStateUpdateS2CPacket.PACKET_CODEC);
    }
}
