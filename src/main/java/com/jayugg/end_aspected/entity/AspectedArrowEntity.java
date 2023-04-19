package com.jayugg.end_aspected.entity;

import com.jayugg.end_aspected.item.AspectedArrowItem;
import com.jayugg.end_aspected.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nonnull;

public class AspectedArrowEntity extends AbstractArrow {

    public boolean teleportedFlag = false;

    public AspectedArrowEntity(EntityType<AspectedArrowEntity> entityType, Level world) {
        super(entityType, world);
    }

    public AspectedArrowEntity(EntityType<AspectedArrowEntity> entityType, double x, double y, double z, Level world) {
        super(entityType, x, y, z, world);
    }

    public AspectedArrowEntity(EntityType<AspectedArrowEntity> entityType, LivingEntity shooter, Level world) {
        super(entityType, shooter, world);
    }

    public AspectedArrowEntity(Level worldIn, LivingEntity shooter) {
        super(ModEntities.ASPECTED_ARROW.get(), shooter, worldIn);
        this.teleportedFlag = true;
    }

    @Override
    protected @Nonnull ItemStack getPickupItem() {
        return new ItemStack(ModItems.ASPECTED_ARROW.get());
    }

    @Override
    protected void onHit(@Nonnull HitResult result) {
        super.onHit(result);
        //System.out.println("IMPACT!");
        Level world = this.level;
        if (this.getOwner() != null && teleportedFlag) {
            //System.out.println("SHOOTER: " + this.getShooter());
            Vec3 startVec = this.getOwner().getPosition(1.0f);
            Vec3 hitVec = result.getLocation();

            // Vectors to find where to spawn particles at the start
            Vec3 lookVec = this.getOwner().getLookAngle().normalize();
            Vec3 particleVec = startVec.add(lookVec.scale(0.25));

            // Spawn particles where arrow reappears
            hitVec = hitVec.subtract(lookVec.scale(AspectedArrowItem.TELEPORT_BUFFER_DISTANCE));

            BlockPos startPos = new BlockPos(startVec.x, startVec.y, startVec.z);
            BlockPos destPos = new BlockPos(hitVec.x, hitVec.y, hitVec.z);

            // Spawn particles along the ray
            world.playSound(null, startPos, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.0f);
            world.playSound(null, destPos, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.0f);

            // Spawn the Enderman particle effect at the destination position
            ((ServerLevel) world).sendParticles(ParticleTypes.PORTAL, hitVec.x, hitVec.y, hitVec.z, 50, 0.5, 0.5, 0.5, 0.0);
            ((ServerLevel) world).sendParticles(ParticleTypes.PORTAL, particleVec.x, particleVec.y, particleVec.z, 50, 0.5, 0.5, 0.5, 0.0);
        }
    }

    @Override
    public @Nonnull Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public void setTeleportedFlag(boolean teleportedFlag) {
        this.teleportedFlag = teleportedFlag;
    }
}
