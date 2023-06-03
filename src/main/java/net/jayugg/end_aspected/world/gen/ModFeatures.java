package net.jayugg.end_aspected.world.gen;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.placement.AtSurfaceWithExtraConfig;
import net.minecraft.world.gen.placement.Placement;

import static net.jayugg.end_aspected.world.gen.ModConfiguredFeatures.VOID_TREE_BASE;

public class ModFeatures {
    public static final ConfiguredFeature<?, ?> TREES_VOID = register("trees_void", Feature.RANDOM_SELECTOR.withConfiguration(new MultipleRandomFeatureConfig(ImmutableList.of(VOID_TREE_BASE.withChance(0.8F)), VOID_TREE_BASE)).withPlacement(Features.Placements.HEIGHTMAP_PLACEMENT).withPlacement(Placement.COUNT_EXTRA.configure(new AtSurfaceWithExtraConfig(1, 0.1F, 1))));

    private static <FC extends IFeatureConfig> ConfiguredFeature<FC, ?> register(String key, ConfiguredFeature<FC, ?> configuredFeature) {
        return Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, key, configuredFeature);
    }
}
