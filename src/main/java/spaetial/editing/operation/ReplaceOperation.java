package spaetial.editing.operation;

import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import spaetial.editing.Filter;
import spaetial.editing.Material;
import spaetial.editing.region.Region;
import spaetial.editing.selection.Selection;
import spaetial.server.editing.ServerManager;
import spaetial.util.BoxUtil;

public class ReplaceOperation extends UndoableOperation {
    private final Selection selection;
    private final Filter filter;
    private final Material material;

    private Region replacedRegion = null;

    private final boolean suppressUpdates;

    public ReplaceOperation(RegistryKey<World> dimension, @NotNull ServerPlayerEntity player, Selection selection, Filter filter, Material material) {
        super(dimension, player);
        this.selection = selection;
        this.filter = filter;
        this.material = material;
        this.suppressUpdates = ServerManager.getPlayerConfigOrDefault(playerId).suppressOperationUpdates();
    }

    @Override
    public OperationType getType() {
        return OperationType.REPLACE;
    }

    @Override
    protected void executeInternal(ServerWorld world, boolean saveToHistory) {
        if (saveToHistory) {
            replacedRegion = Region.create(world, selection, filter);
        }
        material.reset();
        OperationUtil.fillVolume(world, selection.getMaskedBlockBox(), filter, material, suppressUpdates);
    }

    @Override
    protected void undoInternal(ServerWorld world) {
        replacedRegion.place(world, BoxUtil.minPos(selection.getOuterBounds()), true);
    }

    @Override
    protected void redoInternal(ServerWorld world) {
        material.reset();
        OperationUtil.fillVolume(world, selection.getMaskedBlockBox(), filter, material, true);
    }

    @Override
    public int getVolume() {
        return selection.getVolume();
    }
}
