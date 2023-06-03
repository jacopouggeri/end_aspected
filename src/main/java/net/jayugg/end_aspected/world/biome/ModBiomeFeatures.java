package net.jayugg.end_aspected.world.biome;

import net.jayugg.end_aspected.entity.ModEntityTypes;
import net.jayugg.end_aspected.world.features.ModConfiguredFeatures;
import net.minecraft.entity.EntityClassification;
import net.minecraft.world.biome.BiomeGenerationSettings;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.GenerationStage;

public class ModBiomeFeatures {
    public static void withVoidTrees(BiomeGenerationSettings.Builder builder) {
        builder.withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, ModConfiguredFeatures.TREES_VOID);
    }

    public static void withVoidlings(MobSpawnInfo.Builder builder) {
        builder.withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(ModEntityTypes.VOIDMITE.get(), 10, 4, 4));
        builder.withSpawner(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(ModEntityTypes.VOIDBAT.get(), 10, 4, 4));
    }
}
