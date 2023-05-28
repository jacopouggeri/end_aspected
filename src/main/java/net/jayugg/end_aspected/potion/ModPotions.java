package net.jayugg.end_aspected.potion;

import net.jayugg.end_aspected.item.ModItems;
import net.jayugg.end_aspected.mixin.BrewingRecipeRegistryMixin;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static net.jayugg.end_aspected.effect.ModEffects.UNSTABLE_PHASE;

public class ModPotions {

    public static final DeferredRegister<Potion> POTIONS =
            DeferredRegister.create(ForgeRegistries.POTION_TYPES, "end_aspected");

    public static final RegistryObject<Potion> UNSTABLE_PHASE_POTION = POTIONS.register("unstable_phase",
            () -> new Potion(new EffectInstance(UNSTABLE_PHASE.get(), 3600)));

    public static void register(IEventBus eventBus) {
        POTIONS.register(eventBus);
        registerPotionRecipes();
    }

    private static void registerPotionRecipes() {
        BrewingRecipeRegistryMixin.invokeRegisterPotionRecipe(Potions.WATER, ModItems.ASPECT_SHARD.get(),
                ModPotions.UNSTABLE_PHASE_POTION.get());
    }
}
