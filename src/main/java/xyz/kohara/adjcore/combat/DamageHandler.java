package xyz.kohara.adjcore.combat;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class DamageHandler {

    private static int INVUL_TIME;

    public static void setInvulTime(LivingEntity entity, int time) {
        entity.invulnerableTime = time * 2;
        entity.hurtTime = entity.hurtDuration = time;
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onLivingKnockback(LivingKnockBackEvent event) {
        LivingEntity entity = event.getEntity();

        int cooldown = entity.adjcore$getKnockbackCooldown();
        if (cooldown > 0) {
            float s = event.getStrength();
            s = Math.max(0f, s - s * cooldown / 15f);
            event.setStrength(s);
        } else if (cooldown == 0) {
            if (INVUL_TIME != 0) entity.adjcore$setKnockbackCooldown(INVUL_TIME * 2);
            else entity.adjcore$setKnockbackCooldown(5);
        }
        setInvulTime(entity, INVUL_TIME);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLivingHurtEvent(LivingHurtEvent event) {

        DamageSource source = event.getSource();
        if (!source.is(ModDamageTypeTags.IGNORES_COOLDOWN)) return;

        LivingEntity entity = event.getEntity();

        INVUL_TIME = 0;
        if (source.is(ModDamageTypeTags.MELEE)) {
            if (source.is(ModDamageTypeTags.PLAYER_MELEE)) INVUL_TIME = 8;
            else INVUL_TIME = 3;
        } else if (source.is(DamageTypes.WITHER) || source.is(DamageTypeTags.IS_FIRE)) {
            INVUL_TIME = 8;
        }
        setInvulTime(entity, INVUL_TIME);
    }

}
