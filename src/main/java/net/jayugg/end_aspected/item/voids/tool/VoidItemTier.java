package net.jayugg.end_aspected.item.voids.tool;


import net.jayugg.end_aspected.block.ModBlocks;
import net.jayugg.end_aspected.item.ModItemTier;
import net.jayugg.end_aspected.item.voids.IConsumingStatSet;
import net.minecraft.item.IItemTier;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.function.Supplier;

@Immutable
public class VoidItemTier implements IItemTier, IConsumingStatSet<IItemTier, VoidItemTier> {
    private int harvestLevel;
    private int maxUses;
    private float efficiency;
    private final float attackDamage;
    private int enchantability;
    private final Supplier<Ingredient> repairMaterial = () -> Ingredient.fromItems(ModBlocks.VOID_STEM.get());

    public VoidItemTier() {
        // Set initial values based on the incoming tier
        this.harvestLevel = ModItemTier.VOID.getHarvestLevel();
        this.maxUses = ModItemTier.VOID.getMaxUses();
        this.efficiency = ModItemTier.VOID.getEfficiency();
        this.attackDamage = ModItemTier.VOID.getAttackDamage();
        this.enchantability = ModItemTier.VOID.getEnchantability();
    }

    public VoidItemTier(int harvestLevel, int maxUses, float efficiency, float attackDamage, int enchantability) {
        this.harvestLevel = harvestLevel;
        this.maxUses = maxUses;
        this.efficiency = efficiency;
        this.attackDamage = attackDamage;
        this.enchantability = enchantability;
    }

    public VoidItemTier consume(IItemTier absorbingTier) {
        this.harvestLevel = Math.max(this.getHarvestLevel(), absorbingTier.getHarvestLevel());
        this.maxUses = this.getMaxUses() + absorbingTier.getMaxUses();
        this.efficiency = this.getEfficiency() + absorbingTier.getEfficiency();
        this.enchantability = Math.max(this.getEnchantability(), absorbingTier.getEnchantability());
        return this;
    }

    public VoidItemTier consume(IItemTier absorbingTier, double multiplier) {
        this.harvestLevel = Math.max(this.getHarvestLevel(), absorbingTier.getHarvestLevel());
        this.maxUses = this.getMaxUses() + (int) (multiplier*absorbingTier.getMaxUses());
        this.efficiency = this.getEfficiency() + (float) (multiplier*absorbingTier.getEfficiency());
        this.enchantability = Math.max(this.getEnchantability(), absorbingTier.getEnchantability());
        return this;
    }

    public static VoidItemTier fromNBT(CompoundNBT tierTag) {
        int harvestLevel = tierTag.getInt("harvestLevel");
        int maxUses = tierTag.getInt("maxUses");
        float efficiency = tierTag.getFloat("efficiency");
        float attackDamage = tierTag.getFloat("attackDamage");
        int enchantability = tierTag.getInt("enchantability");
        return new VoidItemTier(harvestLevel, maxUses, efficiency, attackDamage, enchantability);
    }

    public CompoundNBT toNBT() {
        CompoundNBT tierTag = new CompoundNBT();
        tierTag.putInt("harvestLevel", this.getHarvestLevel());
        tierTag.putInt("maxUses", this.getMaxUses());
        tierTag.putFloat("efficiency", this.getEfficiency());
        tierTag.putFloat("attackDamage", this.getAttackDamage());
        tierTag.putInt("enchantability", this.getEnchantability());
        return tierTag;
    }

    @Override
    public int getMaxUses() {
        return this.maxUses;
    }

    @Override
    public float getEfficiency() {
        return this.efficiency;
    }

    @Override
    public float getAttackDamage() {
        return this.attackDamage;
    }

    @Override
    public int getHarvestLevel() {
        return this.harvestLevel;
    }

    @Override
    public int getEnchantability() {
        return this.enchantability;
    }

    @Nonnull
    @Override
    public Ingredient getRepairMaterial() {
        return this.repairMaterial.get();
    }

    public VoidItemTier addAttackDamage(float attackDamage) {
        return new VoidItemTier(this.harvestLevel, this.maxUses, this.efficiency, this.attackDamage + attackDamage, this.enchantability);
    }

    @Override
    public String toString() {
        return "VoidItemTier{" +
                "harvestLevel=" + harvestLevel +
                ", maxUses=" + maxUses +
                ", efficiency=" + efficiency +
                ", attackDamage=" + attackDamage +
                ", enchantability=" + enchantability +
                ", repairMaterial=" + repairMaterial +
                '}';
    }
}
