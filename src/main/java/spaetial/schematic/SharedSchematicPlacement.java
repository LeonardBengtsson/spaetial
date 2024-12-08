package spaetial.schematic;

import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.UUID;

public class SharedSchematicPlacement {
    public final SchematicPlacement schematicPlacement;

    public final UUID owner;
    public final HashSet<UUID> participants;

    public SharedSchematicPlacement(SchematicPlacement schematicPlacement, UUID owner) {
        this(schematicPlacement, owner, new HashSet<>());
        this.participants.add(owner);
    }

    private SharedSchematicPlacement(SchematicPlacement schematicPlacement, UUID owner, HashSet<UUID> participants) {
        this.schematicPlacement = schematicPlacement;
        this.owner = owner;
        this.participants = participants;
    }

    public SharedSchematicPlacement move(BlockPos newMinPos, RegistryKey<World> newDimension) {
        return new SharedSchematicPlacement(schematicPlacement.move(newMinPos, newDimension), owner, participants);
    }
}
