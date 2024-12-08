package spaetial.server;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import spaetial.server.editing.ServerManager;
import spaetial.server.networking.ServerNetworking;
import spaetial.server.networking.ServerRegionReceiver;
import spaetial.server.networking.ServerRegionTransmitter;

public final class ServerEvents {
    private ServerEvents() {}

    public static void onServerTick(MinecraftServer server) {
        ServerManager.onTick(server);
        ServerRegionReceiver.onTick();
        ServerRegionTransmitter.onTick(server);
    }

    public static void onServerStarting(MinecraftServer server) {
        // TODO load config
    }

    public static void onServerStopping(MinecraftServer server) {
        ServerRegionTransmitter.sync(server);
        ServerManager.onServerStopping(server);
        // TODO save config
    }

    public static void onPlayerJoins(ServerPlayNetworkHandler serverPlayNetworkHandler, PacketSender packetSender, MinecraftServer server) {
        var id = serverPlayNetworkHandler.player.getUuid();
        // TODO load player data from file
    }

    public static void onPlayerDisconnects(ServerPlayNetworkHandler serverPlayNetworkHandler, MinecraftServer server) {
        var id = serverPlayNetworkHandler.player.getUuid();

        ServerNetworking.sendMultiplayerCacheRemovedPackets(server, serverPlayNetworkHandler.player);

        // TODO save player data to file
        ServerManager.savePlayerDataToFile();
        ServerManager.setPlayerConfig(id, null);
        // TODO remove player data after syncing operation queue
    }
}
