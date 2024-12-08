package spaetial.server.permissions;

import net.minecraft.server.network.ServerPlayerEntity;
import spaetial.server.ServerConfig;

/**
 * Specifies the permission levels that are used in the mod, where {@link PermissionLevel#VIEWER} and
 * {@link PermissionLevel#EDITOR} are configurable on the server side
 *
 * @see PermissionRequirement
 * @see EditingPermissions
 * @see ServerConfig
 */
public enum PermissionLevel {
    /**
     * Permits all players
     */
    ANYONE,

    /**
     * Permits all players considered viewers. Used for things such as sharing other players cached editing state
     * and other potentially exploitable information, although this is generally not a big deal
     */
    VIEWER,

    /**
     * Permits all players considered editors. Used for things such as performing editing operations that physically
     * change blocks or entities
     */
    EDITOR,

    /**
     * Permits all players that have operator permission on the server. Used for things such as changing server config
     */
    OPERATOR,

    /**
     * Permits no players
     */
    NO_ONE;

    /**
     * @return Whether the player has this permission level
     */
    public boolean hasLevel(ServerPlayerEntity player) {
        return switch (this) {
            case ANYONE -> PermissionRequirement.ANYONE.passes(player);
            case VIEWER -> ServerConfig.getViewerPermissionRequirement().passes(player);
            case EDITOR -> ServerConfig.getEditorPermissionRequirement().passes(player);
            case OPERATOR -> player.hasPermissionLevel(player.server.getOpPermissionLevel());
            case NO_ONE -> false;
        };
    }
}
