package net.jayugg.end_aspected.block.parent;

import net.jayugg.end_aspected.util.ModTags;
import net.minecraft.block.BlockState;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public interface IConnectedFlora {
    int MAX_DISTANCE = 20;
    BooleanProperty CONNECTED = BooleanProperty.create("connected");
    IntegerProperty DISTANCE = IntegerProperty.create("distance", 0, MAX_DISTANCE);

    default BlockState updateDistance(BlockState state, IWorld worldIn, BlockPos pos) {
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

    default BlockState updateConnection(BlockState state, IWorld worldIn, BlockPos pos) {
        BlockState newState = updateDistance(state, worldIn, pos);
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

        return connected ? newState.with(CONNECTED, true) : newState.with(CONNECTED, false).with(DISTANCE, MAX_DISTANCE);
    }

    default int getDistance(BlockState neighbor) {
        if (ModTags.PERSISTENT_VOID_FLORA_BLOCKS.contains(neighbor.getBlock())) {
            return 0;
        } else {
            return neighbor.getBlock() instanceof IConnectedFlora ? neighbor.get(DISTANCE) : MAX_DISTANCE;
        }
    }
}
