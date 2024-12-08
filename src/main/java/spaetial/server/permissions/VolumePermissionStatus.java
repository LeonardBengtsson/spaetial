package spaetial.server.permissions;

public record VolumePermissionStatus(Status status, int volume, int maxVolume) {
    public enum Status {
        /**
         * The volume is below the soft limit
         */
        OK,
        /**
         * The volume is above the soft limit but below the hard limit
         */
        NEEDS_CONFIRM,
        /**
         * The operation is above the hard limit and thus denied
         */
        DENIED,
        /**
         * The operation is above the hard limit, but can be forced since the player is an operator
         */
        OPERATOR_CONFIRM;
    }
}
