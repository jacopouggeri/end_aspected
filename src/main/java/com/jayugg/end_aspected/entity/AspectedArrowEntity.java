package com.jayugg.end_aspected.entity;

import com.jayugg.end_aspected.item.AspectedArrowItem;
import com.jayugg.end_aspected.item.ModItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;

public class AspectedArrowEntity extends AbstractArrowEntity {

    public boolean teleportedFlag = false;

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
        this.teleportedFlag = true;
    }

    @Override
    protected @Nonnull ItemStack getArrowStack() {
        return new ItemStack(ModItems.ASPECTED_ARROW.get());
    }

    @Override
    protected void onImpact(@Nonnull RayTraceResult result) {
        super.onImpact(result);
        //System.out.println("IMPACT!");
        World world = this.world;
        if (this.getShooter() != null && teleportedFlag) {
            //System.out.println("SHOOTER: " + this.getShooter());
            Vector3d startVec = this.getShooter().getPositionVec();
            Vector3d hitVec = result.getHitVec();

            // Vectors to find where to spawn particles at the start
            Vector3d lookVec = this.getShooter().getLookVec().normalize();
            Vector3d particleVec = startVec.add(lookVec.scale(0.25));

            // Spawn particles where arrow reappears
            hitVec = hitVec.subtract(lookVec.scale(AspectedArrowItem.TELEPORT_BUFFER_DISTANCE));

            BlockPos startPos = new BlockPos(startVec.x, startVec.y, startVec.z);
            BlockPos destPos = new BlockPos(hitVec.x, hitVec.y, hitVec.z);

            // Spawn particles along the ray
            world.playSound(null, startPos, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0f, 1.0f);
            world.playSound(null, destPos, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0f, 1.0f);

            // Spawn the Enderman particle effect at the destination position
            ((ServerWorld) world).spawnParticle(ParticleTypes.PORTAL, hitVec.x, hitVec.y, hitVec.z, 50, 0.5, 0.5, 0.5, 0.0);
            ((ServerWorld) world).spawnParticle(ParticleTypes.PORTAL, particleVec.x, particleVec.y, particleVec.z, 50, 0.5, 0.5, 0.5, 0.0);
        }
    }


    @Override
    public @Nonnull IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public void setTeleportedFlag(boolean teleportedFlag) {
        this.teleportedFlag = teleportedFlag;
    }
}
