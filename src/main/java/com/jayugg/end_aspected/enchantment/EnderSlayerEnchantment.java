package com.jayugg.end_aspected.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EnderSlayerEnchantment extends Enchantment {
    private static final int MIN_COST = 5;
    private static final int LEVEL_COST = 8;
    private static final int LEVEL_COST_SPAN = 20;

    // Create a set containing the EntityType of all Ender entities
    static List<EntityType<?>> enderEntities = new ArrayList<>();
    public EnderSlayerEnchantment(Enchantment.Rarity rarityIn, EquipmentSlotType... slots) {
        super(rarityIn, EnchantmentType.WEAPON, slots);
        enderEntities.add(EntityType.ENDERMAN);
        enderEntities.add(EntityType.SHULKER);
        enderEntities.add(EntityType.ENDERMITE);
        enderEntities.add(EntityType.PHANTOM);
        enderEntities.add(EntityType.PHANTOM);
        enderEntities.add(EntityType.ENDER_DRAGON);
    }

    public static float calculateDamage(@Nonnull Entity target, float lastDamage, int level) {
        if (target instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)target;
            if (enderEntities.contains(Objects.requireNonNull(livingEntity).getType())){
                return lastDamage * (1f + 0.2f*level);
            }
        }
        return lastDamage;
    }

    @Override
    public int getMaxEnchantability(int enchantmentLevel) {
        return this.getMinEnchantability(enchantmentLevel) + LEVEL_COST_SPAN;
    }

    // Called whenever a mob is damaged
    public static void getLastDamageInflicted(LivingHurtEvent event) {
        // Find player and target anc check that code is on server
        LivingEntity user = (LivingEntity) event.getSource().getTrueSource();

        LivingEntity target = event.getEntityLiving();

        if (user != null && !user.getEntityWorld().isRemote) {
            ItemStack stack = user.getHeldItem(Hand.MAIN_HAND);
            int level = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.ENDER_SLAYER.get(), stack);

            // Set damage
            event.setAmount(calculateDamage(target, event.getAmount(), level));
            //LOGGER.info("DAMAGED ENDERMAN!");
        }
    }

    // Returns the minimal value of enchantability needed on the enchantment level passed.
    @Override
    public int getMinEnchantability(int enchantmentLevel) {
        return MIN_COST + (enchantmentLevel - 1) * LEVEL_COST;
    }

    // Returns the maximum level that the enchantment can have.
    @Override
    public int getMaxLevel() {
        return 5;
    }

    // Determines if the enchantment passed can be applied together with this enchantment.
    @Override
    public boolean canApplyTogether(@Nonnull Enchantment enchantment) {
        return !(enchantment instanceof net.minecraft.enchantment.DamageEnchantment);
    }

    // Determines if this enchantment can be applied to a specific ItemStack.
    @Override
    public boolean canApply(ItemStack stack) {
        return stack.getItem() instanceof AxeItem || super.canApply(stack);
    }

}