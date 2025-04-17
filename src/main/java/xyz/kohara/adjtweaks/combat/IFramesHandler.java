package xyz.kohara.adjtweaks.combat;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xyz.kohara.adjtweaks.ADJTweaks;

@Mod.EventBusSubscriber(modid = ADJTweaks.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class IFramesHandler {

    private static int time;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLivingKnockback(LivingKnockBackEvent event) {
        LivingEntity entity = event.getEntity();

        Vec3d v = entity.getVelocity();
        double[] velocityXZ = {v.x, v.z} ;
        double speed = Math.sqrt(velocityXZ[0] * velocityXZ[0] + velocityXZ[1] * velocityXZ[1]);
        double scaleFactor = Math.max(0.1, Math.min(1.0, 1.0 - (speed / 10.0)));

        //double[] knockback = {event.getRatioX() * scaleFactor, event.getRatioZ() * scaleFactor};

        event.setStrength((float) (event.getStrength() * scaleFactor));

        System.out.println("Speed: " + speed + " | Scale Factor: " + scaleFactor);

        // Leave those here
        // This game is ultra dumb
        entity.timeUntilRegen = time * 2;
        entity.hurtTime = entity.maxHurtTime = time;
    }

    @SubscribeEvent
    public void onLivingHurtEvent(LivingHurtEvent event) {

        LivingEntity entity = event.getEntity();
        DamageSource source = event.getSource();

        if (!source.isIn(ModDamageTypeTags.IGNORES_COOLDOWN)) return;

        time = 0;
        if (source.isIn(ModDamageTypeTags.MELEE)) {
            time = 8;
        } else if (source.isOf(DamageTypes.WITHER) || source.isIn(DamageTypeTags.IS_FIRE)) {
            time = 8;
        }
        entity.timeUntilRegen = time * 2;
        entity.hurtTime = entity.maxHurtTime = time;
    }

}
