package spaetial.editing.operation;

import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import spaetial.editing.Filter;
import spaetial.editing.Material;
import spaetial.editing.region.MaskedBlockBox;
import spaetial.editing.selection.Selection;
import spaetial.editing.region.Region;
import spaetial.server.editing.ServerManager;
import spaetial.util.BoxUtil;

import java.util.UUID;

public class CopyOperation extends UndoableOperation {
    private final Selection selection;
    private final boolean cut;
    private final Filter filter;
    private final @Nullable UUID regionRequestId;

    private Region region = null;

    private MaskedBlockBox removalMask = null;

    private final boolean suppressUpdates;

    public CopyOperation(RegistryKey<World> dimension, ServerPlayerEntity player, Selection selection, boolean cut, Filter filter, @Nullable UUID regionRequestId) {
        super(dimension, player);
        this.selection = selection;
        this.cut = cut;
        this.filter = filter;
        this.regionRequestId = regionRequestId;
        this.suppressUpdates = ServerManager.getPlayerConfigOrDefault(playerId).suppressOperationUpdates();
    }

    @Override
    public OperationType getType() {
        return OperationType.COPY;
    }

    @Override
    public boolean canBeSavedToHistory() { return cut; }

    @Override
    protected void executeInternal(ServerWorld world, boolean saveToHistory) {
        region = Region.create(world, selection, filter);
        ServerManager.setClipboard(world.getServer(), playerId, region, regionRequestId);
        if (cut) {
            removalMask = MaskedBlockBox.create(selection.getOuterBounds(), region.createMask());
            OperationUtil.fillVolume(world, removalMask, Filter.ALLOW_ALL, new Material(Blocks.AIR.getDefaultState()), suppressUpdates);
        }
    }

    @Override
    protected void undoInternal(ServerWorld world) {
        if (cut) {
            region.place(world, BoxUtil.minPos(selection.getOuterBounds()), true);
        }
    }

    @Override
    protected void redoInternal(ServerWorld world) {
        if (cut) {
            OperationUtil.fillVolume(world, removalMask, Filter.ALLOW_ALL, new Material(Blocks.AIR.getDefaultState()), true);
        }
    }

    @Override
    public int getVolume() {
        return selection.getVolume();
    }
}
