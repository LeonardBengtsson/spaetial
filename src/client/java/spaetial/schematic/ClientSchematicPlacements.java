package spaetial.schematic;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import spaetial.ClientConfig;
import spaetial.Spaetial;
import spaetial.editing.ClientManager;
import spaetial.editing.Filter;
import spaetial.editing.state.ActiveSchematicPlacementState;
import spaetial.networking.ClientRegionRequest;
import spaetial.networking.ClientRegionTransmitter;
import spaetial.networking.c2s.*;
import spaetial.networking.s2c.SharedSchematicPlacementAddedS2CPacket;
import spaetial.networking.s2c.SharedSchematicPlacementRemovedS2CPacket;
import spaetial.util.BoxUtil;

import java.util.HashMap;
import java.util.UUID;
import java.util.function.BiConsumer;

/**
 * Manages the client's own schematic placements as well as caches of other player's shared schematic placements
 */
public final class ClientSchematicPlacements {
    private ClientSchematicPlacements() {}

    private static final HashMap<UUID, SchematicPlacement> schematicPlacements = new HashMap<>();
    private static final HashMap<UUID, SharedSchematicPlacement> ownSharedSchematicPlacements = new HashMap<>();
    private static final HashMap<UUID, ClientSharedSchematicPlacementInfo> othersSharedSchematicPlacements = new HashMap<>();

    public static void clear() {
        // TODO remove method or send packet to server?
        schematicPlacements.clear();
        ownSharedSchematicPlacements.clear();
        othersSharedSchematicPlacements.clear();
    }

    public static @Nullable SchematicPlacement getSchematicPlacement(UUID id) {
        var schem = schematicPlacements.get(id);
        if (schem == null) {
            var sharedSchem = ownSharedSchematicPlacements.get(id);
            if (sharedSchem != null) {
                return sharedSchem.schematicPlacement;
            }
        }
        return schem;
    }

    /**
     * @param setActive Whether to change the client's state to set the placed schematic as active
     * @param upload    Whether the schematic comes from a source on the client and needs to be uploaded to the server
     */
    public static void addSchematicPlacement(ClientPlayerEntity player, boolean surface, boolean setActive, Schematic schematic, boolean upload) {
        var id = UUID.randomUUID();
        var placement = new SchematicPlacement(
            schematic,
            BoxUtil.boxPositioning(player, ClientConfig.Persistent.getMaxRaycastRange(), ClientConfig.getTargetSurface(surface), schematic.region().dimensions()),
            player.clientWorld.getRegistryKey()
        );
        schematicPlacements.put(id, placement);
        if (setActive) ClientManager.switchEditingState(new ActiveSchematicPlacementState(id));
        if (upload) {
            ClientRegionTransmitter.uploadSchematicPlacement(id, placement);
        } else {
            ClientPlayNetworking.send(new PasteClipboardC2SPacket(id, placement.minPos, player.clientWorld.getRegistryKey()));
        }
    }

    public static void removeSchematicPlacement(UUID placementId) {
        schematicPlacements.remove(placementId);
        ownSharedSchematicPlacements.remove(placementId);
        ClientPlayNetworking.send(new RemoveSchematicPlacementC2SPacket(placementId));
    }

    public static void moveSchematicPlacement(UUID placementId, BlockPos newMinPos, RegistryKey<World> dim) {
        var placement = schematicPlacements.get(placementId);
        if (placement == null) {
            var sharedPlacement = ownSharedSchematicPlacements.get(placementId);
            if (sharedPlacement != null) {
                ownSharedSchematicPlacements.put(placementId, sharedPlacement.move(newMinPos, dim));
            }
        } else {
            schematicPlacements.put(placementId, placement.move(newMinPos, dim));
        }
        ClientPlayNetworking.send(new MoveSchematicPlacementC2SPacket(placementId, newMinPos, dim));
    }

    public static void completeSchematicPlacement(UUID placementId, Filter filter) {
        ClientPlayNetworking.send(new RequestCompleteSchematicOperationC2SPacket(placementId, filter));
        // TODO might not wanna remove prematurely in case no permission
        schematicPlacements.remove(placementId);
    }

    public static void toggleSharingSchematicPlacement(UUID placementId, boolean toggle, UUID ownPlayerId) {
        if (toggle) {
            var placement = schematicPlacements.get(placementId);
            if (placement == null) return;
            ownSharedSchematicPlacements.put(placementId, new SharedSchematicPlacement(placement, ownPlayerId));
            schematicPlacements.remove(placementId);
        } else {
            var placement = ownSharedSchematicPlacements.get(placementId);
            if (placement == null) return;
            schematicPlacements.put(placementId, placement.schematicPlacement);
            ownSharedSchematicPlacements.remove(placementId);
        }
        ClientPlayNetworking.send(new ToggleSharingSchematicPlacementC2SPacket(placementId, toggle));
    }

    public static void toggleParticipationSharedSchematicPlacement(UUID placementId, boolean toggle) {
        var placement = othersSharedSchematicPlacements.get(placementId);
        if (placement != null) {
            placement.setParticipating(toggle);
            if (toggle) {
                ClientRegionRequest.requestSharedSchematicPlacementRegion(placementId, placement::setRegion);
            } else {
                placement.setRegion(null);
            }
            ClientPlayNetworking.send(new ToggleParticipationSharedSchematicPlacementC2SPacket(placementId, toggle));
        }
    }

    public static void handleSharedSchematicPlacementAddedS2CPacket(SharedSchematicPlacementAddedS2CPacket packet) {
        if (schematicPlacements.containsKey(packet.placementId())) {
            Spaetial.warn("Server tried to toggle on sharing for client's own schematic placement");
            return;
        }
        othersSharedSchematicPlacements.put(packet.placementId(), new ClientSharedSchematicPlacementInfo(packet.info()));
    }

    public static void handleSharedSchematicPlacementRemovedPacket(SharedSchematicPlacementRemovedS2CPacket packet) {
        if (ownSharedSchematicPlacements.containsKey(packet.placementId())) {
            Spaetial.warn("Server tried to toggle off sharing for client's own schematic placement");
        }
        othersSharedSchematicPlacements.remove(packet.placementId());
    }

    public static void iterateSchematicPlacements(BiConsumer<UUID, SchematicPlacement> consumer) {
        schematicPlacements.forEach(consumer);
    }

    public static void iterateOwnSharedSchematicPlacements(BiConsumer<UUID, SharedSchematicPlacement> consumer) {
        ownSharedSchematicPlacements.forEach(consumer);
    }

    public static void iterateOthersSharedSchematicPlacements(BiConsumer<UUID, ClientSharedSchematicPlacementInfo> consumer) {
        othersSharedSchematicPlacements.forEach(consumer);
    }
}
