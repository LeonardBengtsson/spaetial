package spaetial.editing.operation;

import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import spaetial.Spaetial;
import spaetial.editing.Filter;
import spaetial.editing.Material;
import spaetial.editing.region.MaskedBlockBox;
import spaetial.editing.selection.MaskedCuboidSelection;
import spaetial.editing.selection.Selection;
import spaetial.editing.region.Region;
import spaetial.server.editing.ServerManager;
import spaetial.util.BoxUtil;

public class CloneOperation extends UndoableOperation {
    private final RegistryKey<World> destinationDim;
    private final BlockBox destinationBox;
    private final boolean move;
    private final Filter sourceFilter;
    private final Filter destinationFilter;

    private final Selection sourceSelection;
    private Region sourceRegion = null;
    private MaskedBlockBox sourceRegionRemovalMask = null;
    private Region destinationRegion = null;

    private final boolean suppressUpdates;

    public CloneOperation(RegistryKey<World> sourceDim, RegistryKey<World> destDim, ServerPlayerEntity player, Selection selection, BlockPos delta, boolean move, Filter sourceFilter, Filter destinationFilter) {
        super(sourceDim, player);
        this.destinationDim = destDim;
        this.destinationBox = selection.getOuterBounds();
        this.move = move;
        this.sourceFilter = sourceFilter;
        this.destinationFilter = destinationFilter;

        this.sourceSelection = selection.copyAndOffset(delta.multiply(-1));
        this.suppressUpdates = ServerManager.getPlayerConfigOrDefault(playerId).suppressOperationUpdates();
    }

    @Override
    public OperationType getType() {
        return OperationType.CLONE;
    }

    @Override
    protected void executeInternal(ServerWorld world, boolean saveToHistory) {
        ServerWorld destinationWorld = world.getServer().getWorld(destinationDim);
        if (destinationWorld == null) {
            Spaetial.warn("Couldn't find dimension with name " + destinationDim.getValue() + " while performing operation " + this);
            return;
        }

        sourceRegion = Region.create(world, sourceSelection, sourceFilter);
        if (saveToHistory) {
            destinationRegion = Region.create(
                world,
                new MaskedCuboidSelection(MaskedBlockBox.create(destinationBox, sourceRegion.createMask())),
                destinationFilter
            );
        }
        if (move) {
            sourceRegionRemovalMask = MaskedBlockBox.create(sourceSelection.getOuterBounds(), sourceRegion.createMask());
            OperationUtil.fillVolume(world, sourceRegionRemovalMask, Filter.ALLOW_ALL, new Material(Blocks.AIR.getDefaultState()), suppressUpdates);
        }
        sourceRegion.place(destinationWorld, BoxUtil.minPos(destinationBox), suppressUpdates, destinationFilter);
    }

    @Override
    protected void undoInternal(ServerWorld world) {
        if (move) {
            sourceRegion.place(world, BoxUtil.minPos(sourceSelection.getOuterBounds()), true);
        }
        destinationRegion.place(world, BoxUtil.minPos(destinationBox), true);
    }

    @Override
    protected void redoInternal(ServerWorld world) {
        if (move) {
            OperationUtil.fillVolume(world, sourceRegionRemovalMask, Filter.ALLOW_ALL, new Material(Blocks.AIR.getDefaultState()), true);
        }
        sourceRegion.place(world, BoxUtil.minPos(destinationBox), true, destinationFilter);
    }

    @Override
    public int getVolume() {
        return BoxUtil.getVolume(destinationBox);
    }
}
