package net.jayugg.end_aspected.block.parent;

import net.minecraft.block.BlockState;
import net.minecraft.state.IntegerProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

import java.util.HashSet;
import java.util.Set;

import static net.jayugg.end_aspected.EndAspected.LOGGER;

public interface IVeinNetworkElement extends IConnectedFlora {
    int MAX_CHARGE = 4;
    IntegerProperty CHARGE = IntegerProperty.create("charge", 0, MAX_CHARGE);
    // Add a ThreadLocal to store the updating state for each thread
    ThreadLocal<Set<BlockPos>> UPDATING_POSITIONS = ThreadLocal.withInitial(HashSet::new);

    default BlockState shareChargeToNeighbors(BlockState blockState, IWorld worldIn, BlockPos blockPos) {
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
        int power = blockState.get(CHARGE);

        if (power == 0 || UPDATING_POSITIONS.get().contains(blockPos)) {
            return blockState;
        }
        UPDATING_POSITIONS.get().add(blockPos);
        try {
            for (Direction direction : Direction.values()) {
                blockpos$mutable.setAndMove(blockPos, direction);
                BlockState neighbor = worldIn.getBlockState(blockpos$mutable);

                if (neighbor.getBlock() instanceof IVeinNetworkElement) {
                    int neighborPower = neighbor.get(CHARGE);
                    int neighborDistance = getDistance(neighbor);
                    int currentDistance = getDistance(blockState);

                    // Share power if the neighbor is at the same distance and has lower power or if the neighbor is closer to a node
                    // Crucial to avoid infinite loops: only share power if it doesn't lower the power of the current block below the neighbor's power
                    if ((neighborDistance == currentDistance && neighborPower + 1 < power) || neighborDistance < currentDistance) {
                        LOGGER.info("Sharing charge from {} to {}", blockPos, blockpos$mutable);
                        blockState = shareCharge(worldIn, blockState, blockpos$mutable, neighbor, power, neighborPower);
                    }
                }
            }
        } finally {
            // Remove the block position from the set of updating positions
            UPDATING_POSITIONS.get().remove(blockPos);
        }
        return blockState;
    }

    default BlockState shareCharge(IWorld worldIn, BlockState blockState, BlockPos neighborPos, BlockState neighbor, int power, int neighborCharge) {
        if (power == 0) {
            return blockState;
        }
        int sharedPower = Math.min(neighborCharge + 1, MAX_CHARGE) - neighborCharge;
        power = power - sharedPower;
        neighborCharge = neighborCharge + sharedPower;
        blockState = blockState.with(CHARGE, power);
        neighbor = neighbor.with(CHARGE, neighborCharge);
        worldIn.setBlockState(neighborPos, neighbor, 3);
        return blockState;
    }

    static BlockState addChargeFromHealth(BlockState blockState, int healthAmount) {
        // Want to add 1 every 10 health on average
        int toAdd = healthAmount / 10;
        float addChance = (healthAmount % 10) / 10f;
        if (Math.random() < addChance) {
            toAdd++;
        }
        return addCharge(blockState, toAdd);
    }

    static BlockState addCharge(BlockState blockState, int amount) {
        int charge = blockState.get(CHARGE);
        int newCharge = Math.min(charge + amount, MAX_CHARGE);
        return blockState.with(CHARGE, newCharge);
    }

    default BlockState reduceCharge(BlockState blockState, int amount) {
        int charge = blockState.get(CHARGE);
        int newCharge = Math.max(charge - amount, 0);
        return blockState.with(CHARGE, newCharge);
    }

    default int getCharge(BlockState blockState) {
        return blockState.getBlock() instanceof IVeinNetworkElement ? blockState.get(CHARGE) : 0;
    }

    default boolean isFull(BlockState blockState) {
        return getCharge(blockState) == MAX_CHARGE;
    }

    default boolean isNotEmpty(BlockState blockState) {
        return getCharge(blockState) > 0;
    }

}
