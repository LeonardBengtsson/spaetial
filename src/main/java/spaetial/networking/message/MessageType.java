package spaetial.networking.message;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import spaetial.Spaetial;
import spaetial.networking.PacketCodecSupplier;
import spaetial.util.Translatable;

public enum MessageType implements Translatable, PacketCodecSupplier<PacketByteBuf, Message> {
    VOLUME_LIMIT_REACHED_ERROR("volume_limit_reached_error"),
    INSUFFICIENT_PERMISSIONS_ERROR("insufficient_permissions_error"),
    NOTHING_TO_UNDO_ERROR("nothing_to_undo_error"),
    NOTHING_TO_REDO_ERROR("nothing_to_redo_error"),
    NO_CLIPBOARD_ERROR("no_clipboard_error"),
    REQUESTED_REGION_DIMENSION_DOESNT_EXIST_ERROR("requested_region_dimension_doesnt_exist_error"),
    SCHEMATIC_DOESNT_EXIST_ERROR("schematic_doesnt_exist_error"),
    OUTDATED_REQUEST_ERROR("outdated_request_error"),
    REGION_REQUEST_TIMED_OUT_ERROR("region_request_timed_out_error");

    public final String name;

    MessageType(String name) {
        this.name = name;
    }

    @Override
    public String getTranslationKey() {
        return Spaetial.translationKey(null, "error", name, "name");
    }

    @Override
    public PacketCodec<PacketByteBuf, ? extends Message> getCodec() {
        return switch (this) {
            case VOLUME_LIMIT_REACHED_ERROR -> VolumeLimitReachedErrorMessage.PACKET_CODEC;
            case INSUFFICIENT_PERMISSIONS_ERROR -> InsufficientPermissionsErrorMessage.PACKET_CODEC;
            case NOTHING_TO_UNDO_ERROR -> NothingToUndoErrorMessage.PACKET_CODEC;
            case NOTHING_TO_REDO_ERROR -> NothingToRedoErrorMessage.PACKET_CODEC;
            case NO_CLIPBOARD_ERROR -> NoClipboardErrorMessage.PACKET_CODEC;
            case REQUESTED_REGION_DIMENSION_DOESNT_EXIST_ERROR -> RequestedRegionDimensionDoesntExistErrorMessage.PACKET_CODEC;
            case SCHEMATIC_DOESNT_EXIST_ERROR -> SchematicDoesntExistErrorMessage.PACKET_CODEC;
            case OUTDATED_REQUEST_ERROR -> OutdatedRequestErrorMessage.PACKET_CODEC;
            case REGION_REQUEST_TIMED_OUT_ERROR -> RegionRequestTimedOutErrorMessage.PACKET_CODEC;
        };
    }
}
