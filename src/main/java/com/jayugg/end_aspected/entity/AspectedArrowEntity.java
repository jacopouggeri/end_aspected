package com.jayugg.end_aspected.entity;

import com.jayugg.end_aspected.item.ModItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Objects;

public class AspectedArrowEntity extends AbstractArrowEntity {

    public AspectedArrowEntity(EntityType<AspectedArrowEntity> entityType, World world) {
        super(entityType, world);
    }

    public AspectedArrowEntity(EntityType<AspectedArrowEntity> entityType, double x, double y, double z, World world) {
        super(entityType, x, y, z, world);
    }

    public AspectedArrowEntity(EntityType<AspectedArrowEntity> entityType, LivingEntity shooter, World world) {
        super(entityType, shooter, world);
    }

    public AspectedArrowEntity(World worldIn, LivingEntity shooter) {
        super(ModEntities.ASPECTED_ARROW.get(), shooter, worldIn);
    }

    @Override
    protected @Nonnull ItemStack getArrowStack() {
        return new ItemStack(ModItems.ASPECTED_ARROW.get());
    }

    @Override
    protected void onImpact(@Nonnull RayTraceResult result) {
        super.onImpact(result);
        World world = this.world;
        Vector3d startPos = Objects.requireNonNull(this.getShooter()).getPositionVec();
        // Spawn particles along the ray
        Vector3d hitVec = result.getHitVec();
        spawnParticlesAlongRay(world, hitVec, startPos, 0.5);
        }

    private void spawnParticlesAlongRay(World world, Vector3d startPos, Vector3d endPos, double distanceBetweenParticles) {
        // Assuming 'world' is a reference to the world object
        // and 'startPos' and 'endPos' are the start and end positions of the ray
        // and 'particleType' is the type of particle to spawn

        // Calculate the distance between the start and end positions
        double distance = startPos.distanceTo(endPos);

        // Calculate the direction of the ray
        Vector3d direction = endPos.subtract(startPos).normalize();

        // Calculate the number of particles to spawn
        int numParticles = (int) Math.ceil(distance / distanceBetweenParticles); // Spawn a particle every 0.25 blocks

        // Calculate the position increment for each particle
        Vector3d posIncrement = direction.scale(distance / numParticles);

        // Spawn particles along the ray
        for (int i = 0; i < numParticles; i++) {
            Vector3d particlePos = startPos.add(posIncrement.scale(i));
            world.addParticle(ParticleTypes.PORTAL, particlePos.getX(), particlePos.getY(), particlePos.getZ(), 0, 0, 0);
        }
    }

}
