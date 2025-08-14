package xyz.kohara.adjcore.combat;

import dev.shadowsoffire.attributeslib.api.ALObjects;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xyz.kohara.adjcore.Config;
import xyz.kohara.adjcore.attributes.ModAttributes;

public class CombatRules {

    private static final float MIN_DAMAGE = Config.MIN_DAMAGE_TAKEN.get().floatValue();
    private static final float ARMOR_POINT_FACTOR = Config.ARMOR_POINT_REDUCTION_FACTOR.get().floatValue();
    private static final float ARMOR_POINT_FACTOR_ENTITY = Config.ARMOR_POINT_REDUCTION_FACTOR_ENTITY.get().floatValue();

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        DamageSource source = event.getSource();
        LivingEntity entity = event.getEntity();
        LivingEntity attacker = (LivingEntity) source.getEntity();
        float damage = event.getAmount();

        // Reduce the damage by the DR value
        if (source.is(DamageTypeTags.BYPASSES_RESISTANCE)) {
            damage *= 1 - getValue(entity, ModAttributes.DAMAGE_REDUCTION.get());
            if (source.is(DamageTypeTags.IS_PROJECTILE)) {
                damage *= 1 - getValue(entity, ModAttributes.PROJECTILE_DAMAGE_REDUCTION.get());
            }
        }
        // Armor points
        if (!source.is(DamageTypeTags.BYPASSES_ARMOR)) {
            int armorPoints = (int) getValue(entity, Attributes.ARMOR);
            int armorToughness = (int) getValue(entity, Attributes.ARMOR_TOUGHNESS);

            if (attacker != null) {
                int armorPierce = (int) getValue(attacker, ALObjects.Attributes.ARMOR_PIERCE.get());
                float armorShred = Math.max(1 - getValue(attacker, ALObjects.Attributes.ARMOR_SHRED.get()), 0);
                armorPierce = Math.max(0, armorPierce - armorToughness);
                armorPoints = Math.max(Math.round(armorPoints * armorShred) - armorPierce, 0);
            }

            float factor = (entity instanceof Player) ? ARMOR_POINT_FACTOR : ARMOR_POINT_FACTOR_ENTITY;
            damage = Math.max(MIN_DAMAGE, damage - (((float) armorPoints) / factor));
        }

        event.setAmount(damage);
    }

    private static float getValue(LivingEntity entity, Attribute attribute) {
        AttributeInstance instance = entity.getAttribute(attribute);
        return instance != null ? (float) instance.getValue() : 0;
    }

}
