package spaetial.editing.operation;

import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import spaetial.editing.Filter;
import spaetial.editing.region.MaskedBlockBox;
import spaetial.editing.region.Region;
import spaetial.editing.selection.MaskedCuboidSelection;
import spaetial.server.editing.ServerManager;
import spaetial.util.BoxUtil;

import java.util.UUID;

public class CompleteSchematicOperation extends UndoableOperation {
    private final MaskedBlockBox destinationBox;
    private final Region filteredSchematicRegion;
    private Region replacedRegion = null;
    private final boolean suppressUpdates;

    public CompleteSchematicOperation(RegistryKey<World> dimension, ServerPlayerEntity player, MinecraftServer server, UUID placementId, Region schematicRegion, BlockPos minPos, Filter filter) {
        super(dimension, player);
        this.filteredSchematicRegion = schematicRegion.cloneWithFilter(filter);
        this.destinationBox = MaskedBlockBox.create(BoxUtil.fromMinAndDimensions(minPos, schematicRegion.dimensions()), filteredSchematicRegion.createMask());
        this.suppressUpdates = ServerManager.getPlayerConfigOrDefault(playerId).suppressOperationUpdates();

        ServerManager.removeSchematicPlacement(server, player, placementId);
    }

    @Override
    public OperationType getType() {
        return OperationType.COMPLETE_SCHEMATIC;
    }

    @Override
    protected void executeInternal(ServerWorld world, boolean saveToHistory) {
        if (saveToHistory) {
            replacedRegion = Region.create(world, new MaskedCuboidSelection(destinationBox), Filter.ALLOW_ALL);
        }
        filteredSchematicRegion.place(world, BoxUtil.minPos(destinationBox.getBox()), suppressUpdates, Filter.ALLOW_ALL);
    }

    @Override
    protected void undoInternal(ServerWorld world) {
        replacedRegion.place(world, BoxUtil.minPos(destinationBox.getBox()), true);
    }

    @Override
    protected void redoInternal(ServerWorld world) {
        filteredSchematicRegion.place(world, BoxUtil.minPos(destinationBox.getBox()), true, Filter.ALLOW_ALL);
    }

    @Override
    public int getVolume() {
        return destinationBox.getVolume();
    }
}
