package spaetial.editing;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.world.RaycastContext;
import org.jetbrains.annotations.Nullable;
import spaetial.mixin.BlockItemInvoker;
import spaetial.mixin.BucketItemAccessor;
import spaetial.networking.PacketCodecsUtil;
import spaetial.util.math.RaycastUtil;

import java.util.*;
import java.util.stream.Stream;

public class Material {
    private static final Item AIR_ITEM = Items.STRUCTURE_VOID;

    private final BlockState[] states;
    private final int[] weights;
    private final int weightSum;
    private final Random random;
    private final long seed;

    public static final PacketCodec<RegistryByteBuf, Material> PACKET_CODEC = PacketCodec.tuple(
        PacketCodecsUtil.createList(PacketCodecsUtil.BLOCK_STATE), material -> List.of(material.states),
        PacketCodecsUtil.createList(PacketCodecsUtil.createBufMap(PacketCodecs.INTEGER)), material -> Arrays.stream(material.weights).boxed().toList(),
        PacketCodecs.VAR_LONG, material -> material.seed,
        (blockStates, weights, seed) -> new Material(blockStates.toArray(new BlockState[0]), weights.stream().mapToInt(i -> i).toArray(), seed)
    );

    public Material(BlockState[] states, int[] weights, long seed) {
        assert states.length == weights.length;
        assert states.length > 0;
        this.states = states;
        this.weights = weights;

        int sum = 0;
        for (int w : weights) {
            sum += w;
        }
        this.weightSum = sum;
        this.random = new Random(seed);
        this.seed = seed;
    }
    public Material(BlockState state) {
        this(new BlockState[]{state}, new int[]{1}, 0);
    }

    public void reset() {
        random.setSeed(seed);
    }

    public BlockState getNext() {
        if (states.length == 1) {
            return states[0];
        }
        int cumulativeWeightThreshold = random.nextInt(weightSum);
        for (int i = 0; i < states.length; i++) {
            cumulativeWeightThreshold -= weights[i];
            if (cumulativeWeightThreshold < 0) {
                return states[i];
            }
        }
        return states[0];
    }

    public void skip() {
        random.nextInt(weightSum);
    }

    private static @Nullable BlockState getStateFromItem(PlayerEntity player, Hand hand, ItemStack stack, BlockHitResult hitResult) {
        // TODO move method to util class
        var item = stack.getItem();
        if (item == AIR_ITEM) {
            return Blocks.AIR.getDefaultState();
        } else if (item instanceof BlockItem blockItem) {
            var context = new ItemPlacementContext(player, hand, stack, hitResult);
            var placementState = ((BlockItemInvoker) blockItem).getPlacementStateInvoker(context);
            return placementState == null ? blockItem.getBlock().getDefaultState() : placementState;
        } else if (item instanceof BucketItem bucketItem) {
            return ((BucketItemAccessor) bucketItem).getFluid().getDefaultState().getBlockState();
        }
        return null;
    }

    public static @Nullable Material fromMainhandItem(PlayerEntity player, boolean deterministicRandom) {
        var stack = player.getInventory().getMainHandStack();
        if (stack == null) return null;

        var hitResult = RaycastUtil.raycast(player, 100, false, RaycastContext.ShapeType.OUTLINE);
        if (hitResult.getType() != HitResult.Type.BLOCK) {
            var rotationVec = player.getRotationVec(0);
            Direction direction = Direction.getFacing(rotationVec.x, rotationVec.y, rotationVec.z);
            hitResult = new BlockHitResult(player.getPos(), direction, player.getBlockPos(), false);
        }

        var components = stack.getComponents();
        var item = stack.getItem();

        long seed = deterministicRandom ? stack.hashCode() : new Random().nextLong();

        var bundleContents = components.get(DataComponentTypes.BUNDLE_CONTENTS);
        var container = components.get(DataComponentTypes.CONTAINER);
        if (bundleContents != null || container != null) {
            Stream<ItemStack> stream = Stream.of();
            if (bundleContents != null) stream = bundleContents.stream();
            if (container != null) stream = Stream.concat(stream, container.stream());

            var items = new HashMap<Item, Integer>();
            stream.forEach(stackInContainer -> {
                var itemInContainer = stackInContainer.getItem();
                if (itemInContainer instanceof BlockItem || itemInContainer instanceof BucketItem) {
                    items.merge(itemInContainer, stackInContainer.getCount(), Integer::sum);
                }
            });

            if (items.size() > 0) {
                var states = new BlockState[items.size()];
                var weights = new int[items.size()];

                var entries = items.entrySet();
                int index = 0;
                for (var entry : entries) {
                    states[index] = getStateFromItem(player, Hand.MAIN_HAND, new ItemStack(entry.getKey()), hitResult);
                    weights[index] = entry.getValue();
                    index++;
                }
                return new Material(states, weights, seed);
            }
        }
        if (item instanceof BlockItem || item instanceof BucketItem) {
            return new Material(new BlockState[]{ getStateFromItem(player, Hand.MAIN_HAND, stack, hitResult) }, new int[] { 1 }, seed);
        } else if (item == Items.AIR) {
            return new Material(new BlockState[]{ Blocks.AIR.getDefaultState() }, new int[] { 1 }, seed);
        }
        return null;
    }

    public String getDebugText() {
        return Arrays.toString(states) + " " + Arrays.toString(weights) + " seed: " + seed;
    }
}
