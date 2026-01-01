package xyz.kohara.adjcore.attributes;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xyz.kohara.adjcore.registry.ADJAttributes;

public class AttributeFunctions {

    @SubscribeEvent
    public void onLivingHeal(LivingHealEvent event) {
        AttributeInstance healingReduction = event.getEntity().getAttribute(ADJAttributes.HEALING_REDUCTION.get());
        if (healingReduction != null) {
            var value = healingReduction.getValue();
            var amount = event.getAmount();
            event.setAmount(
                    (float) (amount + (amount * value))
            );
        }
    }
}