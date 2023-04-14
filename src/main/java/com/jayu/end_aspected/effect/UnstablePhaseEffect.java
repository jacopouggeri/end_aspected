package com.jayu.end_aspected.effect;

import com.jayu.end_aspected.config.ModConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.EntityTeleportEvent;

public class UnstablePhaseEffect extends Effect {
    public UnstablePhaseEffect(EffectType typeIn, int liquidColorIn) {
        super(typeIn, liquidColorIn);
    }

    public static void blockEventEntity(EntityTeleportEvent.EnderEntity event, Entity entity) {
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            Effect unstablePhase = ModEffects.UNSTABLE_PHASE.get();
            if (livingEntity.isPotionActive(unstablePhase)) {
                float damageFraction = (float) ModConfig.unstablePhasePercentDamage.get()/100;
                float damage = damageFraction * livingEntity.getHealth();
                event.setCanceled(true);
                livingEntity.attackEntityFrom(DamageSource.GENERIC, damage);
            }
        }
    }
}