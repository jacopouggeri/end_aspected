package net.jayugg.end_aspected.entity;

import net.jayugg.end_aspected.effect.ModEffects;
import net.jayugg.end_aspected.util.IVoidVeinPlacer;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class VoidlingEntity extends MonsterEntity implements IVoidVeinPlacer {
    private int lifetime;
    public VoidlingEntity(EntityType<? extends VoidlingEntity> type, World worldIn) {
        super(type, worldIn);
        this.experienceValue = 6;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));

        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setCallsForHelp(VoidlingEntity.class));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, EndermanEntity.class, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false,
                (livingEntity) -> !(livingEntity instanceof VoidlingEntity)));
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        if (source == DamageSource.OUT_OF_WORLD) {
            return true;
        } else {
            return super.isInvulnerableTo(source);
        }
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
        if (this.world.isRemote()) {
            for(int i = 0; i < 2; ++i) {
                this.world.addParticle(ParticleTypes.WARPED_SPORE, this.getPosXRandom(0.5D), this.getPosYRandom(), this.getPosZRandom(0.5D), (this.rand.nextDouble() - 0.5D) * 2.0D, -this.rand.nextDouble(), (this.rand.nextDouble() - 0.5D) * 2.0D);
            }
        } else {
            if (this.isOnGround() && ForgeEventFactory.getMobGriefingEvent(this.world, this)) { // Check if entity is standing on a solid block
                placeVoidVeinBlock((ServerWorld) this.world);
            }

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

    private void placeVoidVeinBlock(ServerWorld serverWorld) {
        for(int l = 0; l < 4; ++l) {
            int i = MathHelper.floor(this.getPosX() + (double)((float)(l % 2 * 2 - 1) * 0.25F));
            int j = MathHelper.floor(this.getPosY());
            int k = MathHelper.floor(this.getPosZ() + (double)((float)(l / 2 % 2 - 1) * 0.25F));
            BlockPos blockpos = new BlockPos(i, j, k);
            placeVeinAtPosition(serverWorld, blockpos);
        }
    }


    @Override
    public boolean isPotionApplicable(EffectInstance effect) {
        if (effect.getPotion() == ModEffects.VOIDRUE.get()) {
            return false;
        }
        return super.isPotionApplicable(effect);
    }
}

