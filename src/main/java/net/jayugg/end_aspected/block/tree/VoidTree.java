package net.jayugg.end_aspected.block.tree;

import net.minecraft.block.trees.Tree;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import world.gen.ModConfiguredFeatures;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public class VoidTree extends Tree {

    @Nullable
    @Override
    protected ConfiguredFeature<BaseTreeFeatureConfig, ?> getTreeFeature(@Nonnull Random randomIn, boolean largeHive) {
        return ModConfiguredFeatures.VOID_TREE_BASE;
    }
}
