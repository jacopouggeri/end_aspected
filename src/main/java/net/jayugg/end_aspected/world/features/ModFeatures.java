package net.jayugg.end_aspected.world.features;

import net.jayugg.end_aspected.EndAspected;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.*;
import net.minecraftforge.registries.ForgeRegistries;

public class ModFeatures {

    // Generic Features
    public static final Feature<BaseTreeFeatureConfig> VOID_TREE_FEATURE = register(EndAspected.prefix("void_tree"), new VoidTreeFeature(BaseTreeFeatureConfig.CODEC));

    private static <FC extends IFeatureConfig> Feature<FC> register(ResourceLocation key, Feature<FC> configuredFeature) {
        configuredFeature.setRegistryName(key);
        ForgeRegistries.FEATURES.register(configuredFeature);
        return configuredFeature;
    }

}
