package net.jayugg.end_aspected.potion;

import net.jayugg.end_aspected.EndAspected;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static net.jayugg.end_aspected.effect.ModEffects.UNSTABLE_PHASE;

public class ModPotions {

    public static final DeferredRegister<Potion> POTIONS =
            DeferredRegister.create(ForgeRegistries.POTIONS, EndAspected.MOD_ID);

    public static final RegistryObject<Potion> UNSTABLE_PHASE_POTION = POTIONS.register("unstable_phase",
            () -> new Potion(new MobEffectInstance(UNSTABLE_PHASE.get(), 3600)));

    public static void register(IEventBus eventBus) { POTIONS.register(eventBus); }
}
