package spaetial.server.permissions;

import net.minecraft.server.network.ServerPlayerEntity;
import spaetial.editing.operation.Operation;
import spaetial.editing.operation.UndoableOperation;

public final class EditingPermissions {
    private EditingPermissions() {}

    public static boolean mayRequestRegion(ServerPlayerEntity player) {
        return PermissionLevel.VIEWER.hasLevel(player);
    }

    public static boolean mayViewOthersStates(ServerPlayerEntity player) {
        return PermissionLevel.VIEWER.hasLevel(player);
    }

    public static boolean mayRequestOperation(ServerPlayerEntity player, Operation operation) {
        return operation.getType().permissionLevel.hasLevel(player);
    }

    public static boolean mayRequestUndo(ServerPlayerEntity player, UndoableOperation operation) {
        return operation.getType().permissionLevel.hasLevel(player);
    }

    public static boolean mayRequestRedo(ServerPlayerEntity player, UndoableOperation operation) {
        return operation.getType().permissionLevel.hasLevel(player);
    }

    public static boolean mayForceOperation(ServerPlayerEntity player, Operation operation) {
        return PermissionLevel.OPERATOR.hasLevel(player);
    }
}
