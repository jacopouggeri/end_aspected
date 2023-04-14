package com.jayugg.end_aspected.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class AspectedArrowItem extends ArrowItem {
    public AspectedArrowItem(Properties properties) {
        super(properties);
    }

    @Override
    public @Nonnull ArrowEntity createArrow(World world, @Nonnull ItemStack stack, @Nonnull LivingEntity shooter) {
        // Create a new instance of your custom arrow entity
        ArrowEntity arrowEntity = new ArrowEntity(shooter.world, shooter);

        // Set the arrow's position, motion, and shooter
        arrowEntity.setPosition(shooter.getPosX(), shooter.getPosY() + shooter.getEyeHeight(), shooter.getPosZ());
        arrowEntity.setMotion(shooter.getLookVec().scale(100.0));
        arrowEntity.setShooter(shooter);

        // Set the arrow's damage and knockback
        arrowEntity.setDamage(2.0);
        arrowEntity.setKnockbackStrength(1);
        arrowEntity.setNoGravity(true);
        arrowEntity.setInvisible(true);

        // Spawn the arrow entity in the world
        world.addEntity(arrowEntity);

        // Return the arrow entity
        return arrowEntity;
    }
}
