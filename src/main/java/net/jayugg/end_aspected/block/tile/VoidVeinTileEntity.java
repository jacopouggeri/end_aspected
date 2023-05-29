package net.jayugg.end_aspected.block.tile;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nonnull;
import java.util.Random;

public class VoidVeinTileEntity extends TileEntity {
    private boolean placedByVoidling;
    private int lifetime;
    private final int max_lifetime;

    public VoidVeinTileEntity() {
        super(ModTileEntities.VOID_VEIN.get());
        Random random = new Random();
        this.lifetime = 0;
        this.max_lifetime = MathHelper.nextInt(random, 1, 5);
    }

    @Override
    public void read(@Nonnull BlockState state, @Nonnull CompoundNBT nbt) {
        super.read(state, nbt);
        this.placedByVoidling = nbt.getBoolean("PlacedByVoidling");
        this.lifetime = nbt.getInt("Lifetime");
    }

    @Override
    public @Nonnull CompoundNBT write(@Nonnull CompoundNBT nbt) {
        super.write(nbt);
        nbt.putBoolean("PlacedByVoidling", this.placedByVoidling);
        nbt.putInt("Lifetime", this.lifetime);
        return nbt;
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

    public void resetLifetime() {
        this.lifetime = 0;
    }
}

