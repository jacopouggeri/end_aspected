package net.jayugg.end_aspected.item;

import net.jayugg.end_aspected.entity.AspectedArrowEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;

import static net.jayugg.end_aspected.EndAspected.LOGGER;

import javax.annotation.Nonnull;

public class AspectedArrowItem extends ArrowItem {

    public AspectedArrowItem(Properties properties) {
        super(properties);
    }

    private int getTeleportRange() {
        return 20 * 16;
    }

    @Override
    public @Nonnull AspectedArrowEntity createArrow(@Nonnull Level level, @Nonnull ItemStack stack, @Nonnull LivingEntity shooter) {
        Vec3 shooterPos = shooter.getEyePosition(1.0f);
        Vec3 lookVec = shooter.getLookAngle();
        Vec3 endVec = shooterPos.add(lookVec.normalize().scale(getTeleportRange()));

        AspectedArrowEntity arrowEntity = new AspectedArrowEntity(level, shooter);
        arrowEntity.setOwner(shooter);
        arrowEntity.setDeltaMovement(lookVec);

        BlockHitResult blockResult = level.clip(new ClipContext(shooterPos, endVec, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, shooter));
        double blockDistance = blockResult.getType() != BlockHitResult.Type.MISS ? blockResult.getLocation().distanceTo(shooterPos) : Integer.MAX_VALUE;

        Vec3 rayVec = arrowEntity.getDeltaMovement().normalize().scale(getTeleportRange());
        EntityHitResult entityResult = ProjectileUtil.getEntityHitResult(level, arrowEntity, shooterPos, endVec,
                shooter.getBoundingBox().expandTowards(rayVec).inflate(2.0D),
                (target) -> !target.isSpectator());
        double entityDistance = Integer.MAX_VALUE;
        if (entityResult != null) {
            Entity hitEntity = entityResult.getEntity();
            entityDistance = entityResult.getLocation().distanceTo(shooterPos) - hitEntity.getBoundingBox().getSize();
            LOGGER.info("Distance to entity: " + entityDistance);
        }
        double teleportDistance = Math.min(blockDistance, entityDistance);
        teleportDistance = Math.min(0.95 * teleportDistance, teleportDistance - 1.0);
        teleportDistance = teleportDistance == Integer.MAX_VALUE ? 0 : teleportDistance;
        Vec3 destination = arrowEntity.position().add(arrowEntity.getDeltaMovement().normalize().scale(teleportDistance));
        arrowEntity.setPos(destination.x, destination.y, destination.z);

        return arrowEntity;
    }

}

