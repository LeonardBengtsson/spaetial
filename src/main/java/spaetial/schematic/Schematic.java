package spaetial.schematic;

import spaetial.Spaetial;
import spaetial.editing.region.Region;
import spaetial.util.encoding.ByteArrayReader;
import spaetial.util.encoding.ByteArrayWriter;
import spaetial.util.encoding.ByteDecoderException;
import spaetial.util.encoding.InvalidSignatureException;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public record Schematic(Region region, SchematicMetadata metadata) {
    private static final String VERSION = "v2";

    private static final byte[] ENCODING_SIGNATURE = (Spaetial.MOD_ID + "-" + VERSION + "-").getBytes(StandardCharsets.UTF_8);

    public static Schematic decode(byte[] data) throws InvalidSignatureException, ByteDecoderException {
        var reader = new ByteArrayReader(data);
        try {
            var signature = reader.readArray(ENCODING_SIGNATURE.length);
            if (!Arrays.equals(signature, ENCODING_SIGNATURE)) {
                throw new InvalidSignatureException("Error while decoding schematic data: Invalid signature prefix");
            }
            var metadata = SchematicMetadata.decode(reader);
            var region = Region.decode(reader);
            return new Schematic(region, metadata);
        } catch (InvalidSignatureException e) {
            throw e;
        } catch (Throwable e) {
            throw new ByteDecoderException(e);
        }
    }

    public byte[] encode() {
        var writer = new ByteArrayWriter();
        writer.write(ENCODING_SIGNATURE);
        writer.write(metadata.encode());
        writer.write(region.encode());
        return writer.get();
    }
}
