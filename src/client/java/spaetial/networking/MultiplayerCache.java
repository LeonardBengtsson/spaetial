package spaetial.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import spaetial.ClientConfig;
import spaetial.Spaetial;
import spaetial.editing.state.EditingState;
import spaetial.networking.c2s.RequestMultiplayerCacheUpdateC2SPacket;
import spaetial.networking.s2c.MultiplayerCacheRemovedS2CPacket;
import spaetial.networking.s2c.MultiplayerCacheUpdatedS2CPacket;

import java.util.HashMap;
import java.util.UUID;
import java.util.function.BiConsumer;

/**
 * Contains a cache representing the editing states of other players on the same world or server. Accurate cache data
 * relies on other players updating their state to the server and the server relaying that data properly, and so the
 * multiplayer cache won't work at all on unmodded servers, or for other players without the mod.
 */
public final class MultiplayerCache {
    private MultiplayerCache() {}

    private static final HashMap<UUID, EditingState> playerInfo = new HashMap<>();

    public static void iterate(BiConsumer<UUID, EditingState> consumer) {
        playerInfo.forEach(consumer);
    }

    /**
     * Receive a packet to update the cached state of any given player. Used when:
     * <ul>
     *     <li>A player changes their state</li>
     *     <li>A new player joins</li>
     *     <li>A player changes their config so that they go from not sharing to sharing their state</li>
     * </ul>
     *
     * @see ClientConfig.Persistent#shouldParticipateInMultiplayerCache
     */
    public static void receiveUpdatePacket(MinecraftClient client, MultiplayerCacheUpdatedS2CPacket packet) {
        if (client.player != null) {
            if (packet.playerId() == client.player.getUuid()) {
                Spaetial.warn("Received invalid update multiplayer cache packet with id of client's own player (" + packet.playerId().toString() + ")");
                return;
            }
        }
        var state = packet.state();
        playerInfo.put(packet.playerId(), EditingState.convertToClientLossy(state));
    }


    /**
     * Receive a packet to remove the cached state of any given player. Used when:
     * <ul>
     *     <li>A player leaves</li>
     *     <li>A player changes their config so that they no longer share their state</li>
     * </ul>
     *
     * @see ClientConfig.Persistent#shouldParticipateInMultiplayerCache
     */
    public static void receiveRemovePacket(MinecraftClient client, MultiplayerCacheRemovedS2CPacket packet) {
        playerInfo.remove(packet.playerId());
    }

    /**
     * Sends a packet requesting the server to update the player. Used when:
     * <ul>
     *     <li>The client joins a world or server</li>
     *     <li>The client toggles on participating in multiplayer cache</li>
     * </ul>
     *
     * @see ClientConfig.Persistent#shouldParticipateInMultiplayerCache
     */
    public static void requestUpdate() {
        ClientPlayNetworking.send(new RequestMultiplayerCacheUpdateC2SPacket());
    }

    /**
     * Clears all cached player data. Used when:
     * <ul>
     *     <li>The client disconnects from the world or server</li>
     *     <li>The client toggles off participating in multiplayer cache</li>
     * </ul>
     *
     * @see ClientConfig.Persistent#shouldParticipateInMultiplayerCache
     */
    public static void clear() {
        playerInfo.clear();
    }
}
