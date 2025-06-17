package xyz.kohara.adjcore.attributes;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class DamageReduction {

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        DamageSource source = event.getSource();

        if (!source.is(DamageTypeTags.BYPASSES_RESISTANCE)) {
            float damage = event.getAmount();
            LivingEntity entity = event.getEntity();

            AttributeInstance damageReduction = entity.getAttribute(ModAttributes.DAMAGE_REDUCTION.get());
            if (damageReduction != null)
                damage *= (float) (1 - damageReduction.getValue());

            if (source.is(DamageTypeTags.IS_PROJECTILE)) {
                AttributeInstance projectileDamageReduction = entity.getAttribute(ModAttributes.PROJECTILE_DAMAGE_REDUCTION.get());
                if (projectileDamageReduction != null)
                    damage *= (float) (1 - projectileDamageReduction.getValue());
            }
            event.setAmount(damage);
        }
    }
}
