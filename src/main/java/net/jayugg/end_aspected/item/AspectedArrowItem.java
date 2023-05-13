package net.jayugg.end_aspected.item;

import net.jayugg.end_aspected.entity.AspectedArrowEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
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
    public @Nonnull AbstractArrow createArrow(@Nonnull Level world, @Nonnull ItemStack stack, @Nonnull LivingEntity shooter) {
        Vec3 shooterPos = shooter.getEyePosition(1.0f);
        Vec3 lookVec = shooter.getLookAngle();
        Vec3 endVec = shooterPos.add(lookVec.normalize().scale(getMaxTeleportDistance(shooter)));

        AspectedArrowEntity arrowEntity = new AspectedArrowEntity(world, shooter);
        // Set the arrow's position, motion, and shooter
        arrowEntity.setOwner(shooter);
        arrowEntity.setPos(shooterPos.x, shooterPos.y, shooterPos.z);
        arrowEntity.setDeltaMovement(lookVec.scale(1.0));

        // Use the AABB to include entities in the ray tracing
        Vec3 startVec = shooterPos.add(0, shooter.getEyeHeight(), 0);
        double distance = endVec.distanceTo(shooterPos);
        Vec3 direction = endVec.subtract(shooterPos).normalize();
        Vec3 endVecWithDistance = startVec.add(direction.scale(distance));
        EntityHitResult entityResult = ProjectileUtil.getEntityHitResult(world, shooter, startVec, endVecWithDistance, shooter.getBoundingBox().expandTowards(direction), (entity) -> !entity.isSpectator() && entity.canBeCollidedWith());

        if (entityResult != null) {
            handleTeleportHit(entityResult, shooter, arrowEntity);
            arrowEntity.setTeleportedFlag(true);
        } else {
            BlockHitResult rayTraceResult = world.clip(new ClipContext(startVec, endVecWithDistance, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, shooter));

            if (rayTraceResult.getType() != HitResult.Type.MISS) {
                handleTeleportHit(rayTraceResult, shooter, arrowEntity);
                arrowEntity.setTeleportedFlag(true);
            }
        }

        // Set other arrow parameters
        arrowEntity.setNoGravity(true);

        // Return the arrow entity
        return arrowEntity;
    }


    private void handleTeleportHit(HitResult hitResult, LivingEntity shooter, AspectedArrowEntity arrowEntity) {
        Vec3 hitVec = hitResult.getLocation();
        Vec3 shooterPos = shooter.getEyePosition(1.0f);
        Vec3 teleportPos = arrowEntity.getPosition(1.0f);

        if (hitResult.getType() == HitResult.Type.ENTITY || hitResult.getType() == HitResult.Type.BLOCK) {
            Vec3 distanceVec = hitVec.subtract(shooterPos);
            // Check if the arrow has traveled far enough before teleporting it
            if (distanceVec.lengthSqr() > Math.pow(TELEPORT_BUFFER_DISTANCE + 0.5, 2)) {
                // Handle entity or block hit
                teleportPos = hitVec.subtract(shooter.getLookAngle().normalize().scale(TELEPORT_BUFFER_DISTANCE));
            }
        }

        if (hitResult.getType() == HitResult.Type.ENTITY) {
            // Add an offset for the height of the arrow
            teleportPos = teleportPos.add(0, shooter.getEyeHeight(), 0);
        }

        arrowEntity.setPos(teleportPos.x, teleportPos.y, teleportPos.z);
    }

}

