package net.jayugg.end_aspected.potion;

import net.jayugg.end_aspected.EndAspected;
import net.jayugg.end_aspected.effect.ModEffects;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static net.jayugg.end_aspected.effect.ModEffects.UNSTABLE_PHASE;

public class ModPotions {

    public static final DeferredRegister<Potion> POTIONS =
            DeferredRegister.create(ForgeRegistries.POTION_TYPES, EndAspected.MOD_ID);

    public static final RegistryObject<Potion> UNSTABLE_PHASE_POTION = POTIONS.register("unstable_phase",
            () -> new Potion(new EffectInstance(UNSTABLE_PHASE.get(), 3600)));

    public static final RegistryObject<Potion> VOIDRUE = POTIONS.register("voidrue",
            () -> new Potion(new EffectInstance(ModEffects.VOIDRUE.get(), 3600)));

    public static void register(IEventBus eventBus) { POTIONS.register(eventBus); }
}
