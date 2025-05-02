package xyz.kohara.adjtweaks.combat;

import net.minecraft.world.entity.npc.Villager;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class DamageControl {
    @SubscribeEvent
    public void onLivingHurtEvent(LivingHurtEvent event) {
        if (
                event.getEntity() instanceof Villager
                        && event.getSource().is(ModDamageTypeTags.VILLAGER_IMMUNE)
        ) {
            event.setCanceled(true);
        }
    }
}
