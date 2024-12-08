package spaetial;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import spaetial.editing.Filter;
import spaetial.editing.selection.FuzzyConnectionBehaviour;
import spaetial.editing.selection.FuzzyMode;
import spaetial.networking.c2s.ConfigUpdateC2SPacket;
import spaetial.server.networking.ServerSideClientConfig;
import spaetial.util.color.Color4f;
import spaetial.util.mixin.NoClipUtil;

import java.awt.*;

public final class ClientConfig {
    private ClientConfig() {}

    public static void sendUpdatePacket() {
        ClientPlayNetworking.send(new ConfigUpdateC2SPacket(new ServerSideClientConfig(
            Persistent.shouldParticipateInMultiplayerCache,
            Local.suppressOperationUpdates,
            Local.suppressPlayerUpdates,
            Local.forcedBlockPlacement,
            Local.noClip,
            Local.farReach,
            Persistent.maxRaycastRange,
            Local.flySpeed
        )));
    }

    public static boolean getTargetSurface(boolean targetSurface) {
        return targetSurface ^ Persistent.invertTargetSurface;
    }

    public static boolean getIgnoreAir(boolean ignoreAir) {
        return ignoreAir ^ Persistent.invertIgnoreAir;
    }

    /**
     * Contains client-specific config that's persistent across worlds
     */
    public static final class Persistent {
        private Persistent() {}

        // INPUT
        private static boolean quickSetReleaseCancel = false;
        private static boolean moveInPlaneReleaseCancel = false;
        public static boolean getQuickSetReleaseCancel() { return quickSetReleaseCancel; }
        public static boolean getMoveInPlaneReleaseCancel() { return moveInPlaneReleaseCancel; }

        // APPEARANCE
        private static Color accentColor = new Color(0x00aaff);
        private static Color activeSchematicColor = new Color(0x8800ff);
        private static Color inactiveSchematicColor = new Color(0x310063);
        private static Color ownSelectionColor = new Color(0xffffff);
        private static Color othersSelectionColor = new Color(0x777777);
        private static Color ownAddBlocksColor = new Color(0x00ff77);
        private static Color othersAddBlocksColor = new Color(0x00632a);
        private static Color ownReplaceBlocksColor = new Color(0xffee00);
        private static Color othersReplaceBlocksColor = new Color(0x807000);
        private static Color ownRemoveBlocksColor = new Color(0xff0077);
        private static Color othersRemoveBlocksColor = new Color(0x800038);

        public static Color getAccentColor() { return accentColor; }
        public static Color getSchematicColor(boolean active) { return active ? activeSchematicColor : inactiveSchematicColor; }
        public static Color getSelectionColor(boolean isOther) { return isOther ? othersSelectionColor : ownSelectionColor; }
        public static Color getAddBlocksColor(boolean isOther) { return isOther ? othersAddBlocksColor : ownAddBlocksColor; }
        public static Color getReplaceBlocksColor(boolean isOther) { return isOther ? othersReplaceBlocksColor : ownReplaceBlocksColor; }
        public static Color getRemoveBlocksColor(boolean isOther) { return isOther ? othersRemoveBlocksColor : ownRemoveBlocksColor; }

        @Deprecated
        private static Color primaryColor = new Color(0x00aaff);
        @Deprecated
        private static Color secondaryColor = new Color(0x007ab8);
        @Deprecated
        private static Color tertiaryColor = new Color(0x4466);
        @Deprecated
        private static Color sourcePositiveColor = new Color(0xffee00);
        @Deprecated
        private static Color sourceNegativeColor = new Color(0xaa4422);

        @Deprecated
        private static Color othersPrimaryColor = new Color(127, 127, 127);
        @Deprecated
        private static Color othersSecondaryColor = new Color(80, 80, 80);
        @Deprecated
        private static Color othersTertiaryColor = new Color(40, 40, 40);
        @Deprecated
        private static Color othersSourcePositiveColor = new Color(0, 20, 40);
        @Deprecated
        private static Color othersSourceNegativeColor = new Color(40, 0, 10);

        @Deprecated
        private static Color primarySchematicBoxColor = new Color(0xff00aa);
        @Deprecated
        private static Color secondarySchematicBoxColor = new Color(0xb80056);
        @Deprecated
        private static Color othersSchematicBoxColor = new Color(0xaa7788);

        @Deprecated
        public static Color getPrimaryColor() {
            return primaryColor;
        }

        @Deprecated
        public static Color4f getPrimaryLineColor(boolean isOtherPlayer) {
            return Color4f.create(isOtherPlayer ? othersPrimaryColor : primaryColor);
        }

        @Deprecated
        public static Color4f getSecondaryLineColor(boolean isOtherPlayer) {
            return Color4f.create(isOtherPlayer ? othersSecondaryColor : secondaryColor);
        }

        @Deprecated
        public static Color4f getTertiaryLineColor(boolean isOtherPlayer) {
            return Color4f.create(isOtherPlayer ? othersTertiaryColor : tertiaryColor);
        }

        @Deprecated
        public static Color4f getSourceColor(boolean isNegative, boolean isOtherPlayer) {
            return Color4f.create(
                isNegative
                    ? (isOtherPlayer ? othersSourceNegativeColor : sourceNegativeColor)
                    : (isOtherPlayer ? othersSourcePositiveColor : sourcePositiveColor)
            );
        }

        @Deprecated
        public static Color4f getSchematicBoxColor(boolean active) {
            return Color4f.create(active ? primarySchematicBoxColor : secondarySchematicBoxColor);
        }

        @Deprecated
        public static Color4f getOthersSchematicBoxColor(boolean joined) {
            return Color4f.create(joined ? secondarySchematicBoxColor : othersSchematicBoxColor);
        }

        // EDITING / PERFORMANCE
        private static double maxRaycastRange = 200;
        private static int lineRenderLimit = 10000;
        private static int blockRenderLimit = 25000;
        private static long inactiveTimeMillis = 1000 * 60;
        private static boolean invertTargetSurface = false;
        private static boolean invertIgnoreAir = false;

        /**
         * @return At what range the client should stop looking for blocks to target to instead default to the player's current position
         */
        public static double getMaxRaycastRange() {
            return maxRaycastRange;
        }
        /**
         * @return The rough limit for how many lines can be rendered in one run
         */
        public static int getLineRenderLimit() { return lineRenderLimit; }
        /**
         * @return The rough limit for how many blocks can be rendered in one run
         */
        public static int getBlockRenderLimit() { return blockRenderLimit; }
        /**
         * @return How long, in milliseconds, it takes for the client to become inactive after the previous change to the editing state. A value of {@code -1} indicates that the client should never become inactive.
         */
        public static long getInactiveTimeMillis() { return inactiveTimeMillis; }
        public static boolean getInvertTargetSurface() { return invertTargetSurface; }
        public static boolean getInvertIgnoreAir() { return invertIgnoreAir; }

        // INPUT
        @Deprecated
        private static boolean ctrlHotkeys = false;

        /**
         * This setting toggles certain keybindings between needing to press Ctrl to activate them, or not. For example,
         * while this method returns true, the keybinding for undoing would be Ctrl Z, while it would be only Z otherwise.
         *
         * @return False if ctrl needs to be pressed and isn't, otherwise true
         */
        @Deprecated
        public static boolean ctrlNeeded(boolean ctrl) { return ctrl || !ctrlHotkeys; }

        // NETWORKING
        private static boolean shouldRequestRegions = true;
        private static boolean shouldParticipateInMultiplayerCache = true;
        private static long regionRequestExpirationMillis = 1000 * 60;
        private static int maxPacketSize = 0x100000; // TODO change

        /**
         * @return Whether the client should request regions from the server for rendering the current editing state etc.
         */
        public static boolean shouldRequestRegions() {
            return shouldRequestRegions;
        }

        /**
         * @return Whether the client should send information about its own editing state to other players, and request their state
         */
        public static boolean shouldParticipateInMultiplayerCache() {
            return shouldParticipateInMultiplayerCache;
        }
        public static long getRegionRequestExpirationMillis() { return regionRequestExpirationMillis; }
        public static int getMaxPacketSize() { return maxPacketSize; }

    }

    /**
     * Contains client-side config that's specific to one world
     */
    public static final class Local {
        private Local() {}

        // SELECTION
        private static boolean targetFluids = false;
        private static FuzzyMode fuzzyMode = FuzzyMode.NON_AIR;
        private static FuzzyConnectionBehaviour fuzzyConnectionBehaviour = FuzzyConnectionBehaviour.FACES_CONNECTED;

        public static boolean getTargetFluids() { return targetFluids; }
        public static FuzzyMode getFuzzyMode() {
            return fuzzyMode;
        }
        public static FuzzyConnectionBehaviour getFuzzyConnectionBehaviour() {
            return fuzzyConnectionBehaviour;
        }

        // EDITING
        private static boolean relativeEditing = true;
        private static boolean suppressOperationUpdates = true;
        private static Filter sourceFilter = Filter.ALLOW_ALL;
        private static Filter destinationFilter = Filter.ALLOW_ALL;

        public static boolean getRelativeEditing() { return relativeEditing; }
        public static boolean getSuppressOperationUpdates() { return suppressOperationUpdates; }
        public static void DEBUG_toggleSuppressPlayerUpdates(boolean toggle) {
            suppressPlayerUpdates = toggle; // TODO REMOVE DEBUG METHOD
        }
        public static Filter getSourceFilter() { return sourceFilter; }
        public static Filter getDestinationFilter() { return destinationFilter; }

        // PLAYER
        private static boolean suppressPlayerUpdates = false;
        private static boolean forcedBlockPlacement = false;
        private static boolean noClip = false;
        private static boolean fullBright = false;
        private static boolean farReach = false;
        private static double flySpeed = 1.0;

        public static boolean getSuppressPlayerUpdates() { return suppressPlayerUpdates; }
        public static boolean getForcedBlockPlacement() { return forcedBlockPlacement; }
        /**
         * @return Whether the player should be able to move through blocks
         */
        public static boolean getNoClip() {
            return noClip;
        }
        public static void setNoClip(boolean toggle) {
            noClip = toggle;
            NoClipUtil.isClientAndNoClipOn = toggle;
        }
        public static boolean getFullBright() { return fullBright; }
        public static double getFlySpeed() { return flySpeed; }
    }
}
