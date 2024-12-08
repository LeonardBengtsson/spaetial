package spaetial.editing.operation;

import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public abstract class UndoableOperation extends Operation {

    protected UndoableOperation(RegistryKey<World> dimension, @NotNull ServerPlayerEntity player) {
        super(dimension, player);
    }

    @Override
    public boolean canBeSavedToHistory() { return true; }

    public final void undo(ServerWorld world) {
        assert isCompleted();
        undoInternal(world);
    }

    public final void redo(ServerWorld world) {
        assert isCompleted();
        redoInternal(world);
    }

    public final void execute(ServerWorld world, boolean saveToHistory) {
        assert !isCompleted();
        executeInternal(world, saveToHistory);
        complete();
    }

    @Override
    protected final void executeInternal(ServerWorld world) {
        executeInternal(world, true);
    }
    
    protected abstract void executeInternal(ServerWorld world, boolean saveToHistory);
    protected abstract void undoInternal(ServerWorld world);
    protected abstract void redoInternal(ServerWorld world);
}
