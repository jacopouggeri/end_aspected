package com.jayugg.end_aspected.config;
import net.minecraftforge.common.ForgeConfigSpec;

public final class ModConfig {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec SPEC;
    public static ForgeConfigSpec.ConfigValue<Integer> teleportDistance;
    public static ForgeConfigSpec.ConfigValue<Integer> maxTeleports;

    public static ForgeConfigSpec.BooleanValue enableAoteCooldown;
    public static ForgeConfigSpec.ConfigValue<Long> aoteCooldown;
    public static ForgeConfigSpec.BooleanValue enableAoteLostDurability;
    public static ForgeConfigSpec.ConfigValue<Integer> aoteLostDurability;

    public static ForgeConfigSpec.BooleanValue enableNaoteCooldown;
    public static ForgeConfigSpec.ConfigValue<Long> naoteCooldown;
    public static ForgeConfigSpec.BooleanValue enableNaoteLostDurability;
    public static ForgeConfigSpec.ConfigValue<Integer> naoteLostDurability;

    public static ForgeConfigSpec.BooleanValue enableDaoteCooldown;
    public static ForgeConfigSpec.ConfigValue<Long> daoteCooldown;
    public static ForgeConfigSpec.BooleanValue enableDaoteLostDurability;
    public static ForgeConfigSpec.ConfigValue<Integer> daoteLostDurability;

    public static ForgeConfigSpec.BooleanValue enableShulkerWandCooldown;
    public static ForgeConfigSpec.ConfigValue<Long>  shulkerWandCooldown;
    public static ForgeConfigSpec.BooleanValue enableShulkerWandLostDurability;
    public static ForgeConfigSpec.ConfigValue<Integer> shulkerWandLostDurability;
    public static ForgeConfigSpec.ConfigValue<Integer> enderTrapRadius;
    public static ForgeConfigSpec.ConfigValue<Long>  unstablePhasePercentDamage;
    public static ForgeConfigSpec.BooleanValue unstableTeleports;
    public static ForgeConfigSpec.ConfigValue<Integer> unstableTeleportsLimit;
    public static ForgeConfigSpec.DoubleValue unstablePhaseCooldownMultiplier;


    static {
        BUILDER.push("Item Settings");

        teleportDistance = BUILDER.comment("Teleport distance in blocks for the Aspect of the End and upgrades.").defineInRange("teleportDistance", 8, 0, Integer.MAX_VALUE);
        maxTeleports = BUILDER.comment("Maximum number of teleports of Aspect of the End and upgrades before the cooldown is triggered.").defineInRange("maxTeleports", 6, 0, Integer.MAX_VALUE);

        BUILDER.push("Aspect of The End");
        enableAoteCooldown = BUILDER.comment("Enable cooldown.").define("enableCooldown", true);
        BUILDER.push("Cooldown");
        aoteCooldown = BUILDER.comment("Cooldown duration. Works only if enabled.").defineInRange("cooldownDuration", 20, 0, Long.MAX_VALUE);
        enableAoteLostDurability = BUILDER.comment("Enable losing durability during cooldown. If false the ability can't be used during cooldown, if true the sword can be used but will lose durability.").define("enableLostDurability", true);
        aoteLostDurability = BUILDER.comment("Durability lost on use during cooldown.").defineInRange("lostDurability", 20, 0, Integer.MAX_VALUE);
        BUILDER.pop();
        BUILDER.pop();

        BUILDER.push("Netherforged Aspect of The End");
        enableNaoteCooldown = BUILDER.comment("Enable cooldown.").define("enableCooldown", true);
        BUILDER.push("Cooldown");
        naoteCooldown = BUILDER.comment("Cooldown duration. Works only if enabled.").defineInRange("cooldownDuration", 10, 0, Long.MAX_VALUE);
        enableNaoteLostDurability = BUILDER.comment("Enable losing durability during cooldown. If false the ability can't be used during cooldown, if true the sword can be used but will lose durability.").define("enableLostDurability", true);
        naoteLostDurability = BUILDER.comment("Durability lost on use during cooldown.").defineInRange("lostDurability", 20, 0, Integer.MAX_VALUE);
        BUILDER.pop();
        BUILDER.pop();

        BUILDER.push("Dragonforged Aspect of The End");
        enableDaoteCooldown = BUILDER.comment("Enable cooldown.").define("enableCooldown", false);
        BUILDER.push("Cooldown");
        daoteCooldown = BUILDER.comment("Cooldown duration. Works only if enabled.").defineInRange("cooldownDuration", 5, 0, Long.MAX_VALUE);
        enableDaoteLostDurability = BUILDER.comment("Enable losing durability during cooldown. If false the ability can't be used during cooldown, if true the sword can be used but will lose durability.").define("enableLostDurability", true);
        daoteLostDurability = BUILDER.comment("Durability lost on use during cooldown.").defineInRange("lostDurability", 20, 0, Integer.MAX_VALUE);
        BUILDER.pop();
        BUILDER.pop();

        BUILDER.push("Shulker Wand");
        enableShulkerWandCooldown = BUILDER.comment("Maximum number of teleports before the cooldown is triggered.").define("enableCooldown", true);
        shulkerWandCooldown = BUILDER.comment("Cooldown duration.").defineInRange("cooldownDuration", 5, 0, Long.MAX_VALUE);
        enableShulkerWandLostDurability = BUILDER.comment("Enable losing durability during cooldown. If false the ability can't be used during cooldown, if true the wand can be used but will lose durability.").define("enableLostDurability", true);
        shulkerWandLostDurability = BUILDER.comment("Durability lost on use during cooldown.").defineInRange("lostDurability", 5, 0, Integer.MAX_VALUE);
        BUILDER.pop();

        BUILDER.pop();

        BUILDER.push("Block Settings");
        enderTrapRadius = BUILDER.comment("Set ender trap activation radius (below 33 will not trap endermen effectively).").defineInRange("effectRadius", 33, 0, 100);
        BUILDER.pop();

        BUILDER.push("Effect Settings");
        BUILDER.push("Unstable Phase");
        unstablePhasePercentDamage = BUILDER.comment("Damage (inn% of max health) the Unstable Phase effect deals on attempted teleports.").defineInRange("percentDamage", 20, 0, Long.valueOf(100));
        unstableTeleports = BUILDER.comment("If true, too many teleports during the Aspect of the End cooldown will give the Unstable Phase effect.").define("unstableTeleports", true);
        unstableTeleportsLimit = BUILDER.comment("Number of teleports before the player gets Unstable Phase.").defineInRange("unstableTeleportsLimit", 20, 0, Integer.MAX_VALUE);
        unstablePhaseCooldownMultiplier = BUILDER.comment("How much (in %) of the remaining cooldown should be added to the unstable phase duration?").defineInRange("unstablePhaseCooldownMultiplier", 5.0f, 0.0f, 100.0f);
        BUILDER.pop();
        BUILDER.pop();

        SPEC = BUILDER.build();
    }

}
