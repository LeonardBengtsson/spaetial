package spaetial.server.editing;

import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;
import spaetial.Spaetial;
import spaetial.editing.OperationAction;
import spaetial.editing.operation.Operation;
import spaetial.editing.operation.UndoableOperation;

import java.util.LinkedList;
import java.util.Queue;

public class OperationQueue {
    // TODO change this value
    // operations with a volume less than this don't need to be added to the queue and can be completed instantly
    private static final int EXECUTE_INSTANTLY = 512;

    private final Queue<Entry> queue = new LinkedList<>();
    private @Nullable Thread workerThread = null;

    public void add(MinecraftServer server, Entry entry) {
        if (
            queue.size() == 0
            && entry.operation.getVolume() <= EXECUTE_INSTANTLY
            && (workerThread == null || !workerThread.isAlive())
        ) {
            perform(server, entry);
        } else {
            queue.add(entry);
            if (queue.size() == 1) onTick(server);
        }
    }

    private static void perform(MinecraftServer server, Entry entry) {
        var operation = entry.operation;
        var world = server.getWorld(operation.dimension);
        if (world == null) {
            // TODO
            // some operations could want to allow null world, maybe add boolean Operation::requiresWorld
            Spaetial.warn("Couldn't find dimension with name " + operation.dimension.getValue() + " while performing operation " + operation);
        } else {
            switch (entry.action) {
                case EXECUTE -> operation.execute(world);
                case EXECUTE_WITHOUT_HISTORY -> {
                    if (operation instanceof UndoableOperation undoableOperation) {
                        undoableOperation.execute(world, false);
                    } else {
                        operation.execute(world);
                    }
                }
                case UNDO -> {
                    if (operation instanceof UndoableOperation undoableOperation) {
                        undoableOperation.undo(world);
                    } else {
                        throw new IllegalStateException("Tried to undo operation " + operation.toString());
                    }
                }
                case REDO -> {
                    if (operation instanceof UndoableOperation undoableOperation) {
                        undoableOperation.redo(world);
                    } else {
                        throw new IllegalStateException("Tried to redo operation " + operation.toString());
                    }
                }
            }
        }
    }

    public void onTick(MinecraftServer server) {
        if (!queue.isEmpty() && (workerThread == null || !workerThread.isAlive())) {
            var entry = queue.remove();
            workerThread = new Thread(() -> perform(server, entry));
            workerThread.start();
        }
    }

    public boolean isProcessing() {
        return queue.size() != 0 || (workerThread != null && workerThread.isAlive());
    }

    public void sync(MinecraftServer server) {
        if (workerThread != null) {
            try {
                workerThread.join();
            }
            catch (Throwable ignored) {}
        }
        for (var entry : queue) {
            perform(server, entry);
        }
        queue.clear();
    }

    record Entry(Operation operation, OperationAction action) {}
}
