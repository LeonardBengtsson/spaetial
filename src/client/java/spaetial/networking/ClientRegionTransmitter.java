package spaetial.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import spaetial.ClientConfig;
import spaetial.schematic.SchematicPlacement;
import spaetial.networking.c2s.SchematicUploadHeadC2SPacket;
import spaetial.networking.c2s.SchematicUploadPartC2SPacket;

import java.util.UUID;

/**
 * Used for transmitting a region from the client to the server, such as when uploading a schematic stored on the client
 */
public final class ClientRegionTransmitter {
    private ClientRegionTransmitter() {}

    /**
     * Uploads a schematic placement to the server split up into multiple packets as to avoid hitting the packet size limit
     */
    public static void uploadSchematicPlacement(UUID placementId, SchematicPlacement schematicPlacement) {
        var uploadId = UUID.randomUUID();
        ClientPlayNetworking.send(new SchematicUploadHeadC2SPacket(uploadId, placementId, schematicPlacement.minPos, schematicPlacement.dim, schematicPlacement.schematic.metadata()));

        byte[] data = schematicPlacement.schematic.region().encode();

        int maxPacketDataSize = ClientConfig.Persistent.getMaxPacketSize() - 24;
        int totalPacketCount = (maxPacketDataSize - 1 + data.length) / maxPacketDataSize;

        for (int i = 0; i < totalPacketCount; i++) {
            int position = i * maxPacketDataSize;

            byte[] packetData;
            if (i < totalPacketCount - 1) {
                packetData = new byte[maxPacketDataSize];
                System.arraycopy(data, position, packetData, 0, maxPacketDataSize);
            } else {
                var size = data.length % maxPacketDataSize;
                packetData = new byte[size];
                System.arraycopy(data, position, packetData, 0, size);
            }

            ClientPlayNetworking.send(new SchematicUploadPartC2SPacket(uploadId, totalPacketCount, i, packetData));
        }
    }
}
