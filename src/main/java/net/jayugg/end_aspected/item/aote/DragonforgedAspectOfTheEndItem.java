package net.jayugg.end_aspected.item.aote;

import net.jayugg.end_aspected.config.ModConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Tier;


public class DragonforgedAspectOfTheEndItem extends AbstractAspectOfTheEndItem {

    public DragonforgedAspectOfTheEndItem(Tier tier, int attackDamageIn, float attackSpeedIn, Properties builder) {
        super(tier, attackDamageIn, attackSpeedIn, builder);
    }

    public Double loadCooldownConfig() {
        return ModConfig.daoteCooldown.get();
    }
    public boolean loadEnableCooldownConfig() { return ModConfig.enableDaoteCooldown.get(); }
    public boolean loadEnableLostDurabilityConfig() {
        return ModConfig.enableDaoteLostDurability.get();
    }
    public int loadLostDurabilityConfig() {
        return ModConfig.daoteLostDurability.get();
    }
    public Component getLore() {
        return Component.translatable("tooltip.end_aspected.dragonforged_aspect_of_the_end_shift");
    }

}