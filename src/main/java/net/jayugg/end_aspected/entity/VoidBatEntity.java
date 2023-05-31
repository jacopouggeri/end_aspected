package net.jayugg.end_aspected.entity;


import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class VoidBatEntity extends VoidlingEntity {
    private static final DataParameter<Byte> HANGING;
    private static final EntityPredicate field_213813_c;
    private BlockPos spawnPosition;

    public VoidBatEntity(EntityType<? extends VoidBatEntity> type, World worldIn) {
        super(type, worldIn);
        this.setIsBatHanging(true);
    }

    public static AttributeModifierMap.MutableAttribute setCustomAttributes() {
        return MobEntity.func_233666_p_()
                .createMutableAttribute(Attributes.MAX_HEALTH, 8.0D)
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.2D)
                .createMutableAttribute(Attributes.ATTACK_DAMAGE, 2.5D)
                .createMutableAttribute(Attributes.FOLLOW_RANGE, 50.0D)
                .createMutableAttribute(Attributes.ATTACK_KNOCKBACK, 1.0D)
                .createMutableAttribute(Attributes.KNOCKBACK_RESISTANCE, 3.0D);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));

        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setCallsForHelp(VoidMiteEntity.class));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, EndermanEntity.class, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false,
                (livingEntity) -> !(livingEntity instanceof VoidMiteEntity)));
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(HANGING, (byte)0);
    }

    @Override
    protected float getSoundVolume() {
        return 0.1F;
    }

    @Override
    protected float getSoundPitch() {
        return super.getSoundPitch() * 0.95F;
    }

    @Nullable
    @Override
    public SoundEvent getAmbientSound() {
        return this.getIsBatHanging() && this.rand.nextInt(4) != 0 ? null : SoundEvents.ENTITY_BAT_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_BAT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_BAT_DEATH;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }
    @Override
    protected void collideWithEntity(Entity entityIn) {
    }

    @Override
    protected void collideWithNearbyEntities() {
    }

    public boolean getIsBatHanging() {
        return (this.dataManager.get(HANGING) & 1) != 0;
    }

    public void setIsBatHanging(boolean isHanging) {
        byte b0 = this.dataManager.get(HANGING);
        if (isHanging) {
            this.dataManager.set(HANGING, (byte)(b0 | 1));
        } else {
            this.dataManager.set(HANGING, (byte)(b0 & -2));
        }

    }

    @Override
    public void tick() {
        super.tick();
        if (this.getIsBatHanging()) {
            this.setMotion(Vector3d.ZERO);
            this.setRawPosition(this.getPosX(), (double) MathHelper.floor(this.getPosY()) + 1.0 - (double)this.getHeight(), this.getPosZ());
        } else {
            this.setMotion(this.getMotion().mul(1.0, 0.6, 1.0));
        }

    }

    @Override
    protected void updateAITasks() {
        super.updateAITasks();
        BlockPos blockpos = this.getPosition();
        BlockPos blockpos1 = blockpos.up();
        if (this.getIsBatHanging()) {
            boolean flag = this.isSilent();
            if (this.world.getBlockState(blockpos1).isNormalCube(this.world, blockpos)) {
                if (this.rand.nextInt(200) == 0) {
                    this.rotationYawHead = (float)this.rand.nextInt(360);
                }

                if (this.world.getClosestPlayer(field_213813_c, this) != null) {
                    this.setIsBatHanging(false);
                    if (!flag) {
                        this.world.playEvent(null, 1025, blockpos, 0);
                    }
                }
            } else {
                this.setIsBatHanging(false);
                if (!flag) {
                    this.world.playEvent(null, 1025, blockpos, 0);
                }
            }
        } else {
            if (this.spawnPosition != null && (!this.world.isAirBlock(this.spawnPosition) || this.spawnPosition.getY() < 1)) {
                this.spawnPosition = null;
            }

            if (this.spawnPosition == null || this.rand.nextInt(30) == 0 || this.spawnPosition.withinDistance(this.getPositionVec(), 2.0)) {
                this.spawnPosition = new BlockPos(this.getPosX() + (double)this.rand.nextInt(7) - (double)this.rand.nextInt(7), this.getPosY() + (double)this.rand.nextInt(6) - 2.0, this.getPosZ() + (double)this.rand.nextInt(7) - (double)this.rand.nextInt(7));
            }

            double d2 = (double)this.spawnPosition.getX() + 0.5 - this.getPosX();
            double d0 = (double)this.spawnPosition.getY() + 0.1 - this.getPosY();
            double d1 = (double)this.spawnPosition.getZ() + 0.5 - this.getPosZ();
            Vector3d vector3d = this.getMotion();
            Vector3d vector3d1 = vector3d.add((Math.signum(d2) * 0.5 - vector3d.x) * 0.10000000149011612, (Math.signum(d0) * 0.699999988079071 - vector3d.y) * 0.10000000149011612, (Math.signum(d1) * 0.5 - vector3d.z) * 0.10000000149011612);
            this.setMotion(vector3d1);
            float f = (float)(MathHelper.atan2(vector3d1.z, vector3d1.x) * 57.2957763671875) - 90.0F;
            float f1 = MathHelper.wrapDegrees(f - this.rotationYaw);
            this.moveForward = 0.5F;
            this.rotationYaw += f1;
            if (this.rand.nextInt(100) == 0 && this.world.getBlockState(blockpos1).isNormalCube(this.world, blockpos1)) {
                this.setIsBatHanging(true);
            }
        }

    }

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    @Override
    public boolean onLivingFall(float distance, float damageMultiplier) {
        return false;
    }

    @Override
    protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    @Override
    public boolean doesEntityNotTriggerPressurePlate() {
        return true;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            if (!this.world.isRemote && this.getIsBatHanging()) {
                this.setIsBatHanging(false);
            }

            return super.attackEntityFrom(source, amount);
        }
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.dataManager.set(HANGING, compound.getByte("BatFlags"));
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putByte("BatFlags", this.dataManager.get(HANGING));
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
        return sizeIn.height / 2.0F;
    }

    static {
        HANGING = EntityDataManager.createKey(VoidBatEntity.class, DataSerializers.BYTE);
        field_213813_c = (new EntityPredicate()).setDistance(4.0).allowFriendlyFire();
    }
}

