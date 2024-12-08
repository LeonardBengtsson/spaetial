package spaetial.editing.operation;

import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class Operation {
    public final RegistryKey<World> dimension;
    protected UUID playerId;

    private boolean completed = false;
    public final boolean isCompleted() { return completed; }
    protected final void complete() { completed = true; }

    protected Operation(RegistryKey<World> dimension, @NotNull ServerPlayerEntity player) {
        this.dimension = dimension;
        this.playerId = player.getUuid();
    }

    /**
     * Determines whether the operation has any meaningful undo action. The default behaviour is that an
     * {@code UndoableOperation} is always considered to have a meaningful undo action, but in some cases this might not
     * be the case, such as when the operation only copies a region to the clipboard without changing the world.
     *
     * @return {@code true} if the operation should be able to be saved to history
     *
     * @see CopyOperation
     */
    public boolean canBeSavedToHistory() { return false; }

    public abstract OperationType getType();
    protected abstract void executeInternal(ServerWorld world);

    public final void execute(ServerWorld world) {
        assert !isCompleted();
        executeInternal(world);
        complete();
    }

    /**
     * @return A value greater than or equal to the amount of blocks changed, roughly indicating the complexity of the operation
     */
    public abstract int getVolume();
}
