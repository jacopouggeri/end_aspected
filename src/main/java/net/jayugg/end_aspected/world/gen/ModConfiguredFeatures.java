package net.jayugg.end_aspected.world.gen;

import com.google.common.collect.ImmutableList;
import net.jayugg.end_aspected.EndAspected;
import net.jayugg.end_aspected.block.ModBlocks;
import net.jayugg.end_aspected.block.tree.decorator.VoidVeinTreeDecorator;
import net.jayugg.end_aspected.imported.feature.ModFeatures;
import net.jayugg.end_aspected.imported.treeplacers.*;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.blockstateprovider.WeightedBlockStateProvider;
import net.minecraft.world.gen.feature.*;

public class ModConfiguredFeatures {
    private final static int LEAF_SHAG_FACTOR = 24;

    private final static int BASE_HEIGHT = 5;
    public static final BaseTreeFeatureConfig VOID_TREE = new BaseTreeFeatureConfig.Builder(
            new SimpleBlockStateProvider(ModBlocks.VOID_STEM.get().getDefaultState()),
            new SimpleBlockStateProvider(ModBlocks.VOID_LEAVES.get().getDefaultState()),
            new LeafSpheroidFoliagePlacer(4.0f, 2f, FeatureSpread.create(0), 3, 0, -0.25f, (int) (LEAF_SHAG_FACTOR * 0.666f)),
            new TrunkRiser(BASE_HEIGHT, new BranchingTrunkPlacer(6, 2, 0, 1, new BranchesConfig(0, 6, 7, 4, 0.3, 0.25), false)),
            new TwoLayerFeature(1, 0, 1))
            .setMaxWaterDepth(6)
            .setDecorators(ImmutableList.of(
                            new TreeRootsDecorator(4, 2, 15, new SimpleBlockStateProvider(ModBlocks.VOID_STEM.get().getDefaultState()), (new WeightedBlockStateProvider())
                                    .addWeightedBlockstate(ModBlocks.VOID_STEM.get().getDefaultState(), 4)
                                    .addWeightedBlockstate(ModBlocks.VOID_LEAVES.get().getDefaultState(), 1)),
                    VoidVeinTreeDecorator.DECORATOR.setBaseHeight(BASE_HEIGHT)
                    )
            )
            .build();

    public static final ConfiguredFeature<BaseTreeFeatureConfig, ? extends Feature<?>> VOID_TREE_BASE = ModFeatures.registerWorldFeature(EndAspected.prefix("tree/void_tree_base"), Feature.TREE.withConfiguration(VOID_TREE));

}
