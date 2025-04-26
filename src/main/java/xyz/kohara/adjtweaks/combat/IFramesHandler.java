package xyz.kohara.adjtweaks.combat;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xyz.kohara.adjtweaks.ADJTweaks;

@Mod.EventBusSubscriber(modid = ADJTweaks.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class IFramesHandler {

    private static int INVUL_TIME;

    public static void setInvulTime(LivingEntity entity, int time) {
        entity.timeUntilRegen = time * 2;
        entity.hurtTime = entity.maxHurtTime = time;
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onLivingKnockback(LivingKnockBackEvent event) {
        LivingEntity entity = event.getEntity();


        int cooldown = entity.aDJTweaks$getKnockbackCooldown();
        if (cooldown > 0) {
            float s = event.getStrength();
            s = Math.max(0f, s - s * cooldown / 15f);
            event.setStrength(s);
        } else if (cooldown == 0) {
            if (INVUL_TIME != 0) entity.aDJTweaks$setKnockbackCooldown(INVUL_TIME * 2);
            else entity.aDJTweaks$setKnockbackCooldown(5);
        }
        setInvulTime(entity, INVUL_TIME);
    }

    @SubscribeEvent
    public void onLivingHurtEvent(LivingHurtEvent event) {

        DamageSource source = event.getSource();
        if (!source.isIn(ModDamageTypeTags.IGNORES_COOLDOWN)) return;

        LivingEntity entity = event.getEntity();

        INVUL_TIME = 0;
        if (source.isIn(ModDamageTypeTags.MELEE)) {
            if (source.isIn(ModDamageTypeTags.PLAYER_MELEE)) INVUL_TIME = 8;
            else INVUL_TIME = 3;
        } else if (source.isOf(DamageTypes.WITHER) || source.isIn(DamageTypeTags.IS_FIRE)) {
            INVUL_TIME = 8;
        }
        setInvulTime(entity, INVUL_TIME);
    }

}
