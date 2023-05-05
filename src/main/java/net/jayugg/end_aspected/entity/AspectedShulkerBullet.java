package net.jayugg.end_aspected.entity;


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
        Vec3 shooterPos = pShooter.getPosition(1.0f);
        double d0 = shooterPos.x;
        double d1 = shooterPos.y + pShooter.getEyeHeight();
        double d2 = shooterPos.z;
        this.moveTo(d0, d1, d2, this.getYRot(), this.getXRot());

        Vec3 look = pShooter.getLookAngle().normalize();

        this.shoot((float) look.x, (float) look.y, (float) look.z, 1.0f + pShooter.getSpeed(), 1);
    }

}
