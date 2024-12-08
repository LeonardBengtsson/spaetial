package spaetial.editing.state;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import org.apache.commons.lang3.NotImplementedException;
import spaetial.Spaetial;
import spaetial.editing.state.common.*;
import spaetial.networking.PacketCodecSupplier;
import spaetial.networking.PacketCodecsUtil;
import spaetial.util.Translatable;

public enum EditingStateType implements Translatable, PacketCodecSupplier<PacketByteBuf, CommonEditingState> {
    NORMAL("normal"),
    TURNED_OFF("turned_off"),
    CUBOID_SELECTION("cuboid_selection"),
    CARVE_SELECTION("carve_selection"),
    COPY("copy"),
    ACTIVE_SCHEMATIC_PLACEMENT("active_schematic_placement");

    public final String name;

    EditingStateType(String name) {
        this.name = name;
    }

    @Override
    public String getTranslationKey() {
        return Spaetial.translationKey(null, "editing_state", name, "name");
    }

    @Override
    public PacketCodec<PacketByteBuf, ? extends CommonEditingState> getCodec() {
        return switch (this) {
            case NORMAL -> CommonNormalState.PACKET_CODEC;
            case TURNED_OFF -> CommonTurnedOffState.PACKET_CODEC;
            case CUBOID_SELECTION -> CommonCuboidSelectionState.PACKET_CODEC;
            case CARVE_SELECTION -> throw new NotImplementedException();
            case COPY -> CommonCopyState.PACKET_CODEC;
            case ACTIVE_SCHEMATIC_PLACEMENT -> CommonActiveSchematicPlacementState.PACKET_CODEC;
        };
    }
}
