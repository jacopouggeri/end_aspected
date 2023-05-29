package net.jayugg.end_aspected.item.end;

import net.jayugg.end_aspected.config.ModConfig;
import net.minecraft.item.IItemTier;
import net.minecraft.util.text.TranslationTextComponent;

public class AspectOfTheEndItem extends AbstractAspectOfTheEndItem {

    public AspectOfTheEndItem(IItemTier tier, int attackDamageIn, float attackSpeedIn, Properties builder) {
        super(tier, attackDamageIn, attackSpeedIn, builder);
    }

    public Double loadCooldownConfig() {
        return ModConfig.aoteCooldown.get();
    }
    public boolean loadEnableCooldownConfig() { return ModConfig.enableAoteCooldown.get(); }
    public boolean loadEnableLostDurabilityConfig() {
        return ModConfig.enableAoteLostDurability.get();
    }
    public int loadLostDurabilityConfig() {
        return ModConfig.aoteLostDurability.get();
    }
    public TranslationTextComponent getLore() {
        return new TranslationTextComponent("tooltip.end_aspected.aspect_of_the_end_shift");
    }

}
