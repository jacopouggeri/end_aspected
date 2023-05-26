package net.jayugg.end_aspected.item.aote;

import net.jayugg.end_aspected.config.ModConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Tier;


public class NetherforgedAspectOfTheEndItem extends AbstractAspectOfTheEndItem {

    public NetherforgedAspectOfTheEndItem(Tier tier, int attackDamageIn, float attackSpeedIn, Properties builder) {
        super(tier, attackDamageIn, attackSpeedIn, builder);
    }

    public Double loadCooldownConfig() {
        return ModConfig.naoteCooldown.get();
    }
    public boolean loadEnableCooldownConfig() { return ModConfig.enableNaoteCooldown.get(); }
    public boolean loadEnableLostDurabilityConfig() {
        return ModConfig.enableNaoteLostDurability.get();
    }
    public int loadLostDurabilityConfig() {
        return ModConfig.naoteLostDurability.get();
    }
    public Component getLore() {
        return Component.translatable("tooltip.end_aspected.netherforged_aspect_of_the_end_shift");
    }

}