package spaetial.schematic;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import org.jetbrains.annotations.Nullable;
import spaetial.networking.PacketCodecsUtil;
import spaetial.networking.VariantPacketCodec;
import spaetial.util.encoding.ByteArrayReader;
import spaetial.util.encoding.ByteArrayWriter;
import spaetial.util.encoding.ByteDecoderException;
import spaetial.util.functional.TypeSupplier;

import java.util.UUID;

public class SchematicMetadata implements TypeSupplier<SchematicMetadataType> {
    private final @Nullable FullSchematicInfo schematicInfo;
    private final @Nullable ClipboardInfo clipboardInfo;

    public static final PacketCodec<PacketByteBuf, SchematicMetadata> PACKET_CODEC
        = new VariantPacketCodec<>(SchematicMetadataType.class);
    public static final PacketCodec<PacketByteBuf, SchematicMetadata> FULL_SCHEMATIC_INFO_PACKET_CODEC = PacketCodecsUtil.createValueMap(
        FullSchematicInfo.PACKET_CODEC,
        info -> new SchematicMetadata(info, null),
        SchematicMetadata::getSchematicInfo
    );
    public static final PacketCodec<PacketByteBuf, SchematicMetadata> CLIPBOARD_INFO_PACKET_CODEC = PacketCodecsUtil.createValueMap(
        ClipboardInfo.PACKET_CODEC,
        info -> new SchematicMetadata(null, info),
        SchematicMetadata::getClipboardInfo
    );
    public static final PacketCodec<PacketByteBuf, SchematicMetadata> REGION_INFO_PACKET_CODEC
        = PacketCodecsUtil.createStatic(createRegionInfo());


    SchematicMetadata(@Nullable FullSchematicInfo schematicInfo, @Nullable ClipboardInfo clipboardInfo) {
        this.schematicInfo = schematicInfo;
        this.clipboardInfo = clipboardInfo;
    }

    public static SchematicMetadata createSchematicInfo(UUID id, String name, UUID authorId, String authorName) {
        return new SchematicMetadata(new FullSchematicInfo(id, name, authorId, authorName), null);
    }

    public static SchematicMetadata createClipboardInfo(UUID authorId) {
        return new SchematicMetadata(null, new ClipboardInfo(authorId));
    }

    public static SchematicMetadata createRegionInfo() {
        return new SchematicMetadata(null, null);
    }

    public static SchematicMetadata decode(ByteArrayReader reader) throws ByteDecoderException {
        try {
            var type = reader.read();
            switch (type) {
                case 0 -> {
                    var id = reader.readUuid();
                    var name = reader.readString();
                    var authorId = reader.readUuid();
                    var authorName = reader.readString();
                    return createSchematicInfo(id, name, authorId, authorName);
                }
                case 1 -> {
                    var authorId = reader.readUuid();
                    return createClipboardInfo(authorId);
                }
                case 2 -> {
                    return createRegionInfo();
                }
                default -> throw new IllegalStateException("Invalid schematic metadata type: " + type);
            }
        } catch (IndexOutOfBoundsException | IllegalStateException e) {
            throw new ByteDecoderException(e);
        }
    }

    public byte[] encode() {
        return switch (getType()) {
            case SCHEMATIC -> {
                var writer = new ByteArrayWriter();
                writer.write((byte) 0);
                writer.writeUuid(schematicInfo.id());
                writer.writeString(schematicInfo.name());
                writer.writeUuid(schematicInfo.authorId());
                writer.writeString(schematicInfo.authorName());
                yield writer.get();
            }
            case CLIPBOARD -> {
                var writer = new ByteArrayWriter();
                writer.write((byte) 1);
                writer.writeUuid(clipboardInfo.authorId());
                yield writer.get();
            }
            case REGION -> new byte[] { (byte) 2 };
        };
    }

    public FullSchematicInfo getSchematicInfo() throws IllegalStateException {
        if (schematicInfo == null) throw new IllegalStateException("Schematic metadata type " + getType().toString() + " can't supply full schematic info");
        return schematicInfo;
    }

    public ClipboardInfo getClipboardInfo() throws IllegalStateException {
        if (clipboardInfo == null) throw new IllegalStateException("Schematic metadata type " + getType().toString() + " can't supply clipboard info");
        return clipboardInfo;
    }

    public SchematicMetadataType getType() {
        if (schematicInfo != null) return SchematicMetadataType.SCHEMATIC;
        if (clipboardInfo != null) return SchematicMetadataType.CLIPBOARD;
        return SchematicMetadataType.REGION;
    }
}
