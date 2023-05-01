package com.jayugg.end_aspected.item;

import com.jayugg.end_aspected.config.ModConfig;
import net.minecraft.item.IItemTier;
import net.minecraft.util.text.TranslationTextComponent;


public class DragonforgedAspectOfTheEndItem extends AbstractAspectOfTheEndItem {

    public DragonforgedAspectOfTheEndItem(IItemTier tier, int attackDamageIn, float attackSpeedIn, Properties builder) {
        super(tier, attackDamageIn, attackSpeedIn, builder);
    }
    public Double loadCooldownConfig() { return ModConfig.daoteCooldown.get(); }
    public boolean loadEnableCooldownConfig() {
        return ModConfig.enableDaoteCooldown.get();
    }
    public boolean loadEnableLostDurabilityConfig() {
        return ModConfig.enableDaoteLostDurability.get();
    }
    public int loadLostDurabilityConfig() {
        return ModConfig.daoteLostDurability.get();
    }
    public TranslationTextComponent getLore() {
        return new TranslationTextComponent("tooltip.end_aspected.dragonforged_aspect_of_the_end_shift");
    }

}