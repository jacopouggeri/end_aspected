package net.jayugg.end_aspected.item.voids;

import net.minecraft.nbt.CompoundNBT;

public interface IConsumingStatSet<I, T extends I> {

    T consume(I toAbsorb);
    T consume(I absorbingTier, double multiplier);
    CompoundNBT toNBT();
    int getEnchantability();

}
