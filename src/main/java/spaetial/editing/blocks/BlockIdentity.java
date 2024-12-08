package spaetial.editing.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;

import java.util.function.Function;

/**
 * Represents how a certain block interacts with various aspects
 *
 * @param type                   The type of the block. Will try to find another block of the same type when changing
 *                               material or color.
 * @param material               The material of the block. Will try to find another block of she same material when
 *                               changing type or color.
 * @param color                  The color of the block. Will try to find another block of the same color when changing
 *                               type or material.
 * @param transformationGroup    How the blocks changes when transformed in certain ways.
 * @param connectionTypeFunction Which sides the block connects to.
 */
public record BlockIdentity(BlockType type, BlockMaterial material, BlockColor color, BlockTransformationGroup transformationGroup, Function<BlockState, BlockConnectionType> connectionTypeFunction) {
    public BlockState changeType(BlockState blockState, BlockType type) {
        // TODO
        return blockState;
    }

    public BlockState changeMaterial(BlockState blockState, BlockMaterial material) {
        // TODO
        return blockState;
    }

    public BlockState changeColor(BlockState blockState, BlockColor color) {
        // TODO
        return blockState;
    }

    public BlockConnectionType getConnectionType (BlockState blockState) {
        return connectionTypeFunction.apply(blockState);
    }
}
