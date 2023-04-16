package com.jayugg.end_aspected.item;

import com.jayugg.end_aspected.entity.AspectedArrowEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Objects;

public class AspectedArrowItem extends ArrowItem {
    public AspectedArrowItem(Properties properties) {
        super(properties);
    }

    private int getMaxTeleportDistance(LivingEntity shooter) {
        if (shooter instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) shooter;
            int renderDistance = Objects.requireNonNull(player.getEntityWorld().getServer()).getPlayerList().getViewDistance();
            return renderDistance * 16;
        } else {
            return 100;
        }
    }

    private void spawnParticlesAlongRay(World world, Vector3d startPos, Vector3d endPos, IParticleData particleType) {
        // Assuming 'world' is a reference to the world object
        // and 'startPos' and 'endPos' are the start and end positions of the ray
        // and 'particleType' is the type of particle to spawn

        // Calculate the distance between the start and end positions
        double distance = startPos.distanceTo(endPos);

        // Calculate the direction of the ray
        Vector3d direction = endPos.subtract(startPos).normalize();

        // Calculate the number of particles to spawn
        int numParticles = (int) Math.ceil(distance / 0.25); // Spawn a particle every 0.25 blocks

        // Calculate the position increment for each particle
        Vector3d posIncrement = direction.scale(distance / numParticles);

        // Spawn particles along the ray
        for (int i = 0; i < numParticles; i++) {
            Vector3d particlePos = startPos.add(posIncrement.scale(i));
            world.addParticle(particleType, particlePos.getX(), particlePos.getY(), particlePos.getZ(), 0, 0, 0);
        }
    }

    @Override
    public @Nonnull AspectedArrowEntity createArrow(World world, @Nonnull ItemStack stack, @Nonnull LivingEntity shooter) {
        // Create a new instance of your custom arrow entity
        AspectedArrowEntity arrowEntity = new AspectedArrowEntity(shooter.world, shooter);

        Vector3d shooterPos = shooter.getEyePosition(1.0f);
        Vector3d lookVec = shooter.getLookVec();
        Vector3d endVec = shooterPos.add(lookVec.normalize().scale(getMaxTeleportDistance(shooter)));

        // Set the arrow's position, motion, and shooter
        arrowEntity.setShooter(shooter);
        arrowEntity.setPosition(shooterPos.x, shooterPos.y, shooterPos.z);
        arrowEntity.setMotion(shooter.getLookVec().scale(3.0));

        // Raytrace until the arrow hits an entity or solid block
        RayTraceResult rayTraceResult = world.rayTraceBlocks(new RayTraceContext(shooterPos, endVec, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, shooter));

        // If the raytrace hit something, spawn the arrow teleportDist blocks before it in the direction the player is looking
        double teleportDist = 8;
        Vector3d hitVec = rayTraceResult.getHitVec();
        Vector3d hitDist = hitVec.subtract(shooterPos);

        if ((hitDist.lengthSquared() > teleportDist) && (rayTraceResult.getType() != RayTraceResult.Type.MISS)) {
            Vector3d teleportPos = hitVec.subtract( shooter.getLookVec().normalize().scale(teleportDist) );
            arrowEntity.setPosition(teleportPos.x, teleportPos.y, teleportPos.z);
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

}
