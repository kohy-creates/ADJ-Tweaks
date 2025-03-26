package xyz.kohara.adjtweaks.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import org.jetbrains.annotations.NotNull;

public class CozyCampfireEffect extends StatusEffect {

    public CozyCampfireEffect() {
        super(StatusEffectCategory.BENEFICIAL, 13458545);
    }

    @Override
    public void applyUpdateEffect(@NotNull LivingEntity entity, int pAmplifier) {
        if (!entity.getWorld().isClient()) {
            if (entity.age  % 80 == 0 && entity.getHealth() < entity.getMaxHealth()) {
                entity.heal(1.0F);
            }
        }
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

}
