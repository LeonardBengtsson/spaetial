package spaetial.networking.s2c;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import org.jetbrains.annotations.Nullable;
import spaetial.Spaetial;
import spaetial.editing.operation.OperationType;
import spaetial.networking.PacketCodecsUtil;
import spaetial.editing.OperationAction;

public record OperationActionVolumeConfirmationPromptS2CPacket(
    OperationAction action, int volume, int maxVolume,
    boolean needsOperatorLevel, @Nullable OperationType operationType
) implements CustomPayload {
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
    public static final Id<OperationActionVolumeConfirmationPromptS2CPacket> ID = new Id<>(Spaetial.id("operation_action_volume_confirmation_prompt"));
    public static final PacketCodec<RegistryByteBuf, OperationActionVolumeConfirmationPromptS2CPacket> PACKET_CODEC = PacketCodec.tuple(
        PacketCodecsUtil.createEnum(OperationAction.class), OperationActionVolumeConfirmationPromptS2CPacket::action,
        PacketCodecs.INTEGER, OperationActionVolumeConfirmationPromptS2CPacket::volume,
        PacketCodecs.INTEGER, OperationActionVolumeConfirmationPromptS2CPacket::maxVolume,
        PacketCodecs.BOOLEAN, OperationActionVolumeConfirmationPromptS2CPacket::needsOperatorLevel,
        PacketCodecsUtil.createNullable(PacketCodecsUtil.createEnum(OperationType.class)), OperationActionVolumeConfirmationPromptS2CPacket::operationType,
        OperationActionVolumeConfirmationPromptS2CPacket::new
    );
}
