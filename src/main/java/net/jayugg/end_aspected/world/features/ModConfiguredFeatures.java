package net.jayugg.end_aspected.world.features;

import com.google.common.collect.ImmutableList;
import net.jayugg.end_aspected.EndAspected;
import net.jayugg.end_aspected.block.ModBlocks;
import net.jayugg.end_aspected.block.tree.decorator.VoidVeinTreeDecorator;
import net.jayugg.end_aspected.imported.treeplacers.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.blockplacer.SimpleBlockPlacer;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.blockstateprovider.WeightedBlockStateProvider;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.placement.AtSurfaceWithExtraConfig;
import net.minecraft.world.gen.placement.Placement;

public class ModConfiguredFeatures {

    // Trees
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
                            new TreeRootsDecorator(4, 2, 5, new SimpleBlockStateProvider(ModBlocks.VOID_STEM.get().getDefaultState()), (new WeightedBlockStateProvider())
                                    .addWeightedBlockstate(ModBlocks.VOID_STEM.get().getDefaultState(), 4)),
                    VoidVeinTreeDecorator.DECORATOR.setBaseHeight(BASE_HEIGHT)
                    )
            )
            .build();

    public static final ConfiguredFeature<BlockClusterFeatureConfig, ? extends Feature<?>> VOID_VEIN_PATCH = register(EndAspected.prefix("gen/void_vein_patch"),
            Feature.RANDOM_PATCH
            .withConfiguration(new BlockClusterFeatureConfig.Builder(
                    new SimpleBlockStateProvider(ModBlocks.VOID_VEIN.get().getDefaultState()),
                    new SimpleBlockPlacer()
            )
            .build())
    );

    public static final ConfiguredFeature<BaseTreeFeatureConfig, ? extends Feature<?>> VOID_TREE_BASE = register(EndAspected.prefix("tree/void_tree_base"), ModFeatures.VOID_TREE_FEATURE.withConfiguration(VOID_TREE));

    // Biome Generation Features
    public static final ConfiguredFeature<?, ?> TREES_VOID = register(EndAspected.prefix("gen/trees_void"), Feature.RANDOM_SELECTOR.withConfiguration(new MultipleRandomFeatureConfig(ImmutableList.of(VOID_TREE_BASE.withChance(0.8F)), VOID_TREE_BASE)).withPlacement(Features.Placements.HEIGHTMAP_PLACEMENT).withPlacement(Placement.COUNT_EXTRA.configure(new AtSurfaceWithExtraConfig(1, 0.1F, 1))));

    private static <FC extends IFeatureConfig, F extends Feature<FC>> ConfiguredFeature<FC, F> register(ResourceLocation key, ConfiguredFeature<FC, F> configuredFeature) {
        return Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, key, configuredFeature);
    }
}
