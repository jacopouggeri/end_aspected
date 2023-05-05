package net.jayugg.end_aspected.config;
import net.minecraftforge.common.ForgeConfigSpec;

public final class ModConfig {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec SPEC;
    public static ForgeConfigSpec.ConfigValue<Integer> teleportDistance;
    public static ForgeConfigSpec.ConfigValue<Integer> maxTeleports;

    public static ForgeConfigSpec.BooleanValue enableAoteCooldown;
    public static ForgeConfigSpec.DoubleValue aoteCooldown;
    public static ForgeConfigSpec.BooleanValue enableAoteLostDurability;
    public static ForgeConfigSpec.ConfigValue<Integer> aoteLostDurability;

    public static ForgeConfigSpec.BooleanValue enableNaoteCooldown;
    public static ForgeConfigSpec.DoubleValue naoteCooldown;
    public static ForgeConfigSpec.BooleanValue enableNaoteLostDurability;
    public static ForgeConfigSpec.ConfigValue<Integer> naoteLostDurability;

    public static ForgeConfigSpec.BooleanValue enableDaoteCooldown;
    public static ForgeConfigSpec.DoubleValue daoteCooldown;
    public static ForgeConfigSpec.BooleanValue enableDaoteLostDurability;
    public static ForgeConfigSpec.ConfigValue<Integer> daoteLostDurability;

    public static ForgeConfigSpec.BooleanValue enableShulkerWandCooldown;
    public static ForgeConfigSpec.ConfigValue<Integer>  shulkerWandCooldown;
    public static ForgeConfigSpec.BooleanValue enableShulkerWandLostDurability;
    public static ForgeConfigSpec.ConfigValue<Integer> shulkerWandLostDurability;
    public static ForgeConfigSpec.ConfigValue<Integer> enderTrapRadius;
    public static ForgeConfigSpec.ConfigValue<Long>  unstablePhasePercentDamage;
    public static ForgeConfigSpec.BooleanValue unstableTeleports;
    public static ForgeConfigSpec.ConfigValue<Integer> unstableTeleportsLimit;
    public static ForgeConfigSpec.DoubleValue unstablePhaseCooldownMultiplier;

    static {
        String COOLDOWN_FLAG_COMMENT = "Enable cooldown.";
        String COOLDOWN_COMMENT = "Cooldown duration. Works only if enabled.";

        String DURABILITY_FLAG_COMMENT = "If true the sword will lose durability on use.";
        String DURABILITY_COMMENT = "Durability lost on use.";

        BUILDER.push("Item Settings");

        teleportDistance = BUILDER.comment("Teleport distance in blocks for the Aspect of the End and upgrades.").defineInRange("teleportDistance", 6, 0, Integer.MAX_VALUE);
        maxTeleports = BUILDER.comment("Maximum number of teleports of Aspect of the End and upgrades before the cooldown is triggered.").defineInRange("maxTeleports", 6, 0, Integer.MAX_VALUE);
        BUILDER.comment("Teleports after cooldown will give the Unstable Phase effect. Check its config to disable unstable teleports.");

        BUILDER.push("Aspect of The End");
        enableAoteCooldown = BUILDER.comment(COOLDOWN_FLAG_COMMENT).define("enableCooldown", true);
        BUILDER.push("Cooldown");
        aoteCooldown = BUILDER.comment(COOLDOWN_COMMENT).defineInRange("cooldownDuration", 3, 0, Double.MAX_VALUE);
        enableAoteLostDurability = BUILDER.comment(DURABILITY_FLAG_COMMENT).define("enableLostDurability", true);
        aoteLostDurability = BUILDER.comment(DURABILITY_COMMENT).defineInRange("lostDurability", 1, 0, Integer.MAX_VALUE);
        BUILDER.pop();
        BUILDER.pop();

        BUILDER.push("Netherforged Aspect of The End");
        enableNaoteCooldown = BUILDER.comment(COOLDOWN_FLAG_COMMENT).define("enableCooldown", true);
        BUILDER.push("Cooldown");
        naoteCooldown = BUILDER.comment(COOLDOWN_COMMENT).defineInRange("cooldownDuration", 1.5, 0, Double.MAX_VALUE);
        enableNaoteLostDurability = BUILDER.comment(DURABILITY_FLAG_COMMENT).define("enableLostDurability", true);
        naoteLostDurability = BUILDER.comment(DURABILITY_COMMENT).defineInRange("lostDurability", 1, 0, Integer.MAX_VALUE);
        BUILDER.pop();
        BUILDER.pop();

        BUILDER.push("Dragonforged Aspect of The End");
        enableDaoteCooldown = BUILDER.comment(COOLDOWN_FLAG_COMMENT).define("enableCooldown", false);
        BUILDER.push("Cooldown");
        daoteCooldown = BUILDER.comment(COOLDOWN_COMMENT).defineInRange("cooldownDuration", 1, 0, Double.MAX_VALUE);
        enableDaoteLostDurability = BUILDER.comment(DURABILITY_FLAG_COMMENT).define("enableLostDurability", true);
        daoteLostDurability = BUILDER.comment(DURABILITY_COMMENT).defineInRange("lostDurability", 1, 0, Integer.MAX_VALUE);
        BUILDER.pop();
        BUILDER.pop();

        BUILDER.push("Shulker Wand");
        enableShulkerWandCooldown = BUILDER.comment(COOLDOWN_FLAG_COMMENT).define("enableCooldown", true);
        shulkerWandCooldown = BUILDER.comment("Cooldown duration.").defineInRange("cooldownDuration", 2, 0, Integer.MAX_VALUE);
        enableShulkerWandLostDurability = BUILDER.comment("If true the wand will lose durability on use.").define("enableLostDurability", true);
        shulkerWandLostDurability = BUILDER.comment(DURABILITY_COMMENT).defineInRange("lostDurability", 5, 0, Integer.MAX_VALUE);
        BUILDER.pop();

        BUILDER.pop();

        BUILDER.push("Block Settings");
        enderTrapRadius = BUILDER.comment("Set ender trap activation radius (below 33 will not trap endermen effectively).").defineInRange("effectRadius", 33, 0, 100);
        BUILDER.pop();

        BUILDER.push("Effect Settings");
        BUILDER.push("Unstable Phase");
        unstablePhasePercentDamage = BUILDER.comment("Damage (inn% of max health) the Unstable Phase effect deals on attempted teleports.").defineInRange("percentDamage", 20, 0, Long.valueOf(100));
        unstableTeleports = BUILDER.comment("If true, too many teleports during the Aspect of the End cooldown will give the Unstable Phase effect.").define("unstableTeleports", true);
        unstableTeleportsLimit = BUILDER.comment("Number of cooldown cycles before the player gets Unstable Phase.").defineInRange("unstableTeleportsLimit", 2, 0, Integer.MAX_VALUE);
        unstablePhaseCooldownMultiplier = BUILDER.comment("How much (in %) of the cooldown should Unstable Phase last when given?").defineInRange("unstablePhaseCooldownMultiplier", 200.0f, 0.0f, Float.MAX_VALUE);
        BUILDER.pop();
        BUILDER.pop();

        SPEC = BUILDER.build();
    }

}
