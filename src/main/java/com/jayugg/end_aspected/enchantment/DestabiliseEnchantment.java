package com.jayugg.end_aspected.enchantment;

import com.jayugg.end_aspected.effect.ModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import javax.annotation.Nonnull;

public class DestabiliseEnchantment extends Enchantment {
    public DestabiliseEnchantment(Rarity rarityIn, EquipmentSlot... slots) {
        super(rarityIn, EnchantmentCategory.WEAPON, slots);
    }

    public int getMinCost(int enchantmentLevel) {
        return 10 + 20 * (enchantmentLevel - 1);
    }

    public int getMaxCost(int enchantmentLevel) {
        return super.getMinCost(enchantmentLevel) + 50;
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }

    @Override
    protected boolean checkCompatibility(Enchantment enchantment) {
        return super.checkCompatibility(enchantment) && !(enchantment instanceof net.minecraft.world.item.enchantment.DamageEnchantment);
    }

    protected boolean canApplyTogether(Enchantment enchantment) {
        return !(enchantment instanceof net.minecraft.world.item.enchantment.DamageEnchantment);
    }

    public void onEntityDamaged(@Nonnull LivingEntity user, @Nonnull Entity target, int level) {
        if (target instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) target;
            int i = 20 + user.getRandom().nextInt(100 * level);
            if (livingEntity.isAffectedByPotions()) {
                livingEntity.addEffect(new MobEffectInstance(ModEffects.UNSTABLE_PHASE.get(), i, 1));
            }
        }
    }
}
