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
import spaetial.util.StackUtil;
import spaetial.util.BoxUtil;

public class LineStackOperation extends UndoableOperation {
    private final RegistryKey<World> destinationDim;
    private final BlockBox destinationOriginBox;
    private final boolean move;
    private final int stackSize;
    private final BlockPos spacing;
    private final Filter sourceFilter;
    private final Filter destinationFilter;

    private final Selection sourceSelection;

    private Region sourceRegion = null;
    private MaskedBlockBox sourceRegionRemovalMask = null;
    private Region[] replacedRegions = null;

    private final boolean suppressUpdates;

    public LineStackOperation(RegistryKey<World> sourceDim, RegistryKey<World> destDim, ServerPlayerEntity player, Selection selection, BlockPos delta, boolean move, int stackSize, BlockPos spacing, Filter sourceFilter, Filter destinationFilter) {
        super(sourceDim, player);
        assert spacing.getX() >= 0 && spacing.getY() >= 0 && spacing.getZ() >= 0;

        this.destinationDim = destDim;
        this.destinationOriginBox = selection.getOuterBounds();
        this.move = move && !(delta.getX() == 0 && delta.getY() == 0 && delta.getZ() == 0);
        this.stackSize = stackSize;
        this.spacing = spacing;

        this.sourceFilter = sourceFilter;
        this.destinationFilter = destinationFilter;

        this.sourceSelection = selection.copyAndOffset(delta.multiply(-1));

        this.suppressUpdates = ServerManager.getPlayerConfigOrDefault(playerId).suppressOperationUpdates();
    }

    @Override
    public OperationType getType() {
        return OperationType.LINE_STACK;
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
            replacedRegions = new Region[stackSize];
        }
        if (move) {
            sourceRegionRemovalMask = MaskedBlockBox.create(sourceSelection.getOuterBounds(), sourceRegion.createMask());
            OperationUtil.fillVolume(world, sourceRegionRemovalMask, Filter.ALLOW_ALL, new Material(Blocks.AIR.getDefaultState()), suppressUpdates);
        }
        var sourceRegionMask = sourceRegion.createMask();
        StackUtil.lineStackIterate(destinationOriginBox, stackSize, spacing, false, (index, box) -> {
            if (saveToHistory) {
                replacedRegions[index] = Region.create(
                    destinationWorld,
                    new MaskedCuboidSelection(MaskedBlockBox.create(box, sourceRegionMask)),
                    destinationFilter
                );
            }
            sourceRegion.place(destinationWorld, BoxUtil.minPos(box), suppressUpdates, destinationFilter);
        });
    }

    @Override
    protected void undoInternal(ServerWorld world) {
        if (move) {
            sourceRegion.place(world, BoxUtil.minPos(sourceSelection.getOuterBounds()), true);
        }
        StackUtil.lineStackIterate(destinationOriginBox, stackSize, spacing, false, (index, box) -> {
            replacedRegions[index].place(world, BoxUtil.minPos(box), true);
        });
    }

    @Override
    protected void redoInternal(ServerWorld world) {
        if (move) {
            OperationUtil.fillVolume(world, sourceRegionRemovalMask, Filter.ALLOW_ALL, new Material(Blocks.AIR.getDefaultState()), true);
        }
        StackUtil.lineStackIterate(destinationOriginBox, stackSize, spacing, false, (index, box) -> {
            sourceRegion.place(world, BoxUtil.minPos(box), true, destinationFilter);
        });
    }

    @Override
    public int getVolume() {
        return stackSize * BoxUtil.getVolume(destinationOriginBox);
    }
}
