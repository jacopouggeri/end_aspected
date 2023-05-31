package net.jayugg.end_aspected.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class VoidMiteEntity extends VoidlingEntity {
    public VoidMiteEntity(EntityType<? extends VoidMiteEntity> type, World worldIn) {
        super(type, worldIn);
    }

    public static AttributeModifierMap.MutableAttribute setCustomAttributes() {
        return MobEntity.func_233666_p_()
                .createMutableAttribute(Attributes.MAX_HEALTH, 20.0D)
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.55D)
                .createMutableAttribute(Attributes.ATTACK_DAMAGE, 2.5D)
                .createMutableAttribute(Attributes.FOLLOW_RANGE, 50.0D)
                .createMutableAttribute(Attributes.ATTACK_KNOCKBACK, 1.0D)
                .createMutableAttribute(Attributes.KNOCKBACK_RESISTANCE, 3.0D);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new SwimGoal(this));
        this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.addGoal(9, new LookRandomlyGoal(this));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
    }

    protected float getStandingEyeHeight(@Nonnull Pose poseIn, @Nonnull EntitySize sizeIn) {
        return 0.13F;
    }

    protected boolean canTriggerWalking() {
        return false;
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_ENDERMITE_AMBIENT;
    }

    protected SoundEvent getHurtSound(@Nonnull DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_ENDERMITE_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_ENDERMITE_DEATH;
    }

    protected void playStepSound(@Nonnull BlockPos pos, @Nonnull BlockState blockIn) {
        this.playSound(SoundEvents.ENTITY_ENDERMITE_STEP, 0.15F, 1.0F);
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void tick() {
        this.renderYawOffset = this.rotationYaw;
        super.tick();
    }

    /**
     * Set the render yaw offset
     */
    public void setRenderYawOffset(float offset) {
        this.rotationYaw = offset;
        super.setRenderYawOffset(offset);
    }

    /**
     * Returns the Y Offset of this entity.
     */
    public double getYOffset() {
        return 0.1D;
    }

}
