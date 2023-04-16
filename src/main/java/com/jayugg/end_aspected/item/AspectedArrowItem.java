package com.jayugg.end_aspected.item;

import com.jayugg.end_aspected.entity.AspectedArrowEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

public class AspectedArrowItem extends ArrowItem {

    public static double TELEPORT_BUFFER_DISTANCE;

    public AspectedArrowItem(Properties properties) {
        super(properties);
        TELEPORT_BUFFER_DISTANCE = 8;
    }

    public static Vector3d raytraceEntities(Vector3d startPos, Vector3d endPos, World world) {
        double closestDistance = Double.POSITIVE_INFINITY;
        Vector3d closestPos = null;

        AxisAlignedBB rayBB = new AxisAlignedBB(startPos, endPos).grow(1.0);
        List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, rayBB);

        for (Entity entity : entities) {
            Optional<Vector3d> hitPosOptional = entity.getBoundingBox().rayTrace(startPos, endPos);
            if (hitPosOptional.isPresent()) {
                Vector3d hitPos = hitPosOptional.get();
                double distance = startPos.distanceTo(hitPos);
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestPos = hitPos;
                }
            }
        }

        return closestPos;
    }

    private int getMaxTeleportDistance(LivingEntity shooter) {
        if (shooter instanceof PlayerEntity) {
            double renderDistanceWeight = Entity.getRenderDistanceWeight();
            int renderDistanceChunks = (int) (renderDistanceWeight * 12.0D); // Convert render distance weight to number of chunks
            return renderDistanceChunks * 16;
        } else {
            return 100;
        }
    }

    @Override
    public @Nonnull AspectedArrowEntity createArrow(World world, @Nonnull ItemStack stack, @Nonnull LivingEntity shooter) {

        Vector3d shooterPos = shooter.getEyePosition(1.0f);
        Vector3d lookVec = shooter.getLookVec();
        Vector3d endVec = shooterPos.add(lookVec.normalize().scale(getMaxTeleportDistance(shooter)));

        // Create a new instance of your custom arrow entity
        AspectedArrowEntity arrowEntity = new AspectedArrowEntity(shooter.world, shooter);
        // Set the arrow's position, motion, and shooter
        arrowEntity.setShooter(shooter);
        arrowEntity.setPosition(shooterPos.x, shooterPos.y, shooterPos.z);
        arrowEntity.setMotion(shooter.getLookVec().scale(3.0));

        RayTraceResult rayTraceResult = world.rayTraceBlocks(new RayTraceContext(shooterPos, endVec, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, shooter));

        if (rayTraceResult.getType() != RayTraceResult.Type.MISS) {
            handleRaytraceHit(rayTraceResult, shooter, arrowEntity);
            arrowEntity.setTeleportedFlag(true);
        }

        // Set other arrow parameters
        arrowEntity.setDamage(2.0);
        arrowEntity.setKnockbackStrength(1);
        arrowEntity.setNoGravity(true);

        // Spawn the arrow entity in the world
        world.addEntity(arrowEntity);

        // Return the arrow entity
        return arrowEntity;
    }

    private void handleRaytraceHit(RayTraceResult rayTraceResult, LivingEntity shooter, AspectedArrowEntity arrowEntity) {
        // If the raytrace hit something, spawn the arrow teleportDist blocks before it in the direction the player is looking
        Vector3d hitVec = rayTraceResult.getHitVec();
        Vector3d hitDist = hitVec.subtract(shooter.getEyePosition(1.0f));


        if ((hitDist.lengthSquared() > TELEPORT_BUFFER_DISTANCE + 0.5) && (rayTraceResult.getType() != RayTraceResult.Type.MISS)) {
            Vector3d teleportPos = hitVec.subtract( shooter.getLookVec().normalize().scale(TELEPORT_BUFFER_DISTANCE) );
            arrowEntity.setPosition(teleportPos.x, teleportPos.y, teleportPos.z);
        }
    }


}
