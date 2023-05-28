package net.jayugg.end_aspected.block;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nonnull;

public class VoidVeinTileEntity extends TileEntity {
    private boolean placedByVoidling;
    private int lifetime;

    public VoidVeinTileEntity() {
        super(ModTileEntities.VOID_VEIN.get());
        this.lifetime = 0;
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

    public int getLifetime() {
        return this.lifetime;
    }
}

