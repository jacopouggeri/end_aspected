package net.jayugg.end_aspected.block.tile;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nonnull;
import java.util.Random;

public class VoidTreeTileEntity extends TileEntity {
    private int hunger;
    private final int max_hunger;
    private boolean grown;

    public VoidTreeTileEntity() {
        super(ModTileEntities.VOID_TREE.get());
        Random random = new Random();
        this.hunger = MathHelper.nextInt(random, 5, 30);
        this.max_hunger = this.hunger;
        this.grown = true;
    }

    @Override
    public void read(@Nonnull BlockState state, @Nonnull CompoundNBT nbt) {
        super.read(state, nbt);
        this.hunger = nbt.getInt("Hunger");
        this.grown = nbt.getBoolean("Grown");
    }

    @Override
    public @Nonnull CompoundNBT write(@Nonnull CompoundNBT nbt) {
        super.write(nbt);
        nbt.putInt("Hunger", this.hunger);
        nbt.putBoolean("Grown", this.grown);
        return nbt;
    }

    public void increaseHunger() {
        this.hunger = Math.min(this.max_hunger, this.hunger + 1);
    }

    public void reduceHunger(int amount) {
        this.hunger = Math.max(this.hunger - amount, 0);
    }

    public boolean isHungry() {
        return this.hunger > 0;
    }

    public void setGrown(boolean grown) {
        this.grown = grown;
    }

    public boolean isGrown() {
        return this.grown;
    }
}