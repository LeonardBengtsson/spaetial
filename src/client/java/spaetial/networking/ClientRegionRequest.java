package spaetial.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import spaetial.ClientConfig;
import spaetial.Spaetial;
import spaetial.editing.ClientManager;
import spaetial.editing.selection.Selection;
import spaetial.editing.region.Region;
import spaetial.networking.c2s.RequestClipboardRegionC2SPacket;
import spaetial.networking.c2s.RequestSharedSchematicPlacementRegionC2SPacket;
import spaetial.networking.c2s.RequestRegionC2SPacket;
import spaetial.networking.message.RegionRequestTimedOutErrorMessage;
import spaetial.networking.s2c.RegionDimensionsS2CPacket;
import spaetial.networking.s2c.RegionPartS2CPacket;
import spaetial.util.WorldUtil;
import spaetial.util.encoding.ByteArrayUtil;
import spaetial.util.encoding.ByteDecoderException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Used to request region data from the server, or, in cases where the client has already loaded in the requested region, grabs the region from the client's cache
 */
public final class ClientRegionRequest {
    private ClientRegionRequest() {}

    /**
     * Keeps track of all the requests that have been made and have not yet expired
     *
     * @see ClientConfig.Persistent#regionRequestExpirationMillis
     */
    private static final HashMap<UUID, RequestedRegion> requestedRegions = new HashMap<>();

    private static void removeEntry(UUID id) {
        requestedRegions.remove(id);
    }

    /**
     * Checks for and removes expired requests
     *
     * @see ClientConfig.Persistent#regionRequestExpirationMillis
     */
    public static void onTick(MinecraftClient client) {
        var currentTime = System.currentTimeMillis();
        var expiredRequests = new ArrayList<UUID>();
        requestedRegions.forEach((uuid, entry) -> {
            if (entry.timeout >= 0 && currentTime > entry.timestamp + entry.timeout) {
                ClientManager.receiveMessage(client, new RegionRequestTimedOutErrorMessage());
                expiredRequests.add(uuid);
            }
        });
        expiredRequests.forEach(requestedRegions::remove);
    }

    /**
     * Requests any region in the world given a selection and dimension
     *
     * @param requireBlockEntities If true, will request any block entities from the server as they are not present on the client-side
     * @param onSuccess            Consumes the {@code Region} once it has been received from the server
     */
    public static void requestRegion(MinecraftClient client, Selection selection, RegistryKey<World> dimension, boolean requireBlockEntities, Consumer<Region> onSuccess) {
        // determine whether the region in the specified selection is loaded on the client, and either return it directly or request it from the server
        // note: it is never guaranteed that the consumer will ever be accepted

        if (client.world != null && client.world.getRegistryKey() == dimension) {
            var bounds = selection.getOuterBounds();
            if (WorldUtil.isLoaded(client.world, bounds)) {
                if (requireBlockEntities) {
                    // TODO only request block entities
                    // requestBlockEntitiesFromServer(selection, dimension, consumer);
                    requestFromServer(selection, dimension, onSuccess, vec -> {});
                    return;
                }
                onSuccess.accept(Region.create(client.world, selection));
                return;
            }
        }

        requestFromServer(selection, dimension, onSuccess, vec -> {});
    }

    /**
     * Requests the region for a shared schematic placement that the player participates in, given a {@code placementId}
     *
     * @param onSuccess Consumes the {@code Region} once it has been received from the server
     */
    public static void requestSharedSchematicPlacementRegion(UUID placementId, Consumer<Region> onSuccess) {
        var requestId = UUID.randomUUID();
        requestedRegions.put(requestId, new RequestedRegion(onSuccess, vec -> {}, requestId, -1));

        ClientPlayNetworking.send(new RequestSharedSchematicPlacementRegionC2SPacket(requestId, placementId));
    }

    /**
     * Requests the region stored in the player's clipboard
     *
     * @param onSuccess Consumes the {@code Region} once it has been received from the server
     */
    public static void requestClipboardRegion(Consumer<Region> onSuccess, Consumer<Vec3i> onDimensionsReceived) {
        var requestId = UUID.randomUUID();
        requestedRegions.put(requestId, new RequestedRegion(onSuccess, onDimensionsReceived, requestId, -1));

        ClientPlayNetworking.send(new RequestClipboardRegionC2SPacket(requestId));
    }

    /**
     * Used when a request for the clipboard region has already been made through other channels
     *
     * @param requestId The pre-determined request id
     * @param onSuccess Consumes the {@code Region} once it has been received from the server
     */
    public static void hasRequestedClipboardRegion(UUID requestId, Consumer<Region> onSuccess, Consumer<Vec3i> onDimensionsReceived) {
        requestedRegions.put(requestId, new RequestedRegion(onSuccess, onDimensionsReceived, requestId, -1));
    }

    /**
     * Requests a region from the server if it's not loaded on the client
     */
    private static void requestFromServer(Selection selection, RegistryKey<World> dim, Consumer<Region> onSuccess, Consumer<Vec3i> onDimensionsReceived) {
        var requestId = UUID.randomUUID();
        requestedRegions.put(requestId, new RequestedRegion(onSuccess, onDimensionsReceived, requestId, ClientConfig.Persistent.getRegionRequestExpirationMillis()));
        ClientPlayNetworking.send(new RequestRegionC2SPacket(requestId, selection, dim));
    }

    public static void receivePartPacket(RegionPartS2CPacket packet) {
        var entry = requestedRegions.get(packet.requestId());
        if (entry != null) {
            try {
                entry.receivePartPacket(packet);
            } catch (Throwable e) {
                Spaetial.warn("Received invalid region part packet with id " + packet.requestId(), e.toString());
                requestedRegions.remove(packet.requestId());
            }
        } else {
            Spaetial.warn("Received region part packet with id " + packet.requestId() + ", but no region with that id was requested, or there was a request that expired");
        }
    }

    public static void receiveDimensionsPacket(RegionDimensionsS2CPacket packet) {
        var entry = requestedRegions.get(packet.requestId());
        if (entry != null) {
            entry.receiveDimensionsPacket(packet);
        } else {
            Spaetial.warn("Received region dimensions packet with id " + packet.requestId() + ", but no region with that id was requested, or there was a request that expired");
        }
    }

    /**
     * Contains all the information about a request
     */
    private static class RequestedRegion {
        /**
         * The timestamp for the request's creation
         *
         * @see System#currentTimeMillis()
         */
        public final long timestamp;

        public final long timeout;

        private final Consumer<Region> onSuccess;
        private final Consumer<Vec3i> onDimensionsReceived;
        private final UUID ownId;

        /**
         * An array containing the received packets' data as byte arrays
         */
        private byte[][] packets = null;
        /**
         * An array containing booleans representing whether the packet of that number has been received or not. Used for
         * throwing an error if two packets with the same index are received
         */
        private boolean[] hasReceived = null;
        private int totalCount = -1;
        private int receivedCount = 0;

        public RequestedRegion(Consumer<Region> onSuccess, Consumer<Vec3i> onDimensionsReceived, UUID ownId, long timeout) {
            this.timestamp = System.currentTimeMillis();
            this.onSuccess = onSuccess;
            this.onDimensionsReceived = onDimensionsReceived;
            this.ownId = ownId;
            this.timeout = timeout;
        }

        public void receivePartPacket(RegionPartS2CPacket packet) throws IllegalArgumentException, ByteDecoderException {
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
                onSuccess.accept(region);
                ClientRegionRequest.removeEntry(ownId);
            }
        }

        public void receiveDimensionsPacket(RegionDimensionsS2CPacket packet) {
            onDimensionsReceived.accept(packet.dimensions());
        }
    }
}
