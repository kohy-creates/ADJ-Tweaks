package xyz.kohara.adjtweaks.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class CozyCampfireEffect extends MobEffect {

    public CozyCampfireEffect() {
        super(MobEffectCategory.BENEFICIAL, 13458545);
    }

    @Override
    public void applyEffectTick(@NotNull LivingEntity pLivingEntity, int pAmplifier) {
       if (!pLivingEntity.level().isClientSide) {
           if (pLivingEntity.tickCount % 80 == 0 && pLivingEntity.getHealth() < pLivingEntity.getMaxHealth()) {
               pLivingEntity.heal(1.0F);
           }
       }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

}
