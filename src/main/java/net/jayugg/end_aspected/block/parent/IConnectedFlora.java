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

    default boolean hasDistance() {
        return false;
    }

    default BlockState updateDistance(BlockState state, IWorld worldIn, BlockPos pos) {
        if (!(state.getBlock() instanceof IConnectedFlora)) {
            return state;
        }
        if (!((IConnectedFlora) state.getBlock()).hasDistance()) {
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
        if (!(state.getBlock() instanceof IConnectedFlora)) {
            return false;
        }
        if (!((IConnectedFlora) state.getBlock()).hasDistance()) {
            return true;
        }
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

    default int getDistance(BlockState neighbor) {
        if (!(neighbor.getBlock() instanceof IConnectedFlora)) {
            return MAX_DISTANCE;
        } else if (!((IConnectedFlora) neighbor.getBlock()).hasDistance()) {
            return 0;
        } else {
            return neighbor.get(DISTANCE);
        }
    }

}
