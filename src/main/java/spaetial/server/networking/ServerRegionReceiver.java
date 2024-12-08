package spaetial.server.networking;

import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import spaetial.Spaetial;
import spaetial.schematic.SchematicPlacement;
import spaetial.schematic.Schematic;
import spaetial.schematic.SchematicMetadata;
import spaetial.editing.region.Region;
import spaetial.networking.c2s.SchematicUploadPartC2SPacket;
import spaetial.server.editing.ServerManager;
import spaetial.util.encoding.ByteArrayUtil;
import spaetial.util.encoding.ByteDecoderException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public final class ServerRegionReceiver {
    private ServerRegionReceiver() {}

    private static final long EXPIRATION_MILLIS = 60000;

    private static final HashMap<UUID, StartedTransmission> startedTransmissions = new HashMap<>();
    private static final HashMap<UUID, EarlyPackets> earlyPackets = new HashMap<>();

    private static void removeEntry(UUID id) {
        startedTransmissions.remove(id);
        earlyPackets.remove(id);
    }

    public static void onTick() {
        var currentTime = System.currentTimeMillis();
        var expiredRequests = new ArrayList<UUID>();
        startedTransmissions.forEach((uuid, container) -> {
            if (currentTime > container.timestamp + EXPIRATION_MILLIS) {
                expiredRequests.add(uuid);
            }
        });
        earlyPackets.forEach((uuid, container) -> {
            if (currentTime > container.timestamp + EXPIRATION_MILLIS) {
                expiredRequests.add(uuid);
            }
        });
        expiredRequests.forEach(ServerRegionReceiver::removeEntry);
    }

    public static void receiveHeadPacket(UUID uploadId, UUID schematicId, UUID playerId, BlockPos minPos, RegistryKey<World> dim, SchematicMetadata metadata) {
        var earlyPacketsContainer = earlyPackets.get(uploadId);
        StartedTransmission container;
        if (earlyPacketsContainer == null) {
            container = new StartedTransmission(schematicId, playerId, minPos, dim, metadata, uploadId);
        } else {
            container = new StartedTransmission(earlyPacketsContainer.timestamp, schematicId, playerId, minPos, dim, metadata, uploadId);
        }
        startedTransmissions.put(uploadId, container);
        if (earlyPacketsContainer != null) {
            earlyPacketsContainer.list.forEach(packet -> {
                try {
                    container.receivePacket(packet);
                } catch (Throwable e) {
                    Spaetial.warn("Received invalid region part packet with id " + uploadId, e.toString());
                    startedTransmissions.remove(uploadId);
                    earlyPackets.remove(uploadId);
                }
            });
            earlyPackets.remove(uploadId);
        }
    }

    public static void receivePartPacket(SchematicUploadPartC2SPacket packet) {
        var entry = startedTransmissions.get(packet.uploadId());
        if (entry == null) {
            var earlyPacketsContainer = earlyPackets.computeIfAbsent(packet.uploadId(), uuid -> new EarlyPackets(System.currentTimeMillis(), new ArrayList<>()));
            earlyPacketsContainer.list.add(packet);
        } else {
            try {
                entry.receivePacket(packet);
            } catch (Throwable e) {
                Spaetial.warn("Received invalid region part packet with id " + packet.uploadId(), e.toString());
                startedTransmissions.remove(packet.uploadId());
                earlyPackets.remove(packet.uploadId());
            }
        }
    }

    private static class StartedTransmission {
        public final long timestamp;

        public final UUID schematicId;
        private final UUID playerId;
        public final BlockPos minPos;
        public final RegistryKey<World> dim;
        public final SchematicMetadata metadata;

        private final UUID ownTransmissionId;

        private byte[][] packets = null;
        private boolean[] hasReceived = null;
        private int totalCount = -1;
        private int receivedCount = 0;

        private StartedTransmission(UUID schematicId, UUID playerId, BlockPos minPos, RegistryKey<World> dim, SchematicMetadata metadata, UUID ownTransmissionId) {
            this(System.currentTimeMillis(), schematicId, playerId, minPos, dim, metadata, ownTransmissionId);
        }

        private StartedTransmission(long timestamp, UUID schematicId, UUID playerId, BlockPos minPos, RegistryKey<World> dim, SchematicMetadata metadata, UUID ownTransmissionId) {
            this.timestamp = timestamp;

            this.schematicId = schematicId;
            this.playerId = playerId;
            this.minPos = minPos;
            this.dim = dim;
            this.metadata = metadata;
            this.ownTransmissionId = ownTransmissionId;
        }

        public void receivePacket(SchematicUploadPartC2SPacket packet) throws IllegalArgumentException, ByteDecoderException {
            if (totalCount == -1) {
                totalCount = packet.totalPacketCount();
                packets = new byte[totalCount][];
                hasReceived = new boolean[totalCount];
            } else if (packet.totalPacketCount() != totalCount) {
                throw new IllegalArgumentException("Packet count " + packet.totalPacketCount() + " doesn't match the expected amount of " + this.totalCount);
            }

            if (packet.packetIndex() >= totalCount) {
                throw new IllegalArgumentException("Packet index " + packet.packetIndex() + " is too high. Expected package count is " + this.totalCount);
            }

            if (hasReceived[packet.packetIndex()]) {
                throw new IllegalArgumentException("Packet with index " + packet.packetIndex() + " has already been received");
            }

            packets[packet.packetIndex()] = packet.data();
            hasReceived[packet.packetIndex()] = true;
            receivedCount++;

            if (receivedCount >= totalCount) {
                var data = ByteArrayUtil.flattenArrayOfByteArrays(packets, 0);
                var region = Region.decode(data);
                ServerManager.addSchematicPlacement(playerId, schematicId, new SchematicPlacement(
                    new Schematic(
                        region,
                        metadata
                    ),
                    minPos,
                    dim
                ));
                ServerRegionReceiver.removeEntry(ownTransmissionId);
            }
        }
    }

    private record EarlyPackets(long timestamp, List<SchematicUploadPartC2SPacket> list) { }
}
