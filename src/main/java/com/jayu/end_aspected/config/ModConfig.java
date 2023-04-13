package com.jayu.end_aspected.config;
import net.minecraftforge.common.ForgeConfigSpec;

public final class ModConfig {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec SPEC;
    public static ForgeConfigSpec.ConfigValue<Integer> teleportDistance;
    public static ForgeConfigSpec.ConfigValue<Long> maxTeleports;
    public static ForgeConfigSpec.BooleanValue enableAoteCooldown;
    public static ForgeConfigSpec.ConfigValue<Long> aoteCooldown;
    public static ForgeConfigSpec.ConfigValue<Integer> aoteLostDurability;
    public static ForgeConfigSpec.BooleanValue enableShulkerWandCooldown;
    public static ForgeConfigSpec.ConfigValue<Long>  shulkerWandCooldown;
    public static ForgeConfigSpec.ConfigValue<Integer> shulkerWandLostDurability;
    public static ForgeConfigSpec.ConfigValue<Integer> enderTrapRadius;


    static {
        BUILDER.push("Item Settings");
        BUILDER.push("Aspect of The End");
        teleportDistance = BUILDER.comment("Teleport distance in blocks for the Aspect of the End").defineInRange("teleportDistance", 8, 0, Integer.MAX_VALUE);
        maxTeleports = BUILDER.comment("Maximum number of teleports before the cooldown is triggered").defineInRange("maxTeleports", 6, 0, Long.MAX_VALUE);
        enableAoteCooldown = BUILDER.comment("Enable cooldown for Aspect of the End and Netherforged Aspect of the End").define("enableAoteCooldown", true);
        aoteCooldown = BUILDER.comment("Cooldown for Aspect of the End (Netherforged will have half). Works only if enabled").defineInRange("aoteCooldown", 20, 0, Long.MAX_VALUE);
        aoteLostDurability = BUILDER.comment("Durability lost on use during cooldown").defineInRange("aoteLostDurability", 20, 0, Integer.MAX_VALUE);
        BUILDER.pop();

        BUILDER.push("Shulker Wand");
        enableShulkerWandCooldown = BUILDER.comment("Maximum number of teleports before the cooldown is triggered").define("enableShulkerWandCooldown", true);
        shulkerWandCooldown = BUILDER.comment("Shulker wand cooldown").defineInRange("shulkerWandCooldown", 5, 0, Long.MAX_VALUE);
        shulkerWandLostDurability = BUILDER.comment("Durability lost on use during cooldown").defineInRange("shulkerWandLostDurability", 5, 0, Integer.MAX_VALUE);
        BUILDER.pop();

        BUILDER.push("Block Settings");
        enderTrapRadius = BUILDER.comment("Set ender trap activation radius (below 33 will not trap endermen effectively)").defineInRange("enderTrapRadius", 33, 0, 100);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }

}
