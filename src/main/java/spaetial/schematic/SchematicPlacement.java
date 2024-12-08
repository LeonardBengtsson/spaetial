package spaetial.schematic;

import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import spaetial.util.BoxUtil;

public class SchematicPlacement {
    public final Schematic schematic;
    public final BlockPos minPos;
    public final RegistryKey<World> dim;
    public final BlockBox box;

    public SchematicPlacement(Schematic schematic, BlockPos minPos, RegistryKey<World> dim) {
        this.schematic = schematic;
        this.minPos = minPos;
        this.dim = dim;
        this.box = BoxUtil.fromMinAndDimensions(minPos, schematic.region().dimensions());
    }

    public SchematicPlacement move(BlockPos newMinPos, RegistryKey<World> newDimension) {
        return new SchematicPlacement(schematic, newMinPos, newDimension);
    }
}
