package com.jayugg.end_aspected.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EnderSlayerEnchantment extends Enchantment {
    private static final int MIN_COST = 5;
    private static final int LEVEL_COST = 8;
    private static final int LEVEL_COST_SPAN = 20;

    // Create a set containing the EntityType of all Ender entities
    List<EntityType<?>> enderEntities = new ArrayList<>();
    public EnderSlayerEnchantment(Enchantment.Rarity rarityIn, EquipmentSlotType... slots) {
        super(rarityIn, EnchantmentType.WEAPON, slots);
        enderEntities.add(EntityType.ENDERMAN);
        enderEntities.add(EntityType.SHULKER);
        enderEntities.add(EntityType.ENDERMITE);
        enderEntities.add(EntityType.PHANTOM);
        enderEntities.add(EntityType.PHANTOM);
        enderEntities.add(EntityType.ENDER_DRAGON);
    }


    /**
     * Returns the minimal value of enchantability needed on the enchantment level passed.
     */
    @Override
    public int getMinEnchantability(int enchantmentLevel) {
        return MIN_COST + (enchantmentLevel - 1) * LEVEL_COST;
    }

    @Override
    public int getMaxEnchantability(int enchantmentLevel) {
        return this.getMinEnchantability(enchantmentLevel) + LEVEL_COST_SPAN;
    }

    /**
     * Returns the maximum level that the enchantment can have.
     */
    @Override
    public int getMaxLevel() {
        return 5;
    }

    /**
     * Determines if the enchantment passed can be applyied together with this enchantment.
     */
    @Override
    public boolean canApplyTogether(@Nonnull Enchantment ench) {
        return !(ench instanceof net.minecraft.enchantment.DamageEnchantment);
    }

    /**
     * Determines if this enchantment can be applied to a specific ItemStack.
     */
    @Override
    public boolean canApply(ItemStack stack) {
        return stack.getItem() instanceof AxeItem || super.canApply(stack);
    }

    /**
     * Called whenever a mob is damaged with an item that has this enchantment on it.
     */
    public void onEntityDamaged(@Nonnull LivingEntity user, @Nonnull Entity target, int level) {
        if (target instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)target;
            if (enderEntities.contains(Objects.requireNonNull(livingEntity).getType())){
                //System.out.println("DAMAGED ENDERMAN!");
                float damage = (level * 4.0f);
                livingEntity.attackEntityFrom(DamageSource.GENERIC, damage);
            }
        }
    }

}