package com.jayugg.end_aspected.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DamageEnchantment extends Enchantment {
    private static final String[] DAMAGE_NAMES = new String[]{"all", "undead", "arthropods", "ender"};
    private static final int[] MIN_COST = new int[]{1, 5, 5, 5};
    private static final int[] LEVEL_COST = new int[]{11, 8, 8, 8};
    private static final int[] LEVEL_COST_SPAN = new int[]{20, 20, 20, 20};
    public final int damageType;
    // Create a set containing the EntityType of all Ender entities
    List<EntityType<?>> enderEntities = new ArrayList<>();
    public DamageEnchantment(Enchantment.Rarity rarityIn, int damageTypeIn, EquipmentSlotType... slots) {
        super(rarityIn, EnchantmentType.WEAPON, slots);
        this.damageType = damageTypeIn;
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
        return MIN_COST[this.damageType] + (enchantmentLevel - 1) * LEVEL_COST[this.damageType];
    }

    @Override
    public int getMaxEnchantability(int enchantmentLevel) {
        return this.getMinEnchantability(enchantmentLevel) + LEVEL_COST_SPAN[this.damageType];
    }

    /**
     * Returns the maximum level that the enchantment can have.
     */
    @Override
    public int getMaxLevel() {
        return 5;
    }

    /**
     * Calculates the additional damage that will be dealt by an item with this enchantment. This alternative to
     * calcModifierDamage is sensitive to the targets EnumCreatureAttribute.
     */
    @Override
    public float calcDamageByCreature(int level, @Nonnull CreatureAttribute creatureType) {
        if (this.damageType == 0) {
            return 1.0F + (float)Math.max(0, level - 1) * 0.5F;
        } else if (this.damageType == 1 && creatureType == CreatureAttribute.UNDEAD) {
            return (float)level * 2.5F;
        } else if (this.damageType == 2 && creatureType == CreatureAttribute.ARTHROPOD) {
            return (float)level * 2.5F;
        } else {
            return 0.0F;
        }
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
            if (this.damageType == 3 && enderEntities.contains(Objects.requireNonNull(livingEntity).getType())){
                //System.out.println("DAMAGED ENDERMAN!");
                float damage = (level * 2.5f);
                livingEntity.attackEntityFrom(DamageSource.GENERIC, damage);
            } else if (this.damageType == 2 && livingEntity.getCreatureAttribute() == CreatureAttribute.ARTHROPOD) {
                int i = 20 + user.getRNG().nextInt(10 * level);
                livingEntity.addPotionEffect(new EffectInstance(Effects.SLOWNESS, i, 3));
            }


        }

    }

}