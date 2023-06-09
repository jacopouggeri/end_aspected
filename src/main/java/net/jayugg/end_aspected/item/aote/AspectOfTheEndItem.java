package net.jayugg.end_aspected.item.aote;

import net.jayugg.end_aspected.config.ModConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Tier;

public class AspectOfTheEndItem extends AbstractAspectOfTheEndItem {

    public AspectOfTheEndItem(Tier tier, int attackDamageIn, float attackSpeedIn, Properties builder) {
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
    public Component getLore() {
        return Component.translatable("tooltip.end_aspected.aspect_of_the_end_shift");
    }

}
