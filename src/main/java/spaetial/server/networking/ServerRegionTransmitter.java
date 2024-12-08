package spaetial.server.networking;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3i;
import org.jetbrains.annotations.Nullable;
import spaetial.Spaetial;
import spaetial.editing.region.Region;
import spaetial.networking.s2c.RegionDimensionsS2CPacket;
import spaetial.networking.s2c.RegionPartS2CPacket;
import spaetial.server.ServerConfig;

import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

public final class ServerRegionTransmitter {
    private ServerRegionTransmitter() {}

    // TODO change this value
    // regions with a volume less than this don't need to be added to the queue and can be completed instantly
    private static final int EXECUTE_INSTANTLY = 512;

    private static final Queue<Request> requests = new LinkedList<>();
    private static @Nullable Thread workerThread = null;

    public static void sendRegion(MinecraftServer server, UUID requestId, Region region, ServerPlayerEntity player, boolean sendDimensions) {
        if (sendDimensions) {
            ServerPlayNetworking.send(player, new RegionDimensionsS2CPacket(requestId, new Vec3i(region.sx, region.sy, region.sz)));
        }

        var request = new Request(player.getUuid(), requestId, region);
        if (requests.size() == 0 && region.volume <= EXECUTE_INSTANTLY) {
            perform(server, request);
        } else {
            requests.add(request);
            if (requests.size() == 1) onTick(server);
        }
    }

    private static void perform(MinecraftServer server, Request request) {
        byte[] data = request.region.encode();

        int maxPacketDataSize = ServerConfig.getMaxPacketSize() - 24;
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

            var receiver = server.getPlayerManager().getPlayer(request.receiverId);
            if (receiver == null) {
                Spaetial.warn("Tried to send region part to player that doesn't exist (id: " + request.receiverId + ")");
                return;
            }
            ServerPlayNetworking.send(receiver, new RegionPartS2CPacket(request.requestId, totalPacketCount, i, packetData));
        }
    }

    public static void onTick(MinecraftServer server) {
        if (requests.size() > 0 && (workerThread == null || !workerThread.isAlive())) {
            var entry = requests.remove();
            workerThread = new Thread(() -> perform(server, entry));
            workerThread.start();
        }
    }

    public static boolean isProcessing() {
        return requests.size() != 0 || (workerThread != null && workerThread.isAlive());
    }

    public static void sync(MinecraftServer server) {
        if (workerThread != null) {
            try {
                workerThread.join();
            }
            catch (Throwable ignored) {}
        }
        for (var entry : requests) {
            perform(server, entry);
        }
        requests.clear();
    }

    private record Request(UUID receiverId, UUID requestId, Region region) {}
}
