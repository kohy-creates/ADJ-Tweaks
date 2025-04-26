package xyz.kohara.adjtweaks.combat;

import net.minecraft.entity.passive.VillagerEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class DamageControl {
    @SubscribeEvent
    public void onLivingHurtEvent(LivingHurtEvent event) {
        if (
                event.getEntity() instanceof VillagerEntity
                        && event.getSource().isIn(ModDamageTypeTags.VILLAGER_IMMUNE)
        ) {
            event.setCanceled(true);
        }
    }
}
