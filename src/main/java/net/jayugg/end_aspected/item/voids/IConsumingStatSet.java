package net.jayugg.end_aspected.item.voids;

import net.minecraft.nbt.CompoundNBT;

public interface IConsumingStatSet<I, T extends I> {

    T consume(I toAbsorb);
    T consume(I absorbingTier, double multiplier);
    <I, T extends I> T fromNBT(CompoundNBT tag);
    CompoundNBT toNBT();
    int getEnchantability();

}
