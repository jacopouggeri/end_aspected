package net.jayugg.end_aspected.enchantment;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EnderSlayerEnchantment extends Enchantment {
    private static final int MIN_COST = 5;
    private static final int LEVEL_COST = 8;

    // Create a set containing the EntityType of all Ender entities
    static List<EntityType<?>> enderEntities = new ArrayList<>();

    public EnderSlayerEnchantment(Rarity rarityIn, EnchantmentCategory categoryIn, EquipmentSlot[] slots) {
        super(rarityIn, categoryIn, slots);
        enderEntities.add(EntityType.ENDERMAN);
        enderEntities.add(EntityType.SHULKER);
        enderEntities.add(EntityType.ENDERMITE);
        enderEntities.add(EntityType.PHANTOM);
        enderEntities.add(EntityType.ENDER_DRAGON);
    }

    public static float calculateDamage(@NotNull Entity target, float lastDamage, int level) {
        if (target instanceof LivingEntity livingEntity) {
            if (enderEntities.contains(Objects.requireNonNull(livingEntity).getType())) {
                return lastDamage * (1f + 0.2f * level);
            }
        }
        return lastDamage;
    }

    // Called whenever a mob is damaged
    public static void getLastDamageInflicted(LivingHurtEvent event) {
        // Get the damage source's entity
        Entity sourceEntity = event.getSource().getEntity();

        // Check if the source entity is a LivingEntity and the code is on the server side
        if (sourceEntity instanceof LivingEntity user && !event.getEntity().level.isClientSide()) {
            LivingEntity target = event.getEntity();

            // Get the level of the Ender Slayer enchantment on the user's weapon
            int level = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.ENDER_SLAYER.get(), user);

            // If the enchantment level is greater than 0, calculate and set the new damage amount
            if (level > 0) {
                float newDamage = calculateDamage(target, event.getAmount(), level);
                event.setAmount(newDamage);
                //LOGGER.info("DAMAGED ENDERMAN!");
            }
        }
    }

    // Returns the minimal value of enchantability needed on the enchantment level passed.
    @Override
    public int getMinCost(int enchantmentLevel) {
        return MIN_COST + (enchantmentLevel - 1) * LEVEL_COST;
    }

    // Returns the maximum level that the enchantment can have.
    @Override
    public int getMaxLevel() {
        return 5;
    }

    // Determines if the enchantment passed can be applied together with this enchantment.
    @Override
    public boolean checkCompatibility(@NotNull Enchantment enchantment) {
        return !(enchantment instanceof net.minecraft.world.item.enchantment.DamageEnchantment) && !(enchantment instanceof EnderSlayerEnchantment) && super.checkCompatibility(enchantment);
    }

    // Determines if this enchantment can be applied to a specific ItemStack.
    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof AxeItem || super.canEnchant(stack);
    }

}
