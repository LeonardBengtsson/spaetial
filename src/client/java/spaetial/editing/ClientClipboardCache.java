package spaetial.editing;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3i;
import org.jetbrains.annotations.Nullable;
import spaetial.editing.region.Region;
import spaetial.editing.state.ActiveSchematicPlacementState;
import spaetial.networking.ClientRegionRequest;
import spaetial.networking.message.OutdatedRequestErrorMessage;
import spaetial.schematic.ClientSchematicPlacements;
import spaetial.schematic.Schematic;
import spaetial.schematic.SchematicMetadata;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

public final class ClientClipboardCache {
    private ClientClipboardCache() {}

    private static @Nullable Region clipboardRegion = null;
    private static @Nullable Vec3i clipboardDimensions = null;
    private static @Nullable UUID mostRecentRequestId = null;
    private static boolean crossWorldClipboard = false;

    @Deprecated
    private static final List<Listener> listeners = new ArrayList<>();

    public static void onLeaveWorld() {
        crossWorldClipboard = true;
    }

    public static boolean hasClipboard() {
        return mostRecentRequestId != null || clipboardRegion != null || clipboardDimensions != null;
    }

    public static boolean hasClipboardRegion() {
        return clipboardRegion != null;
    }

    public static boolean hasClipboardDimensions() {
        return clipboardDimensions != null && clipboardRegion != null;
    }

    public static @Nullable Region getClipboardRegion() {
        return clipboardRegion;
    }
    public static @Nullable Vec3i getClipboardDimensions() {
        return clipboardRegion == null ? clipboardDimensions : new Vec3i(clipboardRegion.sx, clipboardRegion.sy, clipboardRegion.sz);
    }

    @Deprecated
    public static void addListener(Listener listener) {
        listeners.add(listener);
    }

    public static void clear() {
        clipboardRegion = null;
        clipboardDimensions = null;
        crossWorldClipboard = false;
        listeners.clear();
    }

    public static void clear(UUID requestId) {
        if (requestId == mostRecentRequestId) clear();
    }

    /**
     * @param setActive Whether to change the client's state to set the placed schematic as active
     *
     * @see ActiveSchematicPlacementState
     */
    public static boolean placeClipboard(ClientPlayerEntity player, boolean surface, boolean setActive) {
        if (!hasClipboardRegion()) return false;
        ClientSchematicPlacements.addSchematicPlacement(
            player,
            surface,
            setActive,
            new Schematic(
                getClipboardRegion(),
                SchematicMetadata.createClipboardInfo(player.getUuid())
            ),
            crossWorldClipboard
        );
        return true;
    }

    /**
     * Used when a request for the clipboard region has already been made through other channels
     *
     * @param requestId The pre-determined request id
     * @param onSuccess Consumes the {@code Region} once it has been received
     */
    public static void prepareToReceive(MinecraftClient client, UUID requestId, @Nullable Consumer<Region> onSuccess) {
        clear();
        mostRecentRequestId = requestId;
        var success = Objects.requireNonNullElse(onSuccess, region -> {});
        ClientRegionRequest.hasRequestedClipboardRegion(
            requestId,
            region -> {
                if (mostRecentRequestId == requestId) {
                    clear();
                    clipboardRegion = region;
                    clipboardDimensions = new Vec3i(region.sx, region.sy, region.sz);
                    success.accept(region);
                    listeners.forEach(listener -> listener.regionConsumer.accept(region));
                } else {
                    ClientManager.receiveMessage(client, new OutdatedRequestErrorMessage());
                }
            },
            vec -> {
                if (mostRecentRequestId == requestId && clipboardRegion == null) {
                    clipboardDimensions = vec;
                    listeners.forEach(listener -> listener.dimensionsConsumer.accept(vec));
                }
            }
        );
    }


    @Deprecated
    // TODO replace with interface
    public record Listener(Consumer<Region> regionConsumer, Consumer<Vec3i> dimensionsConsumer) {}
}
