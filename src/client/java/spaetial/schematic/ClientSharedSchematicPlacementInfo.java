package spaetial.schematic;

import org.jetbrains.annotations.Nullable;
import spaetial.editing.region.Region;

/**
 * Stores information about another player's shared schematic placement.
 *{@link ClientSharedSchematicPlacementInfo#region} is nullable and is typically null when the client has not decided to
 * participate in the shared schematic placement,or has not received the region from the server yet.
 *
 * @see SharedSchematicPlacementInfo
 * @see SharedSchematicPlacement
 */
public class ClientSharedSchematicPlacementInfo {
    public final SharedSchematicPlacementInfo info;
    private boolean participating = false;
    private @Nullable Region region = null;

    public ClientSharedSchematicPlacementInfo(SharedSchematicPlacementInfo info) {
        this.info = info;
    }

    public boolean getParticipating() { return participating; }
    public void setParticipating(boolean participating) { this.participating = participating; }

    public @Nullable Region getRegion() { return region; }
    public void setRegion(@Nullable Region region) { this.region = region; }
}
