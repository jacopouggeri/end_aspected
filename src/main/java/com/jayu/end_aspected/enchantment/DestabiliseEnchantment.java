package com.jayu.end_aspected.enchantment;

import com.jayu.end_aspected.effect.ModEffects;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.potion.EffectInstance;

import javax.annotation.Nonnull;

public class DestabiliseEnchantment extends Enchantment {
    public DestabiliseEnchantment(Enchantment.Rarity rarityIn, EquipmentSlotType... slots) {
        super(rarityIn, EnchantmentType.WEAPON, slots);
    }

    public int getMinEnchantability(int enchantmentLevel) {
        return 10 + 20 * (enchantmentLevel - 1);
    }

    public int getMaxEnchantability(int enchantmentLevel) {
        return super.getMinEnchantability(enchantmentLevel) + 50;
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }

    @Override
    public boolean isTreasureEnchantment() {
        return true;
    }

    public void onEntityDamaged(@Nonnull LivingEntity user, @Nonnull Entity target, int level) {
        LivingEntity livingEntity = (LivingEntity)target;
        int i = 20 + user.getRNG().nextInt(10 * level);
        livingEntity.addPotionEffect(new EffectInstance(ModEffects.UNSTABLE_PHASE.get(), i, 1));
    }

}
