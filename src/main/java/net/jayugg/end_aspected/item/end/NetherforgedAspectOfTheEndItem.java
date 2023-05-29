package net.jayugg.end_aspected.item.end;

import net.jayugg.end_aspected.config.ModConfig;
import net.minecraft.item.IItemTier;
import net.minecraft.util.text.TranslationTextComponent;


public class NetherforgedAspectOfTheEndItem extends AbstractAspectOfTheEndItem {

    public NetherforgedAspectOfTheEndItem(IItemTier tier, int attackDamageIn, float attackSpeedIn, Properties builder) {
        super(tier, attackDamageIn, attackSpeedIn, builder);
    }

    public Double loadCooldownConfig() { return ModConfig.naoteCooldown.get(); }
    public boolean loadEnableCooldownConfig() {
        return ModConfig.enableNaoteCooldown.get();
    }
    public boolean loadEnableLostDurabilityConfig() {
        return ModConfig.enableNaoteLostDurability.get();
    }
    public int loadLostDurabilityConfig() {
        return ModConfig.naoteLostDurability.get();
    }
    public TranslationTextComponent getLore() {
        return new TranslationTextComponent("tooltip.end_aspected.netherforged_aspect_of_the_end_shift");
    }

}