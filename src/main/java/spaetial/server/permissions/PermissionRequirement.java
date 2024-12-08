package spaetial.server.permissions;

import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Determines what is required to gain permission for a certain action
 *
 * @see PermissionLevel
 * @see EditingPermissions
 */
public enum PermissionRequirement {
    /**
     * Gives permission to any player
     */
    ANYONE,
    /**
     * Gives permission to any player who is in creative mode or spectator mode
     */
    CREATIVE_SPECTATOR,
    /**
     * Gives permission to any player who is in creative mode
     */
    CREATIVE,
    /**
     * Gives permission to any player who is an operator, regardless of game mode
     */
    OPERATOR;

    /**
     * @return Whether the player fulfills this permission requirement
     */
    boolean passes(ServerPlayerEntity player) {
        var op = player.hasPermissionLevel(player.server.getOpPermissionLevel());
        return switch (this) {
            case ANYONE -> true;
            case CREATIVE_SPECTATOR -> op || player.isCreative() || player.isSpectator();
            case CREATIVE -> op || player.isCreative();
            case OPERATOR -> op;
        };
    }
}
