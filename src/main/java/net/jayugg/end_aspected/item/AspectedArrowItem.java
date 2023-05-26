package net.jayugg.end_aspected.item;

import net.jayugg.end_aspected.entity.AspectedArrowEntity;
import net.minecraft.entity.Entity;
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

    public AspectedArrowItem(Properties properties) {
        super(properties);
    }

    private int getTeleportRange() {
            return 20 * 16;
    }


    @Override
    public @Nonnull AspectedArrowEntity createArrow(@Nonnull World world, @Nonnull ItemStack stack, @Nonnull LivingEntity shooter) {

        Vector3d shooterPos = shooter.getEyePosition(1.0f);
        Vector3d lookVec = shooter.getLookVec();
        Vector3d endVec = shooterPos.add(lookVec.normalize().scale(getTeleportRange()));

        AspectedArrowEntity arrowEntity = new AspectedArrowEntity(shooter.world, shooter);
        // Set the arrow's position, motion, and shooter
        arrowEntity.setShooter(shooter);
        arrowEntity.setMotion(shooter.getLookVec());

        // Use the AxisAlignedBB to include entities in the ray tracing
        AxisAlignedBB aabb = shooter.getBoundingBox().expand(lookVec.scale(getTeleportRange()));
        EntityRayTraceResult entityResult = ProjectileHelper.rayTraceEntities(world, shooter, shooterPos, endVec, aabb, (target) -> !target.isSpectator() && target.canBeCollidedWith());

        RayTraceResult blockResult = world.rayTraceBlocks(new RayTraceContext(shooterPos, endVec, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, shooter));

        double entityDistance = Integer.MAX_VALUE;
        double blockDistance = Integer.MAX_VALUE;

        if (entityResult != null) {
            entityDistance = entityResult.getHitVec().distanceTo(shooterPos);
        }

        if (blockResult.getType() != RayTraceResult.Type.MISS) {
            blockDistance = blockResult.getHitVec().distanceTo(shooterPos);
        }

        // Teleport the arrow
        double teleportDistance = Math.min(blockDistance, entityDistance);
        teleportDistance = Math.min(0.95 * teleportDistance, teleportDistance - 1.5);
        // Set to 0 if no raytrace hit
        teleportDistance = teleportDistance == Integer.MAX_VALUE ? 0: teleportDistance;
        Vector3d destination = arrowEntity.getPositionVec().add(arrowEntity.getMotion().normalize().scale(teleportDistance));
        arrowEntity.setPosition(destination.getX(), destination.getY(), destination.getZ());

        // Return the arrow entity
        return arrowEntity;
    }

}
