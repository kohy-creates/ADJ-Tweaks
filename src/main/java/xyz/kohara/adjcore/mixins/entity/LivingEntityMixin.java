package xyz.kohara.adjcore.mixins.entity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.kohara.adjcore.Config;
import xyz.kohara.adjcore.attributes.ModAttributes;
import xyz.kohara.adjcore.combat.KnockbackCooldown;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements KnockbackCooldown {

    @Inject(
            method = "getDamageAfterMagicAbsorb",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/world/entity/LivingEntity;hasEffect(Lnet/minecraft/world/effect/MobEffect;)Z",
                    ordinal = 0
            ),
            cancellable = true
    )
    private void resistanceEdit(DamageSource damageSource, float amount, CallbackInfoReturnable<Float> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        MobEffectInstance resistanceEffect = entity.getEffect(MobEffects.DAMAGE_RESISTANCE);
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
    public int adjcore$knockbackCooldown;

    @Override
    public int adjcore$getKnockbackCooldown() {
        return adjcore$knockbackCooldown;
    }

    @Override
    public void adjcore$setKnockbackCooldown(int cooldown) {
        adjcore$knockbackCooldown = cooldown;
    }

    @Inject(
            method = "tick",
            at = @At("HEAD")
    )
    private void onTick(CallbackInfo ci) {
        if (adjcore$knockbackCooldown > 0) {
            adjcore$knockbackCooldown--;
        }
    }

    @ModifyExpressionValue(
            method = "createLivingAttributes",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier;builder()Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier$Builder;")
    )
    private static AttributeSupplier.Builder modifyExpressionValueAtBuilder(AttributeSupplier.Builder original) {
        return original
                .add(ModAttributes.DAMAGE_REDUCTION.get())
                .add(ModAttributes.PROJECTILE_DAMAGE_REDUCTION.get());
    }
}
