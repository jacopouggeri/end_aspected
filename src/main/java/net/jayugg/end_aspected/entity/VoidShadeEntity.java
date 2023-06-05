package net.jayugg.end_aspected.entity;


import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.BodyController;
import net.minecraft.entity.ai.controller.LookController;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class VoidShadeEntity extends FlyingEntity implements IMob, IVoidMob {
    protected static final DataParameter<Integer> SIZE = EntityDataManager.createKey(VoidShadeEntity.class, DataSerializers.VARINT);
    private static final float SPEED_FACTOR = 0.3f;
    private Vector3d orbitOffset = Vector3d.ZERO;
    private BlockPos orbitPosition = BlockPos.ZERO;
    private VoidShadeEntity.AttackPhase attackPhase = VoidShadeEntity.AttackPhase.CIRCLE;

    public VoidShadeEntity(EntityType<? extends VoidShadeEntity> type, World worldIn) {
        super(type, worldIn);
        this.experienceValue = 5;
        this.moveController = new VoidShadeEntity.MoveHelperController(this);
        this.lookController = new LookHelperController(this);
    }

    public static AttributeModifierMap.MutableAttribute setCustomAttributes() {
        return MobEntity.func_233666_p_()
                .createMutableAttribute(Attributes.MAX_HEALTH, 40.0D)
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.5D)
                .createMutableAttribute(Attributes.ATTACK_DAMAGE, 5.0D)
                .createMutableAttribute(Attributes.FOLLOW_RANGE, 50.0D)
                .createMutableAttribute(Attributes.ATTACK_KNOCKBACK, 0.0D)
                .createMutableAttribute(Attributes.KNOCKBACK_RESISTANCE, 3.0D);
    }

    protected BodyController createBodyController() {
        return new VoidShadeEntity.BodyHelperController(this);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new VoidShadeEntity.PickAttackGoal());
        this.goalSelector.addGoal(2, new VoidShadeEntity.SweepAttackGoal());
        this.goalSelector.addGoal(3, new VoidShadeEntity.OrbitPointGoal());
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, EndermanEntity.class, false));
        this.targetSelector.addGoal(2, new VoidShadeEntity.AttackPlayerGoal());
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false,
                (livingEntity) -> !(livingEntity instanceof IVoidMob)));
    }

    protected void registerData() {
        super.registerData();
        this.dataManager.register(SIZE, 0);
    }

    public void setPhantomSize(int sizeIn) {
        this.dataManager.set(SIZE, MathHelper.clamp(sizeIn, 0, 64));
    }

    private void updatePhantomSize() {
        this.recalculateSize();
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(6 + this.getPhantomSize());
    }

    public int getPhantomSize() {
        return this.dataManager.get(SIZE);
    }

    protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
        return sizeIn.height * 0.35F;
    }

    public void notifyDataManagerChange(DataParameter<?> key) {
        if (SIZE.equals(key)) {
            this.updatePhantomSize();
        }

        super.notifyDataManagerChange(key);
    }

    protected boolean isDespawnPeaceful() {
        return true;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void tick() {
        super.tick();
        if (this.world.isRemote) {
            float f = MathHelper.cos((float)(this.getEntityId() * 3 + this.ticksExisted) * 0.13F + (float)Math.PI);
            float f1 = MathHelper.cos((float)(this.getEntityId() * 3 + this.ticksExisted + 1) * 0.13F + (float)Math.PI);
            if (f > 0.0F && f1 <= 0.0F) {
                this.world.playSound(this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_PHANTOM_FLAP, this.getSoundCategory(), 0.95F + this.rand.nextFloat() * 0.05F, 0.95F + this.rand.nextFloat() * 0.05F, false);
            }

            int i = this.getPhantomSize();
            float f2 = MathHelper.cos(this.rotationYaw * ((float)Math.PI / 180F)) * (1.3F + 0.21F * (float)i);
            float f3 = MathHelper.sin(this.rotationYaw * ((float)Math.PI / 180F)) * (1.3F + 0.21F * (float)i);
            float f4 = (0.3F + f * 0.45F) * ((float)i * 0.2F + 1.0F);
            this.world.addParticle(ParticleTypes.WARPED_SPORE, this.getPosX() + (double)f2, this.getPosY() + (double)f4, this.getPosZ() + (double)f3, 0.0D, 0.0D, 0.0D);
            this.world.addParticle(ParticleTypes.WARPED_SPORE, this.getPosX() - (double)f2, this.getPosY() + (double)f4, this.getPosZ() - (double)f3, 0.0D, 0.0D, 0.0D);
        }

    }

    protected void updateAITasks() {
        super.updateAITasks();
    }

    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        this.orbitPosition = this.getPosition().up(5);
        this.setPhantomSize(0);
        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        if (compound.contains("AX")) {
            this.orbitPosition = new BlockPos(compound.getInt("AX"), compound.getInt("AY"), compound.getInt("AZ"));
        }

        this.setPhantomSize(compound.getInt("Size"));
    }

    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putInt("AX", this.orbitPosition.getX());
        compound.putInt("AY", this.orbitPosition.getY());
        compound.putInt("AZ", this.orbitPosition.getZ());
        compound.putInt("Size", this.getPhantomSize());
    }

    /**
     * Checks if the entity is in range to render.
     */
    @OnlyIn(Dist.CLIENT)
    public boolean isInRangeToRenderDist(double distance) {
        return true;
    }

    public SoundCategory getSoundCategory() {
        return SoundCategory.HOSTILE;
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_PHANTOM_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_PHANTOM_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_PHANTOM_DEATH;
    }

    public CreatureAttribute getCreatureAttribute() {
        return CreatureAttribute.UNDEAD;
    }

    /**
     * Returns the volume for the sounds this mob makes.
     */
    protected float getSoundVolume() {
        return 1.0F;
    }

    public boolean canAttack(EntityType<?> typeIn) {
        return true;
    }

    public EntitySize getSize(Pose poseIn) {
        int i = this.getPhantomSize();
        EntitySize entitysize = super.getSize(poseIn);
        float f = (entitysize.width + 0.2F * (float)i) / entitysize.width;
        return entitysize.scale(f);
    }

    enum AttackPhase {
        CIRCLE,
        SWOOP
    }

    class AttackPlayerGoal extends Goal {
        private final EntityPredicate field_220842_b = (new EntityPredicate()).setDistance(64.0D);
        private int tickDelay = 20;

        private AttackPlayerGoal() {
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        public boolean shouldExecute() {
            if (this.tickDelay > 0) {
                --this.tickDelay;
            } else {
                this.tickDelay = 60;
                List<PlayerEntity> list = VoidShadeEntity.this.world.getTargettablePlayersWithinAABB(this.field_220842_b, VoidShadeEntity.this, VoidShadeEntity.this.getBoundingBox().grow(16.0D, 64.0D, 16.0D));
                if (!list.isEmpty()) {
                    list.sort(Comparator.comparing(Entity::getPosY).reversed());

                    for(PlayerEntity playerentity : list) {
                        if (VoidShadeEntity.this.canAttack(playerentity, EntityPredicate.DEFAULT)) {
                            VoidShadeEntity.this.setAttackTarget(playerentity);
                            return true;
                        }
                    }
                }

            }
            return false;
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean shouldContinueExecuting() {
            LivingEntity livingentity = VoidShadeEntity.this.getAttackTarget();
            return livingentity != null && VoidShadeEntity.this.canAttack(livingentity, EntityPredicate.DEFAULT);
        }
    }

    class BodyHelperController extends BodyController {
        public BodyHelperController(MobEntity mob) {
            super(mob);
        }

        /**
         * Update the Head and Body rendenring angles
         */
        public void updateRenderAngles() {
            VoidShadeEntity.this.rotationYawHead = VoidShadeEntity.this.renderYawOffset;
            VoidShadeEntity.this.renderYawOffset = VoidShadeEntity.this.rotationYaw;
        }
    }

    static class LookHelperController extends LookController {
        public LookHelperController(MobEntity entityIn) {
            super(entityIn);
        }

        /**
         * Updates look
         */
        public void tick() {
        }
    }

    abstract class MoveGoal extends Goal {
        public MoveGoal() {
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        protected boolean func_203146_f() {
            return VoidShadeEntity.this.orbitOffset.squareDistanceTo(VoidShadeEntity.this.getPosX(), VoidShadeEntity.this.getPosY(), VoidShadeEntity.this.getPosZ()) < 4.0D;
        }
    }

    class MoveHelperController extends MovementController {
        private float speedFactor = VoidShadeEntity.SPEED_FACTOR;

        public MoveHelperController(MobEntity entityIn) {
            super(entityIn);
        }

        public void tick() {
            if (VoidShadeEntity.this.collidedHorizontally) {
                VoidShadeEntity.this.rotationYaw += 180.0F;
                this.speedFactor = 0.1F;
            }

            float f = (float)(VoidShadeEntity.this.orbitOffset.x - VoidShadeEntity.this.getPosX());
            float f1 = (float)(VoidShadeEntity.this.orbitOffset.y - VoidShadeEntity.this.getPosY());
            float f2 = (float)(VoidShadeEntity.this.orbitOffset.z - VoidShadeEntity.this.getPosZ());
            double d0 = MathHelper.sqrt(f * f + f2 * f2);
            double d1 = 1.0D - (double)MathHelper.abs(f1 * 0.7F) / d0;
            f = (float)((double)f * d1);
            f2 = (float)((double)f2 * d1);
            d0 = MathHelper.sqrt(f * f + f2 * f2);
            double d2 = MathHelper.sqrt(f * f + f2 * f2 + f1 * f1);
            float f3 = VoidShadeEntity.this.rotationYaw;
            float f4 = (float)MathHelper.atan2(f2, f);
            float f5 = MathHelper.wrapDegrees(VoidShadeEntity.this.rotationYaw + 90.0F);
            float f6 = MathHelper.wrapDegrees(f4 * (180F / (float)Math.PI));
            VoidShadeEntity.this.rotationYaw = MathHelper.approachDegrees(f5, f6, 4.0F) - 90.0F;
            VoidShadeEntity.this.renderYawOffset = VoidShadeEntity.this.rotationYaw;
            if (MathHelper.degreesDifferenceAbs(f3, VoidShadeEntity.this.rotationYaw) < 3.0F) {
                this.speedFactor = MathHelper.approach(this.speedFactor, 1.8F, 0.005F * (1.8F / this.speedFactor));
            } else {
                this.speedFactor = MathHelper.approach(this.speedFactor, 0.2F, 0.025F);
            }

            float f7 = (float)(-(MathHelper.atan2(-f1, d0) * (double)(180F / (float)Math.PI)));
            VoidShadeEntity.this.rotationPitch = f7;
            float f8 = VoidShadeEntity.this.rotationYaw + 90.0F;
            double d3 = (double)(this.speedFactor * MathHelper.cos(f8 * ((float)Math.PI / 180F))) * Math.abs((double)f / d2);
            double d4 = (double)(this.speedFactor * MathHelper.sin(f8 * ((float)Math.PI / 180F))) * Math.abs((double)f2 / d2);
            double d5 = (double)(this.speedFactor * MathHelper.sin(f7 * ((float)Math.PI / 180F))) * Math.abs((double)f1 / d2);
            Vector3d vector3d = VoidShadeEntity.this.getMotion();
            VoidShadeEntity.this.setMotion(vector3d.add((new Vector3d(d3, d5, d4)).subtract(vector3d).scale(0.2D)));
        }
    }

    class OrbitPointGoal extends VoidShadeEntity.MoveGoal {
        private float field_203150_c;
        private float field_203151_d;
        private float field_203152_e;
        private float field_203153_f;

        private OrbitPointGoal() {
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        public boolean shouldExecute() {
            return VoidShadeEntity.this.getAttackTarget() == null || VoidShadeEntity.this.attackPhase == VoidShadeEntity.AttackPhase.CIRCLE;
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void startExecuting() {
            this.field_203151_d = 5.0F + VoidShadeEntity.this.rand.nextFloat() * 10.0F;
            this.field_203152_e = -4.0F + VoidShadeEntity.this.rand.nextFloat() * 9.0F;
            this.field_203153_f = VoidShadeEntity.this.rand.nextBoolean() ? 1.0F : -1.0F;
            this.setOrbitPosition();
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        public void tick() {
            if (VoidShadeEntity.this.rand.nextInt(350) == 0) {
                this.field_203152_e = -4.0F + VoidShadeEntity.this.rand.nextFloat() * 9.0F;
            }

            if (VoidShadeEntity.this.rand.nextInt(250) == 0) {
                ++this.field_203151_d;
                if (this.field_203151_d > 15.0F) {
                    this.field_203151_d = 5.0F;
                    this.field_203153_f = -this.field_203153_f;
                }
            }

            if (VoidShadeEntity.this.rand.nextInt(450) == 0) {
                this.field_203150_c = VoidShadeEntity.this.rand.nextFloat() * 2.0F * (float)Math.PI;
                this.setOrbitPosition();
            }

            if (this.func_203146_f()) {
                this.setOrbitPosition();
            }

            if (VoidShadeEntity.this.orbitOffset.y < VoidShadeEntity.this.getPosY() && !VoidShadeEntity.this.world.isAirBlock(VoidShadeEntity.this.getPosition().down(1))) {
                this.field_203152_e = Math.max(1.0F, this.field_203152_e);
                this.setOrbitPosition();
            }

            if (VoidShadeEntity.this.orbitOffset.y > VoidShadeEntity.this.getPosY() && !VoidShadeEntity.this.world.isAirBlock(VoidShadeEntity.this.getPosition().up(1))) {
                this.field_203152_e = Math.min(-1.0F, this.field_203152_e);
                this.setOrbitPosition();
            }

        }

        private void setOrbitPosition() {
            if (BlockPos.ZERO.equals(VoidShadeEntity.this.orbitPosition)) {
                VoidShadeEntity.this.orbitPosition = VoidShadeEntity.this.getPosition();
            }

            this.field_203150_c += this.field_203153_f * 15.0F * ((float)Math.PI / 180F);
            VoidShadeEntity.this.orbitOffset = Vector3d.copy(VoidShadeEntity.this.orbitPosition).add(this.field_203151_d * MathHelper.cos(this.field_203150_c), -4.0F + this.field_203152_e, this.field_203151_d * MathHelper.sin(this.field_203150_c));
        }
    }

    class PickAttackGoal extends Goal {
        private int tickDelay;

        private PickAttackGoal() {
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        public boolean shouldExecute() {
            LivingEntity livingentity = VoidShadeEntity.this.getAttackTarget();
            return livingentity != null && VoidShadeEntity.this.canAttack(VoidShadeEntity.this.getAttackTarget(), EntityPredicate.DEFAULT);
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void startExecuting() {
            this.tickDelay = 10;
            VoidShadeEntity.this.attackPhase = VoidShadeEntity.AttackPhase.CIRCLE;
            this.updateOrbitPosition();
        }

        /**
         * Reset the task's internal state. Called when this task is interrupted by another one
         */
        public void resetTask() {
            VoidShadeEntity.this.orbitPosition = VoidShadeEntity.this.world.getHeight(Heightmap.Type.MOTION_BLOCKING, VoidShadeEntity.this.orbitPosition).up(10 + VoidShadeEntity.this.rand.nextInt(20));
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        public void tick() {
            if (VoidShadeEntity.this.attackPhase == VoidShadeEntity.AttackPhase.CIRCLE) {
                --this.tickDelay;
                if (this.tickDelay <= 0) {
                    VoidShadeEntity.this.attackPhase = VoidShadeEntity.AttackPhase.SWOOP;
                    this.updateOrbitPosition();
                    this.tickDelay = (8 + VoidShadeEntity.this.rand.nextInt(4)) * 20;
                    VoidShadeEntity.this.playSound(SoundEvents.ENTITY_PHANTOM_SWOOP, 10.0F, 0.95F + VoidShadeEntity.this.rand.nextFloat() * 0.1F);
                }
            }

        }

        private void updateOrbitPosition() {
            VoidShadeEntity.this.orbitPosition = VoidShadeEntity.this.getAttackTarget().getPosition().up(20 + VoidShadeEntity.this.rand.nextInt(20));
            if (VoidShadeEntity.this.orbitPosition.getY() < VoidShadeEntity.this.world.getSeaLevel()) {
                VoidShadeEntity.this.orbitPosition = new BlockPos(VoidShadeEntity.this.orbitPosition.getX(), VoidShadeEntity.this.world.getSeaLevel() + 1, VoidShadeEntity.this.orbitPosition.getZ());
            }

        }
    }

    class SweepAttackGoal extends VoidShadeEntity.MoveGoal {
        private SweepAttackGoal() {
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        public boolean shouldExecute() {
            return VoidShadeEntity.this.getAttackTarget() != null && VoidShadeEntity.this.attackPhase == VoidShadeEntity.AttackPhase.SWOOP;
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean shouldContinueExecuting() {
            LivingEntity livingentity = VoidShadeEntity.this.getAttackTarget();
            if (livingentity == null) {
                return false;
            } else if (!livingentity.isAlive()) {
                return false;
            } else if (!(livingentity instanceof PlayerEntity) || !livingentity.isSpectator() && !((PlayerEntity)livingentity).isCreative()) {
                if (!this.shouldExecute()) {
                    return false;
                } else {
                    if (VoidShadeEntity.this.ticksExisted % 20 == 0) {
                        List<CatEntity> list = VoidShadeEntity.this.world.getEntitiesWithinAABB(CatEntity.class, VoidShadeEntity.this.getBoundingBox().grow(16.0D), EntityPredicates.IS_ALIVE);
                        if (!list.isEmpty()) {
                            for(CatEntity catentity : list) {
                                catentity.func_213420_ej();
                            }

                            return false;
                        }
                    }

                    return true;
                }
            } else {
                return false;
            }
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void startExecuting() {
        }

        /**
         * Reset the task's internal state. Called when this task is interrupted by another one
         */
        public void resetTask() {
            VoidShadeEntity.this.setAttackTarget(null);
            VoidShadeEntity.this.attackPhase = VoidShadeEntity.AttackPhase.CIRCLE;
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        public void tick() {
            LivingEntity livingentity = VoidShadeEntity.this.getAttackTarget();
            if (livingentity != null) {
                VoidShadeEntity.this.orbitOffset = new Vector3d(livingentity.getPosX(), livingentity.getPosYHeight(0.5D), livingentity.getPosZ());
                if (VoidShadeEntity.this.getBoundingBox().grow(0.2F).intersects(livingentity.getBoundingBox())) {
                    VoidShadeEntity.this.attackEntityAsMob(livingentity);
                    VoidShadeEntity.this.attackPhase = VoidShadeEntity.AttackPhase.CIRCLE;
                    if (!VoidShadeEntity.this.isSilent()) {
                        VoidShadeEntity.this.world.playEvent(1039, VoidShadeEntity.this.getPosition(), 0);
                    }
                } else if (VoidShadeEntity.this.collidedHorizontally || VoidShadeEntity.this.hurtTime > 0) {
                    VoidShadeEntity.this.attackPhase = VoidShadeEntity.AttackPhase.CIRCLE;
                }
            }
        }
    }
}
