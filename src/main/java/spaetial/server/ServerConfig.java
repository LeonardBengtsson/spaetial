package spaetial.server;

import net.minecraft.server.network.ServerPlayerEntity;
import spaetial.editing.operation.Operation;
import spaetial.editing.region.Region;
import spaetial.server.networking.ServerNetworking;
import spaetial.server.permissions.EditingPermissions;
import spaetial.server.permissions.VolumePermissionStatus;
import spaetial.server.permissions.PermissionRequirement;

import java.util.UUID;

public final class ServerConfig {
    private ServerConfig() {}

    // EDITING / PERFORMANCE
    private static int maxHistory = 15;
    /**
     * Decides the operation volume at which the mod will ask the player to confirm the operation and give them the
     * option to skip saving the operation to their editing history
     */
    private static int operationVolumeSoftLimit = 100000;
    /**
     * Decides the operation volume at which non-operator players are not allowed to perform the operation. This limit
     * can still be bypassed by operators
     */
    private static int operationVolumeHardLimit = 1000000;

    public static VolumePermissionStatus checkOperationVolume(ServerPlayerEntity player, Operation operation) {
        var volume = operation.getVolume();
        if (volume <= operationVolumeSoftLimit) {
            return new VolumePermissionStatus(VolumePermissionStatus.Status.OK, volume, operationVolumeSoftLimit);
        } else if (volume <= operationVolumeHardLimit) {
            return new VolumePermissionStatus(VolumePermissionStatus.Status.NEEDS_CONFIRM, volume, operationVolumeSoftLimit);
        } else {
            return new VolumePermissionStatus(
                EditingPermissions.mayForceOperation(player, operation) ? VolumePermissionStatus.Status.OPERATOR_CONFIRM : VolumePermissionStatus.Status.DENIED,
                volume, operationVolumeHardLimit
            );
        }
    }

    /**
     * @return How many operations are saved in each player's editing history
     */
    public static int getMaxHistory() {
        return maxHistory;
    }

    // NETWORKING
    // TODO change this
    private static int maxPacketSize = 0x100000;

    /**
     * @return The maximum size, in bytes, of packets sent from the server. Used for sending regions in parts to clients
     *
     * @see ServerNetworking#sendRegionPartPackets(UUID, Region, ServerPlayerEntity)
     */
    public static int getMaxPacketSize() { return maxPacketSize; }

    // PERMISSIONS
    private static PermissionRequirement viewerPermissionRequirement = PermissionRequirement.ANYONE;
    private static PermissionRequirement editorPermissionRequirement = PermissionRequirement.CREATIVE;
    public static PermissionRequirement getViewerPermissionRequirement() { return viewerPermissionRequirement; }
    public static PermissionRequirement getEditorPermissionRequirement() { return editorPermissionRequirement; }

}
