package net.jayugg.end_aspected.util;

import net.jayugg.end_aspected.block.ModBlocks;
import net.jayugg.end_aspected.block.parent.MultiFaceBlock;
import net.jayugg.end_aspected.block.tree.VoidVeinBlock;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

import javax.annotation.Nullable;

public interface IVoidVeinPlacer {

    default void placeVeinAtPosition(IWorld world, BlockPos groundPos) {
        BlockState state = getStateForPlacement(world, groundPos);
        if (state != null) {
            world.setBlockState(groundPos, state, 3); // Flags=3 for client update
        }
    }

    @Nullable
    default BlockState getStateForPlacement(IWorld world, BlockPos blockPos) {
        VoidVeinBlock voidVeinBlock = (VoidVeinBlock) ModBlocks.VOID_VEIN.get();
        BlockState blockState = world.getBlockState(blockPos);
        FluidState fluidState = world.getFluidState(blockPos);
        // Proceed only if the block in the placement position is a vein already, or if it's replaceable
        boolean flag = blockState.matchesBlock(voidVeinBlock);
        if (!flag && !blockState.isReplaceable(fluidState.getFluid())) {
            return null;
        }
        // If it's a vein, keep its current state, otherwise use the default state
        BlockState newState = flag ? blockState : voidVeinBlock.getDefaultState();

        // Try to place a vein in the first available direction
        for (Direction direction : Direction.values()) {
            BooleanProperty booleanproperty = MultiFaceBlock.getPropertyFor(direction);
            // Check if there's a vine already attached to the block in the given direction
            boolean flag1 = flag && blockState.get(booleanproperty);
            if (!flag1 && voidVeinBlock.hasAttachment(world, blockPos, direction)) {
                // If there isn't and the vine can attach to the block, add the direction to the blockstate
                newState = newState.with(booleanproperty, Boolean.TRUE);
                // If the block is waterlogged, add the waterlogged property to the blockstate
                return waterlogIfFluid(world, blockPos, newState);
            }
        }

        // If the block is waterlogged, add the waterlogged property to the blockstate
        return waterlogIfFluid(world, blockPos, newState);
    }

    default BlockState waterlogIfFluid(IWorld world, BlockPos blockPos, BlockState newState) {
        BlockState oldState = world.getBlockState(blockPos);
        boolean flag = newState.hasProperty(BlockStateProperties.WATERLOGGED);
        return flag ? newState.with(BlockStateProperties.WATERLOGGED, oldState.getFluidState().getFluid().equals(Fluids.WATER)) : oldState;
    }

}
