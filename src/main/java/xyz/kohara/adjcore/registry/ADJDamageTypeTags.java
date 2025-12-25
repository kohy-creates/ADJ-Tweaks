package xyz.kohara.adjcore.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import xyz.kohara.adjcore.ADJCore;

public class ADJDamageTypeTags {
    public static final TagKey<DamageType> MELEE = TagKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(ADJCore.MOD_ID, "melee"));
    public static final TagKey<DamageType> PLAYER_MELEE = TagKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(ADJCore.MOD_ID, "player_melee"));
    // public static final TagKey<DamageType> MOB_MELEE = TagKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(ADJCore.MOD_ID, "mob_melee"));
    // public static final TagKey<DamageType> DOT = TagKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(ADJCore.MOD_ID, "dot"));

    public static final TagKey<DamageType> IGNORES_COOLDOWN = TagKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(ADJCore.MOD_ID, "bypasses_cooldown"));

    public static final TagKey<DamageType> IS_ENVIRONMENTAL = TagKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(ADJCore.MOD_ID, "is_environmental"));
    public static final TagKey<DamageType> IS_PHYSICAL = TagKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(ADJCore.MOD_ID, "is_physical"));
}
