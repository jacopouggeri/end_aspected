package net.jayugg.end_aspected.world.biome;

import net.jayugg.end_aspected.EndAspected;
import net.jayugg.end_aspected.block.ModBlocks;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.surfacebuilders.*;

public class ModConfiguredSurfaceBuilders {
    private static <SC extends ISurfaceBuilderConfig>ConfiguredSurfaceBuilder<SC> register(String name, ConfiguredSurfaceBuilder<SC> csb) {
        return WorldGenRegistries.register(WorldGenRegistries.CONFIGURED_SURFACE_BUILDER, EndAspected.prefix(name), csb);
    }

    public static ConfiguredSurfaceBuilder<?> VOID_EXPANSE_SURFACE = register("void_expanse_surface", SurfaceBuilder.DEFAULT.func_242929_a( new SurfaceBuilderConfig(
            ModBlocks.VOID_STONE_BLOCK.get().getDefaultState(),
            ModBlocks.VOID_STONE_BLOCK.get().getDefaultState(),
            ModBlocks.VOID_STONE_BLOCK.get().getDefaultState()
    )));
}
