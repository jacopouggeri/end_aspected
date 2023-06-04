package net.jayugg.end_aspected.item.voids.armor;

import mcp.MethodsReturnNonnullByDefault;
import net.jayugg.end_aspected.item.ModArmorMaterial;
import net.jayugg.end_aspected.item.voids.IConsumingStatSet;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundEvent;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class VoidArmorMaterial implements IArmorMaterial, IConsumingStatSet<IArmorMaterial, VoidArmorMaterial> {
    private static final int[] MAX_DAMAGE_ARRAY = new int[]{13, 15, 16, 11};
    private final SoundEvent soundEvent = ModArmorMaterial.VOID.getSoundEvent();
    private final Ingredient repairMaterial = ModArmorMaterial.VOID.getRepairMaterial();
    private int maxDamageFactor;
    private int[] damageReductionAmountArray;
    private int enchantability;
    private float toughness;
    private float knockbackResistance;

    public VoidArmorMaterial() {
        this.maxDamageFactor = ModArmorMaterial.VOID.getMaxDamageFactor();
        this.damageReductionAmountArray = ModArmorMaterial.VOID.getDamageReductionAmountArray();
        this.enchantability = ModArmorMaterial.VOID.getEnchantability();
        this.toughness = ModArmorMaterial.VOID.getToughness();
        this.knockbackResistance = ModArmorMaterial.VOID.getKnockbackResistance();
    }

    public VoidArmorMaterial(int maxDamageFactor, int[] damageReductionAmountArray, int enchantability, float toughness, float knockbackResistance) {
        this.maxDamageFactor = maxDamageFactor;
        this.damageReductionAmountArray = damageReductionAmountArray;
        this.enchantability = enchantability;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
    }

    public int getDurability(EquipmentSlotType slotIn) {
        return MAX_DAMAGE_ARRAY[slotIn.getIndex()] * this.maxDamageFactor;
    }
    public int getDamageReductionAmount(EquipmentSlotType slotIn) {
        return this.damageReductionAmountArray[slotIn.getIndex()];
    }
    @Override
    public String getName() {
        return ModArmorMaterial.VOID.getName();
    }

    private int getMaxDamageFactor(IArmorMaterial toAbsorb) {
        return toAbsorb.getDurability(EquipmentSlotType.CHEST) / MAX_DAMAGE_ARRAY[EquipmentSlotType.CHEST.getSlotIndex()] ;
    }

    @Override
    public VoidArmorMaterial consume(IArmorMaterial toAbsorb) {
        this.maxDamageFactor += getMaxDamageFactor(toAbsorb);
        this.damageReductionAmountArray = new int[] {
                this.damageReductionAmountArray[0] + toAbsorb.getDamageReductionAmount(EquipmentSlotType.FEET),
                this.damageReductionAmountArray[1] + toAbsorb.getDamageReductionAmount(EquipmentSlotType.LEGS),
                this.damageReductionAmountArray[2] + toAbsorb.getDamageReductionAmount(EquipmentSlotType.CHEST),
                this.damageReductionAmountArray[3] + toAbsorb.getDamageReductionAmount(EquipmentSlotType.HEAD)
        };
        this.enchantability += toAbsorb.getEnchantability();
        this.toughness += toAbsorb.getToughness();
        this.knockbackResistance += toAbsorb.getKnockbackResistance();
        return this;
    }

    @Override
    public VoidArmorMaterial consume(IArmorMaterial absorbingTier, double multiplier) {
        this.maxDamageFactor += getMaxDamageFactor(absorbingTier) * multiplier;
        this.damageReductionAmountArray = new int[] {
                this.damageReductionAmountArray[0] + (int) (absorbingTier.getDamageReductionAmount(EquipmentSlotType.FEET) * multiplier),
                this.damageReductionAmountArray[1] + (int) (absorbingTier.getDamageReductionAmount(EquipmentSlotType.LEGS) * multiplier),
                this.damageReductionAmountArray[2] + (int) (absorbingTier.getDamageReductionAmount(EquipmentSlotType.CHEST) * multiplier),
                this.damageReductionAmountArray[3] + (int) (absorbingTier.getDamageReductionAmount(EquipmentSlotType.HEAD) * multiplier)
        };
        this.enchantability += (int) (absorbingTier.getEnchantability() * multiplier);
        this.toughness += absorbingTier.getToughness() * multiplier;
        this.knockbackResistance += absorbingTier.getKnockbackResistance() * multiplier;
        return this;
    }

    public static VoidArmorMaterial fromNBT(CompoundNBT tag) {
        return new VoidArmorMaterial(
                tag.getInt("maxDamageFactor"),
                tag.getIntArray("damageReductionAmountArray"),
                tag.getInt("enchantability"),
                tag.getFloat("toughness"),
                tag.getFloat("knockbackResistance")
        );
    }

    @Override
    public CompoundNBT toNBT() {
        CompoundNBT tierTag = new CompoundNBT();
        tierTag.putInt("maxDamageFactor", this.maxDamageFactor);
        tierTag.putIntArray("damageReductionAmountArray", this.damageReductionAmountArray);
        tierTag.putInt("enchantability", this.enchantability);
        tierTag.putFloat("toughness", this.toughness);
        tierTag.putFloat("knockbackResistance", this.knockbackResistance);
        return tierTag;
    }

    public int getEnchantability() {
        return this.enchantability;
    }
    public SoundEvent getSoundEvent() {
        return this.soundEvent;
    }
    public Ingredient getRepairMaterial() {
        return this.repairMaterial;
    }
    public float getToughness() {
        return this.toughness;
    }
    public float getKnockbackResistance() {
        return this.knockbackResistance;
    }
}
