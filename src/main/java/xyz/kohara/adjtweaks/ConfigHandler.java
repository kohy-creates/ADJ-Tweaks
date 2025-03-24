package xyz.kohara.adjtweaks;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class ConfigHandler {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    // Configurable values
    public static final ForgeConfigSpec.DoubleValue DURABILITY_SAVE_CHANCE;
    public static final ForgeConfigSpec.DoubleValue EXTRA_FORTUNE_CHANCE;
    public static final ForgeConfigSpec.BooleanValue DISABLE_CRITS;
    public static final ForgeConfigSpec.BooleanValue DISABLE_SWEEP_ATTACKS;
    public static final ForgeConfigSpec.ConfigValue<Double> RESISTANCE_DAMAGE_REDUCTION;
    public static final ForgeConfigSpec.ConfigValue<Integer> CAMPFIRE_HEAL_RATE;
    public static final ForgeConfigSpec.ConfigValue<Double> CAMPFIRE_HEAL_AMOUNT;
    public static final ForgeConfigSpec.ConfigValue<Double> CAMPFIRE_HEAL_RADIUS;
    public static final ForgeConfigSpec.ConfigValue<Double> CAMPFIRE_HEAL_RADIUS_SIGNAL;

    static {
        BUILDER.comment("Tools").push("tools");

        DURABILITY_SAVE_CHANCE = BUILDER
            .comment("Chance (0 - 1) to prevent durability loss. Default: 0.5 (50%)")
            .comment("Set to 0 to disable or 1 to make all items unbreakable")
            .defineInRange("durabilitySaveChance", 0.5, 0, 1);

        EXTRA_FORTUNE_CHANCE = BUILDER
            .comment("Chance (0 - 1) to treat blocks as if they were mined with Fortune higher by 1")
            .comment("This also applies to breaking blocks without Fortune. Set to 0 to disable")
            .defineInRange("extraFortuneChance", 1.0, 0, 1);

        BUILDER.pop();

        BUILDER.comment("Combat").push("combat");

        DISABLE_CRITS = BUILDER
                .comment("Disables critical attacks")
                .define("disableCrits", true);

        DISABLE_SWEEP_ATTACKS = BUILDER
                .comment("Disables sweep attacks")
                .define("disableSweep", true);

        BUILDER.pop();

        BUILDER.comment("Campfires").push("campfire");

        CAMPFIRE_HEAL_RATE = BUILDER
                .comment("The delay in ticks between Cozy Campfire effect's heals")
                .define("healRate", 80);

        CAMPFIRE_HEAL_AMOUNT = BUILDER
                .comment("The heal amount of the Cozy Campfire effect")
                .define("healAmount", 1.0);

        CAMPFIRE_HEAL_RADIUS = BUILDER
                .comment("Radius around the Campfire where players get the effect")
                .define("healRadius", 16.0);

        CAMPFIRE_HEAL_RADIUS_SIGNAL = BUILDER
                .comment("Radius around a signal Campfire")
                .define("healRadiusSignal", 32.0);

        BUILDER.pop();

        BUILDER.comment("Effects").push("effect");

        RESISTANCE_DAMAGE_REDUCTION = BUILDER
                .comment("How much damage reduction to give per level of Resistance")
                .define("resistanceDR", 0.1);

        BUILDER.pop();
    }

    public static final ForgeConfigSpec CONFIG = BUILDER.build();

    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CONFIG);
    }
}
