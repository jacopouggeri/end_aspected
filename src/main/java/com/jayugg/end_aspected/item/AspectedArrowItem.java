package com.jayugg.end_aspected.item;

import com.jayugg.end_aspected.entity.AspectedArrowEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
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
    public @Nonnull AspectedArrowEntity createArrow(World world, @Nonnull ItemStack stack, @Nonnull LivingEntity shooter) {

        Vector3d shooterPos = shooter.getEyePosition(1.0f);
        Vector3d lookVec = shooter.getLookVec();
        Vector3d endVec = shooterPos.add(lookVec.normalize().scale(getMaxTeleportDistance(shooter)));

        // Create a new instance of your custom arrow entity
        AspectedArrowEntity arrowEntity = new AspectedArrowEntity(shooter.world, shooter);
        // Set the arrow's position, motion, and shooter
        arrowEntity.setShooter(shooter);
        arrowEntity.setPosition(shooterPos.x, shooterPos.y, shooterPos.z);
        arrowEntity.setMotion(shooter.getLookVec().scale(1.0));

        RayTraceResult rayTraceResult = world.rayTraceBlocks(new RayTraceContext(shooterPos, endVec, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, shooter));

        if (rayTraceResult.getType() != RayTraceResult.Type.MISS) {
            handleRaytraceHit(rayTraceResult, shooter, arrowEntity);
            arrowEntity.setTeleportedFlag(true);
        }

        // Set other arrow parameters
        arrowEntity.setNoGravity(true);

        // Return the arrow entity
        return arrowEntity;
    }

    private void handleRaytraceHit(RayTraceResult rayTraceResult, LivingEntity shooter, AspectedArrowEntity arrowEntity) {
        // If the raytrace hit something, spawn the arrow teleportDist blocks before it in the direction the player is looking
        Vector3d hitVec = rayTraceResult.getHitVec();
        Vector3d hitDist = hitVec.subtract(shooter.getEyePosition(1.0f));


        if ((hitDist.lengthSquared() > Math.pow(TELEPORT_BUFFER_DISTANCE + 0.5, 2)) && (rayTraceResult.getType() != RayTraceResult.Type.MISS)) {
            Vector3d teleportPos = hitVec.subtract( shooter.getLookVec().normalize().scale(TELEPORT_BUFFER_DISTANCE) );
            arrowEntity.setPosition(teleportPos.x, teleportPos.y, teleportPos.z);
        }
    }


}
