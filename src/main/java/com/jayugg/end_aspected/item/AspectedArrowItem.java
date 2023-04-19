package com.jayugg.end_aspected.item;

import com.jayugg.end_aspected.entity.AspectedArrowEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;

public class AspectedArrowItem extends ArrowItem {

    public static double TELEPORT_BUFFER_DISTANCE;

    public AspectedArrowItem(Properties properties) {
        super(properties);
        TELEPORT_BUFFER_DISTANCE = 8;
    }

    private int getMaxTeleportDistance(LivingEntity shooter) {
        return 20 * 16;
    }

    @Override
    public @Nonnull AspectedArrowEntity createArrow(@Nonnull Level level, @Nonnull ItemStack stack, @Nonnull LivingEntity shooter) {
        Vec3 shooterPos = shooter.getEyePosition(1.0f);
        Vec3 lookVec = shooter.getLookAngle();
        Vec3 endVec = shooterPos.add(lookVec.normalize().scale(getMaxTeleportDistance(shooter)));

        // Create a new instance of your custom arrow entity
        AspectedArrowEntity arrowEntity = new AspectedArrowEntity(level, shooter);
        // Set the arrow's position, motion, and shooter
        arrowEntity.setOwner(shooter);
        arrowEntity.setPos(shooterPos.x, shooterPos.y, shooterPos.z);
        arrowEntity.setDeltaMovement(shooter.getLookAngle().scale(1.0));

        BlockHitResult hitResult = level.clip(new ClipContext(shooterPos, endVec, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, shooter));

        if (hitResult.getType() != HitResult.Type.MISS) {
            handleRaytraceHit(hitResult, shooter, arrowEntity);
            arrowEntity.setTeleportedFlag(true);
        }

        // Set other arrow parameters
        arrowEntity.setNoGravity(true);

        // Return the arrow entity
        return arrowEntity;
    }

    private void handleRaytraceHit(HitResult hitResult, LivingEntity shooter, AspectedArrowEntity arrowEntity) {
        // If the raytrace hit something, spawn the arrow teleportDist blocks before it in the direction the player is looking
        Vec3 hitVec = hitResult.getLocation();
        Vec3 hitDist = hitVec.subtract(shooter.getEyePosition(1.0f));

        if ((hitDist.lengthSqr() > Math.pow(TELEPORT_BUFFER_DISTANCE + 0.5, 2)) && (hitResult.getType() != HitResult.Type.MISS)) {
            Vec3 teleportPos = hitVec.subtract(shooter.getLookAngle().normalize().scale(TELEPORT_BUFFER_DISTANCE));
            arrowEntity.setPos(teleportPos.x, teleportPos.y, teleportPos.z);
        }
    }

}

