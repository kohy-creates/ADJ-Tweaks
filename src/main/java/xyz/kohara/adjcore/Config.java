package xyz.kohara.adjcore;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    // Configurable values
    public static final ForgeConfigSpec.DoubleValue DURABILITY_SAVE_CHANCE;
    public static final ForgeConfigSpec.DoubleValue EXTRA_FORTUNE_CHANCE;
    public static final ForgeConfigSpec.ConfigValue<Integer> SHIELD_DELAY;
    public static final ForgeConfigSpec.BooleanValue DISABLE_CRITS;
    public static final ForgeConfigSpec.BooleanValue DISABLE_SWEEP_ATTACKS;
    public static final ForgeConfigSpec.ConfigValue<Integer> CAMPFIRE_HEAL_RATE;
    public static final ForgeConfigSpec.ConfigValue<Double> CAMPFIRE_HEAL_AMOUNT;
    public static final ForgeConfigSpec.ConfigValue<Double> CAMPFIRE_HEAL_RADIUS;
    public static final ForgeConfigSpec.ConfigValue<Double> CAMPFIRE_HEAL_RADIUS_SIGNAL;
    public static final ForgeConfigSpec.DoubleValue BOW_INACCURACY;
    public static final ForgeConfigSpec.DoubleValue RANDOM_DAMAGE_VARIATION;
    public static final ForgeConfigSpec.DoubleValue UNBREAKNG_DURABILITY_MULTIPLIER;
    public static final ForgeConfigSpec.ConfigValue<String> CURIO_TYPE_TO_KEEP;
    public static final ForgeConfigSpec.ConfigValue<String> SOULBOUND_FOR_CURIOS;

    public static final ForgeConfigSpec.ConfigValue<Double> MIN_DAMAGE_TAKEN;
    public static final ForgeConfigSpec.ConfigValue<Double> ARMOR_POINT_REDUCTION_FACTOR;
    public static final ForgeConfigSpec.ConfigValue<Double> ARMOR_DURABILITY_DAMAGE_FACTOR;

    static {
        BUILDER.comment("Tools").push("tools");

        DURABILITY_SAVE_CHANCE = BUILDER
                .comment("Chance (0 - 1) to prevent durability loss. Default: 0.5 (50%)")
                .comment("Set to 0 to disable or 1 to make all items unbreakable")
                .defineInRange("DURABILITY_SAVE_CHANCE", 0.5, 0, 1);

        EXTRA_FORTUNE_CHANCE = BUILDER
                .comment("Chance (0 - 1) to treat blocks as if they were mined with Fortune higher by 1")
                .comment("This also applies to breaking blocks without Fortune. Set to 0 to disable")
                .defineInRange("EXTRA_FORTUNE_CHANCE", 1.0, 0, 1);

        SHIELD_DELAY = BUILDER
                .comment("Delay in ticks after which shield will start blocking damage")
                .comment("5 is vanilla, 0 is immediately")
                .defineInRange("SHIELD_DELAY", 0, 0, Integer.MAX_VALUE);

        BOW_INACCURACY = BUILDER
                .comment("Inaccuracy of bows")
                .comment("1 is vanilla")
                .defineInRange("BOW_INACCURACY", 1.8d, 0d, 90.0d);

        UNBREAKNG_DURABILITY_MULTIPLIER = BUILDER
                .comment("Durability multiplier for items enchanted with Unbreaking")
                .defineInRange("UNBREAKNG_DURABILITY_MULTIPLIER", 2.0d, 1.0d, Double.MAX_VALUE);

        CURIO_TYPE_TO_KEEP = BUILDER
                .comment("Removes curio slots other than this one. Also assigns all curios to the specified slot.")
                .comment("Probably case sensitive")
                .define("CURIO_TYPE_TO_KEEP", "ring");

        SOULBOUND_FOR_CURIOS = BUILDER
                .comment("What enchantment causes Curios to be kept?")
                .comment("Set to a placeholder so that it doesn't crash if set to something modded that isn't loaded")
                .define("SOULBOUND_FOR_CURIOS", "minecraft:efficiency");

        BUILDER.pop();

        BUILDER.comment("Combat").push("combat");

        DISABLE_CRITS = BUILDER
                .comment("Disables critical attacks")
                .define("DISABLE_CRITS", true);

        DISABLE_SWEEP_ATTACKS = BUILDER
                .comment("Disables sweep attacks")
                .define("DISABLE_SWEEP_ATTACKS", true);

        RANDOM_DAMAGE_VARIATION = BUILDER
                .comment("Variates dealt damage by +-% this value")
                .defineInRange("RANDOM_DAMAGE_VARIATION", 20d, 0d, 100d);

        ARMOR_POINT_REDUCTION_FACTOR = BUILDER
                .comment("How many armor points for damage to get reduced by 1")
                .defineInRange("ARMOR_POINT_REDUCTION_FACTOR", 2d, 0d, Double.MAX_VALUE);

        MIN_DAMAGE_TAKEN = BUILDER
                .comment("Minimum damage dealt by an attack after all forms of reductions")
                .defineInRange("MIN_DAMAGE_TAKEN", 0.2d, 0d, 100d);

        ARMOR_DURABILITY_DAMAGE_FACTOR = BUILDER
                .comment("How many damage points per 1 durability lost on block")
                .defineInRange("ARMOR_DURABILITY_DAMAGE_FACTOR", 4d, 1d, 100d);

        BUILDER.pop();

        BUILDER.comment("Campfires").push("campfire");

        CAMPFIRE_HEAL_RATE = BUILDER
                .comment("The delay in ticks between Cozy Campfire effect's heals")
                .define("CAMPFIRE_HEAL_RATE", 80);

        CAMPFIRE_HEAL_AMOUNT = BUILDER
                .comment("The heal amount of the Cozy Campfire effect")
                .define("CAMPFIRE_HEAL_AMOUNT", 1.0);

        CAMPFIRE_HEAL_RADIUS = BUILDER
                .comment("Radius around the Campfire where players get the effect")
                .define("CAMPFIRE_HEAL_RADIUS", 16.0);

        CAMPFIRE_HEAL_RADIUS_SIGNAL = BUILDER
                .comment("Radius around a signal Campfire")
                .define("CAMPFIRE_HEAL_RADIUS_SIGNAL", 32.0);

        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}
