package net.jayugg.end_aspected.world.biome;

import net.jayugg.end_aspected.EndAspected;
import net.jayugg.end_aspected.world.features.ModConfiguredFeatures;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.biome.*;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Features;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModBiomes {
    public static final DeferredRegister<Biome> BIOMES = DeferredRegister.create(ForgeRegistries.BIOMES, EndAspected.MOD_ID);

    public static final RegistryObject<Biome> THE_VOID = BIOMES.register("the_void", ModBiomes::makeTheVoidBiome);

    public static final RegistryObject<Biome> VOID_EXPANSE = BIOMES.register("void_expanse", ModBiomes::makeVoidExpanseBiome);

    public static final RegistryObject<Biome> VOID_BARRENS = BIOMES.register("void_barrens", ModBiomes::makeVoidBarrensBiome);

    private static Biome makeVoidBiome(BiomeGenerationSettings.Builder generationSettingsBuilder) {
        MobSpawnInfo.Builder mobspawninfo$builder = new MobSpawnInfo.Builder();
        ModBiomeFeatures.withVoidlings(mobspawninfo$builder);
        return (new Biome.Builder()).precipitation(Biome.RainType.NONE)
                .category(Biome.Category.THEEND).depth(0.1F)
                .scale(0.2F).temperature(0.5F)
                .downfall(0.5F)
                .setEffects((new BiomeAmbience.Builder())
                        .setParticle(new ParticleEffectAmbience(ParticleTypes.WARPED_SPORE, 0.03f))
                        .setWaterColor(4159204)
                        .setWaterFogColor(329011)
                        .setFogColor(10518688)
                        .withSkyColor(0)
                        .setMoodSound(MoodSoundAmbience.DEFAULT_CAVE).build())
                .withMobSpawnSettings(mobspawninfo$builder.build())
                .withGenerationSettings(generationSettingsBuilder.build()).build();
    }

    public static Biome makeTheVoidBiome() {
        BiomeGenerationSettings.Builder biomegenerationsettings$builder = (new BiomeGenerationSettings.Builder()).withSurfaceBuilder(ModConfiguredSurfaceBuilders.VOID_BARREN_SURFACE).withFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, Features.END_SPIKE);
        return makeVoidBiome(biomegenerationsettings$builder);
    }

    public static Biome makeVoidBarrensBiome() {
        BiomeGenerationSettings.Builder biomegenerationsettings$builder = (new BiomeGenerationSettings.Builder()).withSurfaceBuilder(ModConfiguredSurfaceBuilders.VOID_BARREN_SURFACE).withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, ModConfiguredFeatures.VOID_VEIN_PATCH);
        return makeVoidBiome(biomegenerationsettings$builder);
    }

    public static Biome makeVoidExpanseBiome() {
        BiomeGenerationSettings.Builder biomegenerationsettings$builder = (new BiomeGenerationSettings.Builder()).withSurfaceBuilder(ModConfiguredSurfaceBuilders.VOID_EXPANSE_SURFACE).withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, ModConfiguredFeatures.VOID_TREE_BASE);
        return makeVoidBiome(biomegenerationsettings$builder);
    }

    public static void register(IEventBus eventBus) {
        BIOMES.register(eventBus);
    }
}
