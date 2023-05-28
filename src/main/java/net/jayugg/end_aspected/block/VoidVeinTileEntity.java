package net.jayugg.end_aspected.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.ticks.ScheduledTick;
import net.minecraft.world.ticks.TickPriority;

import javax.annotation.Nonnull;

public class VoidVeinTileEntity extends BlockEntity {
    private boolean placedByVoidling;
    private int lifetime;
    private final int max_lifetime;

    public VoidVeinTileEntity(BlockPos pos, BlockState state) {
        super(ModTileEntities.VOID_VEIN.get(), pos, state);
        this.lifetime = 0;
        RandomSource random = RandomSource.create();
        this.max_lifetime = Mth.nextInt(random, 50, 200);
    }

    @Override
    public void saveAdditional(@Nonnull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putBoolean("PlacedByVoidling", this.placedByVoidling);
        nbt.putInt("Lifetime", this.lifetime);
    }

    @Override
    public void load(@Nonnull CompoundTag nbt) {
        super.load(nbt);
        this.placedByVoidling = nbt.getBoolean("PlacedByVoidling");
        this.lifetime = nbt.getInt("Lifetime");
    }

    public boolean isPlacedByVoidling() {
        return this.placedByVoidling;
    }

    public void setPlacedByVoidling(boolean placedByVoidling) {
        this.placedByVoidling = placedByVoidling;
    }

    public void increaseLifetime() {
        this.lifetime++;
    }

    public boolean shouldDestroy() {
        return this.lifetime >= this.max_lifetime;
    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, T be) {
        VoidVeinTileEntity tile = (VoidVeinTileEntity) be;
        if (!level.isClientSide()) {
            if (level.random.nextFloat() > 0.98) {
                ((ServerLevel) level).sendParticles(ParticleTypes.WARPED_SPORE, pos.getX(), pos.getY(), pos.getZ(), 4, 0, 0, 0, 0.1);
            }
            if (tile.isPlacedByVoidling()) {
                tile.increaseLifetime();
                // Tick every 10 seconds
                ScheduledTick<Block> scheduledTick = new ScheduledTick<>(state.getBlock(), pos, 200, TickPriority.LOW, 0L);
                level.getBlockTicks().schedule(scheduledTick);
                if (tile.shouldDestroy()) {
                    level.destroyBlock(pos, false);
                }
            }

            BlockPos blockBelowPos = pos.below();
            BlockState blockBelowState = level.getBlockState(blockBelowPos);

            // if the block below is not solid
            if (!blockBelowState.canOcclude()) {
                // break this block
                level.destroyBlock(pos, true);
            }
        }
    }
}
