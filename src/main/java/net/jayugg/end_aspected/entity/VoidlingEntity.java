package net.jayugg.end_aspected.entity;

import net.jayugg.end_aspected.effect.ModEffects;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class VoidlingEntity extends MonsterEntity {
    private int lifetime;
    public VoidlingEntity(EntityType<? extends VoidlingEntity> type, World worldIn) {
        super(type, worldIn);
        this.experienceValue = 6;
    }

    public static AttributeModifierMap.MutableAttribute setCustomAttributes() {
        return MobEntity.func_233666_p_()
                .createMutableAttribute(Attributes.MAX_HEALTH, 20.0D)
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.7D)
                .createMutableAttribute(Attributes.ATTACK_DAMAGE, 2.5D)
                .createMutableAttribute(Attributes.FOLLOW_RANGE, 50.0D)
                .createMutableAttribute(Attributes.ATTACK_KNOCKBACK, 1.0D)
                .createMutableAttribute(Attributes.KNOCKBACK_RESISTANCE, 3.0D);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new SwimGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.addGoal(9, new LookRandomlyGoal(this));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomWalkingGoal(this, 1.0D));

        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setCallsForHelp(VoidlingEntity.class));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, EndermanEntity.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
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
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readAdditional(@Nonnull CompoundNBT compound) {
        super.readAdditional(compound);
        this.lifetime = compound.getInt("Lifetime");
    }

    public void writeAdditional(@Nonnull CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putInt("Lifetime", this.lifetime);
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

    @Override
    protected int getExperiencePoints(@Nonnull PlayerEntity player)
    {
        return 3 + this.world.rand.nextInt(5);
    }

    @Override
    public boolean attackEntityAsMob(@Nonnull Entity entityIn) {
        if (!super.attackEntityAsMob(entityIn)) {
            return false;
        } else {
            if (entityIn instanceof EndermanEntity || entityIn instanceof PlayerEntity) {
                ((LivingEntity) entityIn).addPotionEffect(new EffectInstance(ModEffects.UNSTABLE_PHASE.get(), 200,3));
            }
            return true;
        }
    }

    @Override
    public void livingTick() {
        super.livingTick();
        if (this.world.isRemote) {
            for(int i = 0; i < 2; ++i) {
                this.world.addParticle(ParticleTypes.WARPED_SPORE, this.getPosXRandom(0.5D), this.getPosYRandom(), this.getPosZRandom(0.5D), (this.rand.nextDouble() - 0.5D) * 2.0D, -this.rand.nextDouble(), (this.rand.nextDouble() - 0.5D) * 2.0D);
            }
        } else {
            if (!this.isNoDespawnRequired()) {
                ++this.lifetime;
            }

            if (this.lifetime >= 2400) {
                for(int i = 0; i < 2; ++i) {
                    this.world.addParticle(ParticleTypes.PORTAL, this.getPosXRandom(0.5D), this.getPosYRandom(), this.getPosZRandom(0.5D), (this.rand.nextDouble() - 0.5D) * 2.0D, -this.rand.nextDouble(), (this.rand.nextDouble() - 0.5D) * 2.0D);
                }
                this.remove();
            }
        }

    }
}
