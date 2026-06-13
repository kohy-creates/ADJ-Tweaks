package xyz.kohara.adjcore;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;

public class Config {
	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static final ForgeConfigSpec SPEC;

	// Configurable values
	private static final ForgeConfigSpec.ConfigValue<Integer> SHIELD_DELAY;
	private static final ForgeConfigSpec.BooleanValue DISABLE_CRITS;
	private static final ForgeConfigSpec.BooleanValue DISABLE_SWEEP_ATTACKS;
	private static final ForgeConfigSpec.DoubleValue BOW_INACCURACY;
	private static final ForgeConfigSpec.DoubleValue RANDOM_DAMAGE_VARIATION;
	private static final ForgeConfigSpec.DoubleValue MIN_DAMAGE_TAKEN;
	private static final ForgeConfigSpec.DoubleValue ARMOR_POINT_REDUCTION_FACTOR;
	private static final ForgeConfigSpec.DoubleValue ARMOR_POINT_REDUCTION_FACTOR_ENTITY;
	private static final ForgeConfigSpec.DoubleValue ARMOR_DURABILITY_DAMAGE_FACTOR;

	private static final ForgeConfigSpec.DoubleValue CAMPFIRE_HEAL_RADIUS;
	private static final ForgeConfigSpec.DoubleValue CAMPFIRE_HEAL_RADIUS_SIGNAL;

	private static final ForgeConfigSpec.DoubleValue DURABILITY_SAVE_CHANCE;
	private static final ForgeConfigSpec.DoubleValue UNBREAKNG_DURABILITY_MULTIPLIER;
	private static final ForgeConfigSpec.ConfigValue<String> SOULBOUND_FOR_CURIOS;

	private static final ForgeConfigSpec.IntValue MIN_STRUCTURE_DISTANCE;

	private static final ForgeConfigSpec.IntValue HARDCORE_RESPAW_RADIUS;

	private static final ForgeConfigSpec.DoubleValue GLOBAL_MULTIPLIER;
	private static final ForgeConfigSpec.DoubleValue DAMAGE_TYPE_MULTIPLIER;
	private static final ForgeConfigSpec.DoubleValue JUMP_MULTIPLIER;
	private static final ForgeConfigSpec.DoubleValue SWIM_MULTIPLIER;
	private static final ForgeConfigSpec.DoubleValue UNDERWATER_WALK_MULTIPLIER;
	private static final ForgeConfigSpec.DoubleValue SHALLOW_WATER_WALK_MULTIPLIER;
	private static final ForgeConfigSpec.DoubleValue SPRINT_MULTIPLIER;
	private static final ForgeConfigSpec.DoubleValue BLOCK_MINING_MULTIPLIER;

	public static class Combat {
		public static int shieldDelay;
		public static boolean disableCrits;
		public static boolean disableSweepAttacks;
		public static double bowInaccuracy;
		public static double damageVariation;
		public static double minDamage;
		public static double armorPointReductionFactor;
		public static double armorPointReductionFactorEntity;
		public static double armorDurabilityDamageFactor;
	}

	public static class Tools {
		public static double durabilitySaveChance;
		public static double unbreakingMultiplier;
		public static ResourceLocation soulboundEnchant;
	}

	public static class Campfire {
		public static double healRadius;
		public static double healRadiusSignal;
	}

	public static class Structures {
		public static int minDistance;
	}

	public static class Hardcore {
		public static int respawnRadius;
	}

	public static class Exhaustion {
		public static double globalMul;
		public static double damageTypeMul;
		public static double jumpMul;
		public static double swimMul;
		public static double underwaterWalkMul;
		public static double shallowWaterWalkMul;
		public static double sprintMul;
		public static double blockMiningMul;
	}

	private static void updateConfig() {
		Combat.shieldDelay = SHIELD_DELAY.get();
		Combat.disableCrits = DISABLE_CRITS.get();
		Combat.disableSweepAttacks = DISABLE_SWEEP_ATTACKS.get();
		Combat.bowInaccuracy = BOW_INACCURACY.get();
		Combat.damageVariation = RANDOM_DAMAGE_VARIATION.get() / 100d;
		Combat.minDamage = MIN_DAMAGE_TAKEN.get();
		Combat.armorPointReductionFactor = ARMOR_POINT_REDUCTION_FACTOR.get();
		Combat.armorPointReductionFactorEntity = ARMOR_POINT_REDUCTION_FACTOR_ENTITY.get();
		Combat.armorDurabilityDamageFactor = ARMOR_DURABILITY_DAMAGE_FACTOR.get();

		Campfire.healRadius = CAMPFIRE_HEAL_RADIUS.get();
		Campfire.healRadiusSignal = CAMPFIRE_HEAL_RADIUS_SIGNAL.get();

		Tools.durabilitySaveChance = DURABILITY_SAVE_CHANCE.get();
		Tools.unbreakingMultiplier = UNBREAKNG_DURABILITY_MULTIPLIER.get();
		Tools.soulboundEnchant = ResourceLocation.parse(SOULBOUND_FOR_CURIOS.get());

		Structures.minDistance = MIN_STRUCTURE_DISTANCE.get();

		Hardcore.respawnRadius = HARDCORE_RESPAW_RADIUS.get();

		Exhaustion.globalMul = GLOBAL_MULTIPLIER.get();
		Exhaustion.damageTypeMul = DAMAGE_TYPE_MULTIPLIER.get();
		Exhaustion.jumpMul = JUMP_MULTIPLIER.get();
		Exhaustion.swimMul = SWIM_MULTIPLIER.get();
		Exhaustion.underwaterWalkMul = UNDERWATER_WALK_MULTIPLIER.get();
		Exhaustion.shallowWaterWalkMul = SHALLOW_WATER_WALK_MULTIPLIER.get();
		Exhaustion.sprintMul = SPRINT_MULTIPLIER.get();
		Exhaustion.blockMiningMul = BLOCK_MINING_MULTIPLIER.get();
	}

	@SubscribeEvent
	static void onLoad(ModConfigEvent.Loading event) {
		updateConfig();
	}

	@SubscribeEvent
	static void onReload(ModConfigEvent.Reloading event) {
		updateConfig();
	}

	static {

		HARDCORE_RESPAW_RADIUS = BUILDER.defineInRange("HARDCORE_RESPAWN_RADIUS", 5000, 0, Integer.MAX_VALUE);

		BUILDER.comment("Structures").push("structures");

		MIN_STRUCTURE_DISTANCE = BUILDER
				.comment("Minimum distance between structures")
				.defineInRange("MIN_STRUCTURE_DISTANCE", 32, 1, 256);

		BUILDER.pop();

		BUILDER.comment("Tools").push("tools");

		DURABILITY_SAVE_CHANCE = BUILDER
				.comment("Chance (0 - 1) to prevent durability loss. Default: 0.5 (50%)")
				.comment("Set to 0 to disable or 1 to make all items unbreakable")
				.defineInRange("DURABILITY_SAVE_CHANCE", 0.5, 0, 1);

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
				.defineInRange("RANDOM_DAMAGE_VARIATION", 15d, 0d, 100d);

		ARMOR_POINT_REDUCTION_FACTOR = BUILDER
				.comment("How many armor points for damage to get reduced by 1 (for players)")
				.defineInRange("ARMOR_POINT_REDUCTION_FACTOR", 2d, 0d, Double.MAX_VALUE);

		ARMOR_POINT_REDUCTION_FACTOR_ENTITY = BUILDER
				.comment("How many armor points for damage to get reduced by 1 (for entities)")
				.defineInRange("ARMOR_POINT_REDUCTION_FACTOR_ENTITY", 2d, 0d, Double.MAX_VALUE);

		MIN_DAMAGE_TAKEN = BUILDER
				.comment("Minimum damage dealt by an attack after all forms of reductions")
				.defineInRange("MIN_DAMAGE_TAKEN", 0.2d, 0d, 100d);

		ARMOR_DURABILITY_DAMAGE_FACTOR = BUILDER
				.comment("How many damage points per 1 durability lost on block")
				.defineInRange("ARMOR_DURABILITY_DAMAGE_FACTOR", 4d, 1d, 100d);

		BUILDER.pop();

		BUILDER.comment("Campfires").push("campfire");

		CAMPFIRE_HEAL_RADIUS = BUILDER
				.comment("Radius around the Campfire where players get the effect")
				.defineInRange("CAMPFIRE_HEAL_RADIUS", 16.0D, 0, 64);

		CAMPFIRE_HEAL_RADIUS_SIGNAL = BUILDER
				.comment("Radius around a signal Campfire")
				.defineInRange("CAMPFIRE_HEAL_RADIUS_SIGNAL", 32.0D, 0, 64);

		BUILDER.pop();

		BUILDER.push("exhaustion_options");

		GLOBAL_MULTIPLIER = BUILDER
				.comment("Global exhaustion multiplier")
				.defineInRange("GLOBAL_MULTIPLIER", 0.5F, 0.0F, 10.0F);

		DAMAGE_TYPE_MULTIPLIER = BUILDER
				.comment("DamageSource exhaustion type multiplier")
				.defineInRange("DAMAGE_TYPE_MULTIPLIER", 2.2F, 0.0F, 10.0F);

		JUMP_MULTIPLIER = BUILDER
				.comment("Jump multiplier")
				.defineInRange("JUMP_MULTIPLIER", 1.4F, 0.0F, 10.0F);

		SWIM_MULTIPLIER = BUILDER
				.comment("Swimming exhaustion multiplier")
				.defineInRange("SWIM_MULTIPLIER", 2.0F, 0.0F, 10.0F);

		UNDERWATER_WALK_MULTIPLIER = BUILDER
				.comment("Underwater walking exhaustion multiplier")
				.defineInRange("UNDERWATER_WALK_MULTIPLIER", 1.5F, 0.0F, 10.0F);

		SHALLOW_WATER_WALK_MULTIPLIER = BUILDER
				.comment("Shallow water walking exhaustion multiplier")
				.defineInRange("SHALLOW_WATER_WALK_MULTIPLIER", 1.5F, 0.0F, 10.0F);

		SPRINT_MULTIPLIER = BUILDER
				.comment("Sprinting exhaustion multiplier")
				.defineInRange("SPRINT_MULTIPLIER", 0.64F, 0.0F, 10.0F);

		BLOCK_MINING_MULTIPLIER = BUILDER
				.comment("Block mining exhaustion multiplier")
				.defineInRange("BLOCK_MINING_MULTIPLIER", 0.66F, 0.0F, 10.0F);

		BUILDER.pop();

		SPEC = BUILDER.build();
	}
}
