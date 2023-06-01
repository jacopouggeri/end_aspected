package net.jayugg.end_aspected.block.parent;

import net.minecraft.block.BlockState;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public interface IVeinNetworkElement extends IConnectedFlora {
    int MAX_POWER = 4;
    IntegerProperty POWER = BlockStateProperties.POWER_0_15;
    default BlockState shareEnergyToNeighbors(BlockState blockState, IWorld worldIn, BlockPos pos) {
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
        int energy = blockState.get(POWER);

        if (energy == 0) {
            return blockState;
        }

        for(Direction direction : Direction.values()) {
            blockpos$mutable.setAndMove(pos, direction);
            BlockState neighbor = worldIn.getBlockState(blockpos$mutable);

            if (neighbor.getBlock() instanceof IVeinNetworkElement) {
                int neighborEnergy = neighbor.get(POWER);
                int neighborDistance = getDistance(neighbor);
                int currentDistance = getDistance(blockState);

                // Share energy if the neighbor is at the same distance and has lower energy or if the neighbor is closer to a node
                if ((neighborDistance == currentDistance && neighborEnergy < energy) || neighborDistance < currentDistance) {
                    blockState = shareEnergy(worldIn, blockState, blockpos$mutable, neighbor, energy, neighborEnergy);
                }
            }
        }
        return blockState;
    }

    default BlockState shareEnergy(IWorld worldIn, BlockState blockState, BlockPos neighborPos, BlockState neighbor, int energy, int neighborEnergy) {
        int sharedEnergy = Math.min(neighborEnergy + 1, MAX_POWER) - neighborEnergy;
        energy = energy - sharedEnergy;
        neighborEnergy = neighborEnergy + sharedEnergy;
        blockState = blockState.with(POWER, energy);
        neighbor = neighbor.with(POWER, neighborEnergy);
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
