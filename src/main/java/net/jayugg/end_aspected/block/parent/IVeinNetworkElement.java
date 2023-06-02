package net.jayugg.end_aspected.block.parent;

import net.minecraft.block.BlockState;
import net.minecraft.state.IntegerProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

import java.util.HashSet;
import java.util.Set;

public interface IVeinNetworkElement extends IConnectedFlora {
    int MAX_POWER = 4;
    IntegerProperty POWER = IntegerProperty.create("void_flora_power", 0, MAX_POWER);
    // Add a ThreadLocal to store the updating state for each thread
    ThreadLocal<Set<BlockPos>> UPDATING_POSITIONS = ThreadLocal.withInitial(HashSet::new);

    default BlockState sharePowerToNeighbors(BlockState blockState, IWorld worldIn, BlockPos blockPos) {
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
        int power = blockState.get(POWER);

        if (power == 0 || UPDATING_POSITIONS.get().contains(blockPos)) {
            return blockState;
        }
        UPDATING_POSITIONS.get().add(blockPos);
        try {
            for (Direction direction : Direction.values()) {
                blockpos$mutable.setAndMove(blockPos, direction);
                BlockState neighbor = worldIn.getBlockState(blockpos$mutable);

                if (neighbor.getBlock() instanceof IVeinNetworkElement) {
                    int neighborPower = neighbor.get(POWER);
                    int neighborDistance = getDistance(neighbor);
                    int currentDistance = getDistance(blockState);

                    // Share power if the neighbor is at the same distance and has lower power or if the neighbor is closer to a node
                    if ((neighborDistance == currentDistance && neighborPower < power) || neighborDistance < currentDistance) {
                        blockState = sharePower(worldIn, blockState, blockpos$mutable, neighbor, power, neighborPower);
                    }
                }
            }
        } finally {
            // Remove the block position from the set of updating positions
            UPDATING_POSITIONS.get().remove(blockPos);
        }
        return blockState;
    }

    default BlockState sharePower(IWorld worldIn, BlockState blockState, BlockPos neighborPos, BlockState neighbor, int power, int neighborPower) {
        if (power == 0) {
            return blockState;
        }
        int sharedPower = Math.min(neighborPower + 1, MAX_POWER) - neighborPower;
        power = power - sharedPower;
        neighborPower = neighborPower + sharedPower;
        blockState = blockState.with(POWER, power);
        neighbor = neighbor.with(POWER, neighborPower);
        worldIn.setBlockState(neighborPos, neighbor, 3);
        return blockState;
    }

    static BlockState addPowerFromHealth(BlockState blockState, int healthAmount) {
        // Want to add 1 every 10 health on average
        int toAdd = healthAmount / 10;
        float addChance = (healthAmount % 10) / 10f;
        if (Math.random() < addChance) {
            toAdd++;
        }
        return addPower(blockState, toAdd);
    }

    static BlockState addPower(BlockState blockState, int amount) {
        int power = blockState.get(POWER);
        int newPower = Math.min(power + amount, MAX_POWER);
        return blockState.with(POWER, newPower);
    }

    default BlockState reducePower(BlockState blockState, int amount) {
        int power = blockState.get(POWER);
        int newPower = Math.max(power - amount, 0);
        return blockState.with(POWER, newPower);
    }

    default int getPower(BlockState blockState) {
        return blockState.getBlock() instanceof IVeinNetworkElement ? blockState.get(POWER) : 0;
    }

    default boolean isNotFull(BlockState blockState) {
        return getPower(blockState) != MAX_POWER;
    }
}
