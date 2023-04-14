package com.jayugg.end_aspected.effect;

import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static com.jayugg.end_aspected.EndAspected.MOD_ID;

public class ModEffects{
    public static final DeferredRegister<Effect> EFFECTS =
            DeferredRegister.create(ForgeRegistries.POTIONS, MOD_ID);

    public static final RegistryObject<Effect> UNSTABLE_PHASE = EFFECTS.register("unstable_phase",
            () -> new UnstablePhaseEffect(EffectType.HARMFUL, 0xFFFFFF)
    );

    public static void register(IEventBus eventBus) {
        EFFECTS.register(eventBus);
    }
}