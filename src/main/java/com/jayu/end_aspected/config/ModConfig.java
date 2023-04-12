package com.jayu.end_aspected.config;
import net.minecraftforge.common.ForgeConfigSpec;

public final class ModConfig {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec SPEC;
    public static ForgeConfigSpec.ConfigValue<Integer> teleportDistance;
    public static ForgeConfigSpec.ConfigValue<Long> maxTeleports;
    public static ForgeConfigSpec.ConfigValue<Integer> enderTrapRadius;
    public static ForgeConfigSpec.ConfigValue<Long>  shulkerWandCooldown;


    static {
        BUILDER.push("Item Settings");
        BUILDER.push("Aspect of The End");
        teleportDistance = BUILDER.comment("Teleport distance in blocks for the Aspect of the End").defineInRange("teleportDistance", 8, 0, Integer.MAX_VALUE);
        maxTeleports = BUILDER.comment("Maximum number of teleports before the cooldown").defineInRange("maxTeleports", 6, 0, Long.MAX_VALUE);
        BUILDER.pop();
        shulkerWandCooldown = BUILDER.comment("Shulker wand cooldown").defineInRange("shulkerWandCooldown", 2, 0, Long.MAX_VALUE);
        BUILDER.pop();

        BUILDER.push("Block Settings");
        enderTrapRadius = BUILDER.comment("Set ender trap activation radius (below 33 will not trap endermen effectively)").defineInRange("enderTrapRadius", 33, 0, 100);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }

}
