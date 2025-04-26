package xyz.kohara.adjtweaks.mixins.entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.kohara.adjtweaks.Config;
import xyz.kohara.adjtweaks.combat.KnockbackCooldown;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements KnockbackCooldown {

    @Inject(method = "modifyAppliedDamage", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/entity/LivingEntity;hasStatusEffect(Lnet/minecraft/entity/effect/StatusEffect;)Z", ordinal = 0), cancellable = true)
    private void resistanceEdit(DamageSource damageSource, float amount, CallbackInfoReturnable<Float> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        StatusEffectInstance resistanceEffect = entity.getStatusEffect(StatusEffects.RESISTANCE);
        if (resistanceEffect != null) {
            float reduction = (float) (Config.RESISTANCE_DAMAGE_REDUCTION.get() * (resistanceEffect.getAmplifier() + 1));

            float finalAmount = amount * (1.0f - reduction);
            cir.setReturnValue(finalAmount);
        }
    }

    // Removes shield use delay
    @ModifyConstant(method = "isBlocking", constant = @Constant(intValue = 5))
    private int setShieldUseDelay(int constant) {
        return Config.SHIELD_DELAY.get();
    }

    @Unique
    public int aDJTweaks$knockbackCooldown;

    @Override
    public int aDJTweaks$getKnockbackCooldown() {
        return aDJTweaks$knockbackCooldown;
    }

    @Override
    public void aDJTweaks$setKnockbackCooldown(int cooldown) {
        aDJTweaks$knockbackCooldown = cooldown;
    }

    @Inject(
            method = "tick",
            at = @At("HEAD")
    )
    private void onTick(CallbackInfo ci) {
        if (aDJTweaks$knockbackCooldown > 0) {
            aDJTweaks$knockbackCooldown--;
        }
    }
}

