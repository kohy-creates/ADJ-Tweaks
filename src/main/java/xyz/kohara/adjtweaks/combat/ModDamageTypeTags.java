package xyz.kohara.adjtweaks.combat;

import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import xyz.kohara.adjtweaks.ADJTweaks;

public class ModDamageTypeTags {
    public static final TagKey<DamageType> MELEE = TagKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(ADJTweaks.MOD_ID, "melee"));
    public static final TagKey<DamageType> DOT = TagKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(ADJTweaks.MOD_ID, "dot"));

    public static final TagKey<DamageType> IGNORES_COOLDOWN = TagKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(ADJTweaks.MOD_ID, "bypasses_cooldown"));
}
