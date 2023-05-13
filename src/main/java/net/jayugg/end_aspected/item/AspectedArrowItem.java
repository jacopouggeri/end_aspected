package net.jayugg.end_aspected.item;

import net.jayugg.end_aspected.entity.AspectedArrowEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

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
    public @Nonnull AspectedArrowEntity createArrow(@Nonnull World world, @Nonnull ItemStack stack, @Nonnull LivingEntity shooter) {

        Vector3d shooterPos = shooter.getEyePosition(1.0f);
        Vector3d lookVec = shooter.getLookVec();
        Vector3d endVec = shooterPos.add(lookVec.normalize().scale(getMaxTeleportDistance(shooter)));

        AspectedArrowEntity arrowEntity = new AspectedArrowEntity(shooter.world, shooter);
        // Set the arrow's position, motion, and shooter
        arrowEntity.setShooter(shooter);
        arrowEntity.setPosition(shooterPos.x, shooterPos.y, shooterPos.z);
        arrowEntity.setMotion(shooter.getLookVec().scale(1.0));

        // Use the AxisAlignedBB to include entities in the ray tracing
        AxisAlignedBB aabb = shooter.getBoundingBox().expand(lookVec.scale(getMaxTeleportDistance(shooter)));
        EntityRayTraceResult entityResult = ProjectileHelper.rayTraceEntities(world, shooter, shooterPos, endVec, aabb, (target) -> !target.isSpectator() && target.canBeCollidedWith());

        if (entityResult != null) {
            handleTeleportHit(entityResult, shooter, arrowEntity);
            arrowEntity.setTeleportedFlag(true);
        } else {
            RayTraceResult rayTraceResult = world.rayTraceBlocks(new RayTraceContext(shooterPos, endVec, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, shooter));

            if (rayTraceResult.getType() != RayTraceResult.Type.MISS) {
                handleTeleportHit(rayTraceResult, shooter, arrowEntity);
                arrowEntity.setTeleportedFlag(true);
            }
        }

        // Set other arrow parameters
        arrowEntity.setNoGravity(true);

        // Return the arrow entity
        return arrowEntity;
    }

    private void handleTeleportHit(RayTraceResult hitResult, LivingEntity shooter, AspectedArrowEntity arrowEntity) {
        Vector3d hitVec = hitResult.getHitVec();
        Vector3d shooterPos = shooter.getEyePosition(1.0f);
        Vector3d teleportPos = arrowEntity.getPositionVec();

        if (hitResult.getType() == RayTraceResult.Type.ENTITY || hitResult.getType() == RayTraceResult.Type.BLOCK) {
            Vector3d distanceVec = hitVec.subtract(shooterPos);
            // Check if the arrow has traveled far enough before teleporting it
            if (distanceVec.lengthSquared() > Math.pow(TELEPORT_BUFFER_DISTANCE + 0.5, 2)) {
                // Handle entity or block hit
                teleportPos = hitVec.subtract(shooter.getLookVec().normalize().scale(TELEPORT_BUFFER_DISTANCE));
            }

        }
        if (hitResult.getType() == RayTraceResult.Type.ENTITY) {
            // Add an offset for the height of the arrow
            teleportPos = teleportPos.add(0, shooter.getEyeHeight(), 0);
        }

        arrowEntity.setPosition(teleportPos.x, teleportPos.y, teleportPos.z);
    }

}
