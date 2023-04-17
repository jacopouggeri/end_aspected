package com.jayugg.end_aspected.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.world.World;

public class AspectedShulkerBulletEntity extends ShulkerBulletEntity {
    public AspectedShulkerBulletEntity(EntityType<? extends ShulkerBulletEntity> entityType, World world) {
        super(entityType, world);
    }
    public AspectedShulkerBulletEntity(World worldIn, double x, double y, double z, double motionXIn, double motionYIn, double motionZIn) {
        super(EntityType.SHULKER_BULLET, worldIn);
        this.setLocationAndAngles(x, y, z, this.rotationYaw, this.rotationPitch);
        this.setMotion(motionXIn, motionYIn, motionZIn);
    }
}
