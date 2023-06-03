package net.jayugg.end_aspected.block.tree;

import net.minecraft.block.BlockState;
import net.minecraft.block.trees.Tree;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.server.ServerWorld;
import net.jayugg.end_aspected.world.features.ModConfiguredFeatures;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

@ParametersAreNonnullByDefault
public class VoidTree extends Tree {

    @Nullable
    @Override
    protected ConfiguredFeature<BaseTreeFeatureConfig, ?> getTreeFeature(@Nonnull Random randomIn, boolean largeHive) {
        return ModConfiguredFeatures.VOID_TREE_BASE;
    }

    @Override
    public boolean attemptGrowTree(ServerWorld world, ChunkGenerator chunkGenerator, BlockPos pos, BlockState state, Random rand) {
        ConfiguredFeature<BaseTreeFeatureConfig, ?> configuredfeature = this.getTreeFeature(rand, false);
        if (configuredfeature == null) {
            return false;
        } else {
            // Remove sapling
            world.setBlockState(pos, state.getFluidState().getBlockState(), 4);
            configuredfeature.config.forcePlacement();
            // Try to place tree, if not, place sapling back
            if (configuredfeature.generate(world, chunkGenerator, rand, pos)) {
                return true;
            } else {
                world.setBlockState(pos, state, 4);
                return false;
            }
        }
    }
}
