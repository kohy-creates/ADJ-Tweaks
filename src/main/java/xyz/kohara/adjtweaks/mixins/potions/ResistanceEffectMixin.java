package xyz.kohara.adjtweaks.mixins.potions;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.kohara.adjtweaks.ConfigHandler;

@Mixin(LivingEntity.class)
public abstract class ResistanceEffectMixin {

    @Inject(method = "getDamageAfterMagicAbsorb", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/entity/LivingEntity;hasEffect(Lnet/minecraft/world/effect/MobEffect;)Z", ordinal = 0), cancellable = true)
    private void resistanceEdit(DamageSource pDamageSource, float pDamageAmount, CallbackInfoReturnable<Float> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        MobEffectInstance resistanceEffect = entity.getEffect(MobEffects.DAMAGE_RESISTANCE);
        if (resistanceEffect != null) {
            float reduction = (float) (ConfigHandler.RESISTANCE_DAMAGE_REDUCTION.get() * (resistanceEffect.getAmplifier() + 1));
            cir.setReturnValue(pDamageAmount * (1.0f - reduction));
        }
    }
}
