package com.jayu.end_aspected.config;
import net.minecraftforge.common.ForgeConfigSpec;

public final class ModConfig {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec SPEC;
    public static ForgeConfigSpec.ConfigValue<Integer> teleportDistance;
    public static ForgeConfigSpec.ConfigValue<Long> maxTeleports;

    public static ForgeConfigSpec.IntValue aoteDamage;
    public static ForgeConfigSpec.BooleanValue enableAoteCooldown;
    public static ForgeConfigSpec.ConfigValue<Long> aoteCooldown;
    public static ForgeConfigSpec.BooleanValue enableAoteLostDurability;
    public static ForgeConfigSpec.ConfigValue<Integer> aoteLostDurability;

    public static ForgeConfigSpec.IntValue naoteDamage;
    public static ForgeConfigSpec.BooleanValue enableNaoteCooldown;
    public static ForgeConfigSpec.ConfigValue<Long> naoteCooldown;
    public static ForgeConfigSpec.BooleanValue enableNaoteLostDurability;
    public static ForgeConfigSpec.ConfigValue<Integer> naoteLostDurability;

    public static ForgeConfigSpec.IntValue daoteDamage;
    public static ForgeConfigSpec.BooleanValue enableDaoteCooldown;
    public static ForgeConfigSpec.ConfigValue<Long> daoteCooldown;
    public static ForgeConfigSpec.BooleanValue enableDaoteLostDurability;
    public static ForgeConfigSpec.ConfigValue<Integer> daoteLostDurability;

    public static ForgeConfigSpec.BooleanValue enableShulkerWandCooldown;
    public static ForgeConfigSpec.ConfigValue<Long>  shulkerWandCooldown;
    public static ForgeConfigSpec.BooleanValue enableShulkerWandLostDurability;
    public static ForgeConfigSpec.ConfigValue<Integer> shulkerWandLostDurability;
    public static ForgeConfigSpec.ConfigValue<Integer> enderTrapRadius;


    static {
        BUILDER.push("Item Settings");
        teleportDistance = BUILDER.comment("Teleport distance in blocks for the Aspect of the End and upgrades.").defineInRange("teleportDistance", 8, 0, Integer.MAX_VALUE);
        maxTeleports = BUILDER.comment("Maximum number of teleports of Aspect of the End and upgrades before the cooldown is triggered.").defineInRange("maxTeleports", 6, 0, Long.MAX_VALUE);

        BUILDER.push("Aspect of The End");
        aoteDamage = BUILDER.comment("Sword damage. Default is like vanilla diamond sword.").defineInRange("damage", 7, 0, Integer.MAX_VALUE);
        enableAoteCooldown = BUILDER.comment("Enable cooldown.").define("enableCooldown", true);
        BUILDER.push("Cooldown");
        aoteCooldown = BUILDER.comment("Cooldown duration. Works only if enabled.").defineInRange("cooldownDuration", 20, 0, Long.MAX_VALUE);
        enableAoteLostDurability = BUILDER.comment("Enable losing durability during cooldown. If false the ability can't be used during cooldown, if true the sword can be used but will lose durability.").define("enableLostDurability", true);
        aoteLostDurability = BUILDER.comment("Durability lost on use during cooldown.").defineInRange("lostDurability", 20, 0, Integer.MAX_VALUE);
        BUILDER.pop();
        BUILDER.pop();

        BUILDER.push("Netherforged Aspect of The End");
        naoteDamage = BUILDER.comment("Sword damage. Default is like vanilla netherite sword.").defineInRange("damage", 8, 0, Integer.MAX_VALUE);
        enableNaoteCooldown = BUILDER.comment("Enable cooldown.").define("enableCooldown", true);
        BUILDER.push("Cooldown");
        naoteCooldown = BUILDER.comment("Cooldown duration. Works only if enabled.").defineInRange("cooldownDuration", 10, 0, Long.MAX_VALUE);
        enableNaoteLostDurability = BUILDER.comment("Enable losing durability during cooldown. If false the ability can't be used during cooldown, if true the sword can be used but will lose durability.").define("enableLostDurability", true);
        naoteLostDurability = BUILDER.comment("Durability lost on use during cooldown.").defineInRange("lostDurability", 20, 0, Integer.MAX_VALUE);
        BUILDER.pop();
        BUILDER.pop();

        BUILDER.push("Dragonforged Aspect of The End");
        daoteDamage = BUILDER.comment("Sword damage. Default is like vanilla netherite sword + 1.").defineInRange("damage", 9, 0, Integer.MAX_VALUE);
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

        BUILDER.push("Block Settings");
        enderTrapRadius = BUILDER.comment("Set ender trap activation radius (below 33 will not trap endermen effectively).").defineInRange("effectRadius", 33, 0, 100);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }

}
