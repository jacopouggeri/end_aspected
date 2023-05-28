package world.gen;

import net.jayugg.end_aspected.block.ModBlocks;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.foliageplacer.JungleFoliagePlacer;
import net.minecraft.world.gen.trunkplacer.StraightTrunkPlacer;

import java.util.OptionalInt;

public class ModConfiguredFeatures {
    public static final ConfiguredFeature<BaseTreeFeatureConfig, ?> VOID =
            register("void", Feature.TREE.withConfiguration((
                    new BaseTreeFeatureConfig.Builder(
                            new SimpleBlockStateProvider(ModBlocks.VOID_STEM.get().getDefaultState()),
                            new SimpleBlockStateProvider(ModBlocks.VOID_LEAVES.get().getDefaultState()),
                            new JungleFoliagePlacer(FeatureSpread.create(2), FeatureSpread.create(1), 4),
                            new StraightTrunkPlacer(6, 2, 2),
                            new ThreeLayerFeature(3, 2, 1, 0, 1, OptionalInt.of(3)))).setIgnoreVines().build()));


    private static <FC extends IFeatureConfig> ConfiguredFeature<FC, ?> register(String key,
                                                                                 ConfiguredFeature<FC, ?> configuredFeature) {
        return Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, key, configuredFeature);
    }
}
