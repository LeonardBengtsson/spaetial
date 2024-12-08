package spaetial.server.editing;

import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;
import spaetial.editing.OperationAction;
import spaetial.editing.operation.Operation;
import spaetial.schematic.SchematicPlacement;
import spaetial.editing.operation.UndoableOperation;
import spaetial.editing.region.Region;
import spaetial.server.ServerConfig;

import java.util.*;

class PlayerData {
    private final OperationQueue operationQueue = new OperationQueue();
    private OperationQueue.Entry onHoldAction = null;
    private final List<UndoableOperation> history = new ArrayList<>();
    private int historyPosition = 0;
    @Deprecated
    private int historyEpoch = 0;

    private @Nullable Region clipboard = null;
    @Deprecated
    private @Nullable UUID clipboardId = null;
    @Deprecated
    private static final HashMap<UUID, HistoryRegionContainer> historicalClipboards = new HashMap<>();

    private final HashMap<UUID, SchematicPlacement> schematicPlacements = new HashMap<>();

    protected PlayerData() {}

    public void onTick(MinecraftServer server) {
        operationQueue.onTick(server);
    }

    public void onServerStopping(MinecraftServer server) {
        operationQueue.sync(server);
    }

    public void putOperationOnHold(Operation operation) {
        onHoldAction = new OperationQueue.Entry(operation, OperationAction.EXECUTE);
    }

    public void putUndoOnHold() {
        onHoldAction = new OperationQueue.Entry(null, OperationAction.UNDO);
    }

    public void putRedoOnHold() {
        onHoldAction = new OperationQueue.Entry(null, OperationAction.REDO);
    }

    public OperationQueue.Entry getOnHoldAction() {
        return onHoldAction;
    }

    public void confirmOnHoldAction(MinecraftServer server, boolean skipHistory) {
        if (onHoldAction != null) {
            switch (onHoldAction.action()) {
                case EXECUTE -> queueExecution(server, onHoldAction.operation(), skipHistory);
                case UNDO -> queueUndo(server);
                case REDO -> queueRedo(server);
            }
        }
    }

    public void queueExecution(MinecraftServer server, Operation operation, boolean skipHistory) {
        onHoldAction = null;
        boolean skipHistoryChecked = skipHistory || !operation.canBeSavedToHistory();
        operationQueue.add(server, new OperationQueue.Entry(
            operation,
            skipHistoryChecked ? OperationAction.EXECUTE_WITHOUT_HISTORY : OperationAction.EXECUTE
        ));
        if (!skipHistoryChecked && (operation instanceof UndoableOperation undoableOperation)) {
            if (historyPosition >= history.size()) {
                history.add(undoableOperation);
            } else {
                history.set(historyPosition, undoableOperation);
                history.subList(historyPosition + 1, history.size()).clear();
            }
            if (history.size() > ServerConfig.getMaxHistory()) {
                history.remove(0);
            } else {
                historyPosition++;
            }
            historyEpoch++;
        }
    }

    public void queueUndo(MinecraftServer server) {
        onHoldAction = null;
        if (historyPosition > 0) {
            historyPosition--;
            historyEpoch--;
            operationQueue.add(server, new OperationQueue.Entry(
                history.get(historyPosition),
                OperationAction.UNDO
            ));
        }
    }

    public void queueRedo(MinecraftServer server) {
        onHoldAction = null;
        if (historyPosition < history.size()) {
            operationQueue.add(server, new OperationQueue.Entry(
                history.get(historyPosition),
                OperationAction.REDO
            ));
            historyPosition++;
            historyEpoch++;
        }
    }

    public @Nullable UndoableOperation getNextUndo() {
        if (historyPosition == 0) return null;
        return history.get(historyPosition - 1);
    }

    public @Nullable UndoableOperation getNextRedo() {
        if (historyPosition >= history.size()) return null;
        return history.get(historyPosition);
    }

    public void setClipboard(@Nullable Region region) {
        clipboard = region;
    }

    public @Nullable Region getClipboard() {
        return clipboard;
    }

    @Deprecated
    @Nullable
    public UUID getClipboardId() {
        return clipboardId;
    }

    @Deprecated
    public void setClipboard_(Region region) {
        UUID id = UUID.randomUUID();
        historicalClipboards.put(id, new HistoryRegionContainer(historyEpoch, region));
        clipboardId = id;
    }

    @Deprecated
    @Nullable
    public Region retrieveHistoricalClipboard(UUID id) {
        HistoryRegionContainer container = historicalClipboards.get(id);
        if (container == null) return null;
        return container.region;
    }

    @Deprecated
    public void purgeExpiredHistoricalClipboards() {
        for (var entry : historicalClipboards.entrySet()) {
            if (entry.getKey() != clipboardId && entry.getValue().historyEpochExpiry < historyEpoch + ServerConfig.getMaxHistory() - 1) {
                historicalClipboards.remove(entry.getKey());
            }
        }
    }

    public void putSchematicPlacement(UUID placementId, SchematicPlacement placement) {
        schematicPlacements.put(placementId, placement);
    }

    public void removeSchematicPlacement(UUID placementId) {
        schematicPlacements.remove(placementId);
    }

    public @Nullable SchematicPlacement getSchematicPlacement(UUID placementId) {
        return schematicPlacements.get(placementId);
    }

    @Deprecated
    private record HistoryRegionContainer(long historyEpochExpiry, Region region) { }
}
