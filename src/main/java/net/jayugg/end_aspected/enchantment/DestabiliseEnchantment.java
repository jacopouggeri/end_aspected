package net.jayugg.end_aspected.enchantment;

import net.jayugg.end_aspected.effect.ModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

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
    protected boolean checkCompatibility(@Nonnull Enchantment enchantment) {
        return super.checkCompatibility(enchantment) && !(enchantment instanceof DestabiliseEnchantment);
    }

    public static void onLivingHurt(LivingHurtEvent event) {
        // Get the damage source's entity
        Entity sourceEntity = event.getSource().getEntity();

        // Check if the source entity is a LivingEntity and the code is on the server side
        if (sourceEntity instanceof LivingEntity user && !event.getEntity().level.isClientSide()) {
            LivingEntity target = event.getEntity();

            // Get the level of the enchantment on the user's weapon
            int level = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.DESTABILISE.get(), user);
            // If the enchantment level is greater than 0, calculate and set the new damage amount
            if (level > 0) {
                applyEffect(user, target, level);
            }
        }
    }

    private static void applyEffect(@Nonnull LivingEntity user, @Nonnull LivingEntity target, int level) {
        int i = 20 + user.getRandom().nextInt(100 * level);
        if (target.isAffectedByPotions()) {
            target.addEffect(new MobEffectInstance(ModEffects.UNSTABLE_PHASE.get(), i, 1));
        }
    }
}
