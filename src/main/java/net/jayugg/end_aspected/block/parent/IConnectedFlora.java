package net.jayugg.end_aspected.block.parent;

import net.jayugg.end_aspected.util.ModTags;
import net.minecraft.block.BlockState;
import net.minecraft.state.IntegerProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public interface IConnectedFlora {
    int MAX_DISTANCE = 15;
    IntegerProperty DISTANCE = IntegerProperty.create("distance", 0, MAX_DISTANCE);

    default BlockState updateDistance(BlockState state, IWorld worldIn, BlockPos pos) {
        if (!(state.getBlock() instanceof IConnectedFlora)) {
            return state;
        }
        int i = MAX_DISTANCE;
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

        for(Direction direction : Direction.values()) {
            blockpos$mutable.setAndMove(pos, direction);
            BlockState neighbor = worldIn.getBlockState(blockpos$mutable);
            i = Math.min(i, getDistance(neighbor) + 1);
            if (i == 1) {
                break;
            }
        }

        return state.with(DISTANCE, Integer.valueOf(i));
    }

    default boolean isConnected(BlockState state, IWorld worldIn, BlockPos pos) {
        boolean connected = false;
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

        for(Direction direction : Direction.values()) {
            blockpos$mutable.setAndMove(pos, direction);
            BlockState neighbor = worldIn.getBlockState(blockpos$mutable);
            connected = getDistance(neighbor) < state.get(DISTANCE);
            if (connected) {
                break;
            }
        }

        return connected;
    }

    /*
    Need to find a way to structure this better, as blocks in ModTags.PERSISTENT_VOID_FLORA_BLOCKS
    should not have the DISTANCE property, but they implement IVeinConnectedElement which extends IConnectedFlora,
    so if they somehow skip the first check, the code will try to access their DISTANCE and crash. This shouldn't
    happen if the tags are set up correctly, but it's not the safest way to go.
     */
    default int getDistance(BlockState neighbor) {
        if (ModTags.PERSISTENT_VOID_FLORA_BLOCKS.contains(neighbor.getBlock())) {
            return 0;
        } else {
            return neighbor.getBlock() instanceof IConnectedFlora ? neighbor.get(DISTANCE) : MAX_DISTANCE;
        }
    }
}
