package net.jayugg.end_aspected.entity;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.world.BossInfo;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class VoidBeastEntity extends VoidShadeEntity {
    private static final int SIZE_VALUE = 48;
    private final ServerBossInfo bossInfo = new ServerBossInfo(this.getDisplayName(), BossInfo.Color.BLUE, BossInfo.Overlay.PROGRESS);
    public VoidBeastEntity(EntityType<? extends VoidShadeEntity> type, World worldIn) {
        super(type, worldIn);
        this.setPhantomSize();
    }

    public static AttributeModifierMap.MutableAttribute setCustomAttributes() {
        return MobEntity.func_233666_p_()
                .createMutableAttribute(Attributes.MAX_HEALTH, 100.0D)
                .createMutableAttribute(Attributes.ATTACK_DAMAGE, 9.0D)
                .createMutableAttribute(Attributes.FOLLOW_RANGE, 50.0D)
                .createMutableAttribute(Attributes.ATTACK_KNOCKBACK, 5.0D)
                .createMutableAttribute(Attributes.KNOCKBACK_RESISTANCE, 5.0D);
    }

    @Override
    public void setPhantomSize(int sizeIn) {
        this.setPhantomSize();
    }

    public void setPhantomSize() {
        this.dataManager.set(SIZE, SIZE_VALUE);
    }

    @Override
    public int getPhantomSize() {
        return super.getPhantomSize();
    }

    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        this.setPhantomSize();
        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.setPhantomSize();
    }

    @Override
    public void livingTick() {
        super.livingTick();
        this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
    }

    @Override
    public void onDeath(DamageSource cause) {
        super.onDeath(cause);
        this.bossInfo.removeAllPlayers();
    }
    @Override
    public void addTrackingPlayer(ServerPlayerEntity player) {
        super.addTrackingPlayer(player);
        this.bossInfo.addPlayer(player);
    }

    @Override
    public void removeTrackingPlayer(ServerPlayerEntity player) {
        super.removeTrackingPlayer(player);
        this.bossInfo.removePlayer(player);
    }


}
