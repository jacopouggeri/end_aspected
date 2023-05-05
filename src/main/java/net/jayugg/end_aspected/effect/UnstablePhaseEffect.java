package net.jayugg.end_aspected.effect;

import net.jayugg.end_aspected.config.ModConfig;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class UnstablePhaseEffect extends MobEffect {
    public UnstablePhaseEffect(MobEffectCategory typeIn, int liquidColorIn) {
        super(typeIn, liquidColorIn);
    }

    public static void damageTeleporter(Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            MobEffect unstablePhase = ModEffects.UNSTABLE_PHASE.get();
            if (livingEntity.hasEffect(unstablePhase)) {
                float damageFraction = (float) ModConfig.unstablePhasePercentDamage.get() / 100;
                float damage = damageFraction * livingEntity.getHealth();
                livingEntity.hurt(DamageSource.GENERIC, damage);
            }
        }
    }
}
