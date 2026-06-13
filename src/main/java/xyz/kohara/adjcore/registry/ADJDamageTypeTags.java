package xyz.kohara.adjcore.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import xyz.kohara.adjcore.ADJCore;

public class ADJDamageTypeTags {
	public static final TagKey<DamageType> MELEE = TagKey.create(Registries.DAMAGE_TYPE, ADJCore.of("melee"));
	public static final TagKey<DamageType> PLAYER_MELEE = TagKey.create(Registries.DAMAGE_TYPE, ADJCore.of("player_melee"));
	// public static final TagKey<DamageType> MOB_MELEE = TagKey.create(Registries.DAMAGE_TYPE, ADJCore.of( "mob_melee"));
	// public static final TagKey<DamageType> DOT = TagKey.create(Registries.DAMAGE_TYPE, ADJCore.of( "dot"));

	public static final TagKey<DamageType> IGNORES_COOLDOWN = TagKey.create(Registries.DAMAGE_TYPE, ADJCore.of("bypasses_cooldown"));

	public static final TagKey<DamageType> IS_ENVIRONMENTAL = TagKey.create(Registries.DAMAGE_TYPE, ADJCore.of("is_environmental"));
	public static final TagKey<DamageType> IS_PHYSICAL = TagKey.create(Registries.DAMAGE_TYPE, ADJCore.of("is_physical"));

	public static final TagKey<DamageType> NO_HURT_BOB = TagKey.create(Registries.DAMAGE_TYPE, ADJCore.of("no_hurt_bob"));
}
