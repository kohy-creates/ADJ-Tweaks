package xyz.kohara.adjtweaks.combat;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import xyz.kohara.adjtweaks.ADJTweaks;

public class ModDamageTypeTags {
    public static final TagKey<DamageType> MELEE = TagKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(ADJTweaks.MOD_ID, "melee"));
    public static final TagKey<DamageType> PLAYER_MELEE = TagKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(ADJTweaks.MOD_ID, "player_melee"));
    // public static final TagKey<DamageType> MOB_MELEE = TagKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(ADJTweaks.MOD_ID, "mob_melee"));
    // public static final TagKey<DamageType> DOT = TagKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(ADJTweaks.MOD_ID, "dot"));

    public static final TagKey<DamageType> VILLAGER_IMMUNE = TagKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(ADJTweaks.MOD_ID, "immunity/villager"));

    public static final TagKey<DamageType> IGNORES_COOLDOWN = TagKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(ADJTweaks.MOD_ID, "bypasses_cooldown"));
}
