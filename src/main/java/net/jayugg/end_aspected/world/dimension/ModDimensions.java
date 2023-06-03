package net.jayugg.end_aspected.world.dimension;

import net.jayugg.end_aspected.EndAspected;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class ModDimensions {
    public static RegistryKey<World> THE_VOID = RegistryKey.getOrCreateKey(Registry.WORLD_KEY,
            EndAspected.prefix("the_void"));
}