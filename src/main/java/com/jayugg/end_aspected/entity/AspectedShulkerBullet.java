package com.jayugg.end_aspected.entity;


import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class AspectedShulkerBullet extends ShulkerBullet {
    public AspectedShulkerBullet(EntityType<? extends ShulkerBullet> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public AspectedShulkerBullet(Level pLevel, LivingEntity pShooter) {
        this(EntityType.SHULKER_BULLET, pLevel);
        this.setOwner(pShooter);
        BlockPos blockpos = pShooter.blockPosition();
        double d0 = (double)blockpos.getX() + 0.5D;
        double d1 = (double)blockpos.getY() + 0.5D;
        double d2 = (double)blockpos.getZ() + 0.5D;
        this.moveTo(d0, d1, d2, this.getYRot(), this.getXRot());

        Vec3 look = pShooter.getLookAngle();

        this.shootFromRotation(pShooter, (float) look.x, (float) look.y, (float) look.z, 1.0f, 1);
    }

}
