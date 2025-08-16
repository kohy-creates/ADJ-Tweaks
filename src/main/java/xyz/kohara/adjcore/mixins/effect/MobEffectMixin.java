package xyz.kohara.adjcore.mixins.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(MobEffect.class)
public abstract class MobEffectMixin {

    @Shadow
    public abstract void applyEffectTick(LivingEntity livingEntity, int amplifier);

    @Inject(method = "applyEffectTick", at = @At("HEAD"), cancellable = true)
    public void adj$applyEffectTick(LivingEntity livingEntity, int amplifier, CallbackInfo ci) {
        ci.cancel();
        MobEffect effect = (MobEffect) (Object) this;
        if (effect == MobEffects.REGENERATION) {
            if (livingEntity.getHealth() < livingEntity.getMaxHealth()) {
                livingEntity.heal(1.0F);
            }
        } else if (effect == MobEffects.POISON) {
            // 8 damage per tick on entities
            // and 3 on players
            float amount = (livingEntity instanceof Player) ? 3f : 5f;
            if (livingEntity.getHealth() > amount * 2) {
                livingEntity.hurt(livingEntity.damageSources().magic(), amount);
            }
        } else if (effect == MobEffects.WITHER) {
            // Wither does 6 + 0.5% of mob's health per tick
            float amount = 6F + livingEntity.getMaxHealth() * 0.005f;
            livingEntity.hurt(livingEntity.damageSources().wither(), amount);
        } else if (effect == MobEffects.HUNGER && livingEntity instanceof Player) {
            ((Player) livingEntity).causeFoodExhaustion(0.005F * (amplifier + 1));
        } else if (effect == MobEffects.SATURATION && livingEntity instanceof Player) {
            if (!livingEntity.level().isClientSide) {
                ((Player) livingEntity).getFoodData().eat(amplifier + 1, 1.0F);
            }
        } else if ((effect != MobEffects.HEAL || livingEntity.isInvertedHealAndHarm()) && (effect != MobEffects.HARM || !livingEntity.isInvertedHealAndHarm())) {
            if (effect == MobEffects.HARM && !livingEntity.isInvertedHealAndHarm() || effect == MobEffects.HEAL && livingEntity.isInvertedHealAndHarm()) {
                int damage = 80 + 40 * amplifier;
                livingEntity.hurt(livingEntity.damageSources().magic(), damage);
            }
        } else {
            int heal = 50 * (amplifier + 1);
            livingEntity.heal(Math.max(heal, 0));
        }
    }

    @Inject(method = "applyInstantenousEffect", at = @At("HEAD"), cancellable = true)
    public void adj$applyInstantenousEffect(Entity source, Entity indirectSource, LivingEntity livingEntity, int amplifier, double health, CallbackInfo ci) {
        ci.cancel();
        MobEffect effect = (MobEffect) (Object) this;

        if ((effect != MobEffects.HEAL || livingEntity.isInvertedHealAndHarm()) &&
                (effect != MobEffects.HARM || !livingEntity.isInvertedHealAndHarm())) {

            // Case: effect is HARM (normal) OR HEAL (inverted) -> DAMAGE
            if ((effect == MobEffects.HARM && !livingEntity.isInvertedHealAndHarm()) ||
                    (effect == MobEffects.HEAL && livingEntity.isInvertedHealAndHarm())) {

                int damage = 80 + 40 * amplifier;

                if (source == null) {
                    livingEntity.hurt(livingEntity.damageSources().magic(), damage);
                } else {
                    livingEntity.hurt(livingEntity.damageSources().indirectMagic(source, indirectSource), damage);
                }

            } else {
                this.applyEffectTick(livingEntity, amplifier);
            }

        } else {
            // Case: effect is HEAL (normal) OR HARM (inverted) -> HEAL
            int heal = 50 * (amplifier + 1);
            livingEntity.heal(heal);
        }
    }


    @Inject(method = "isDurationEffectTick", at = @At("HEAD"), cancellable = true)
    public void adj$isDurationEffectTick(int duration, int amplifier, CallbackInfoReturnable<Boolean> cir) {
        cir.cancel();
        MobEffect effect = (MobEffect) (Object) this;
        if (effect == MobEffects.REGENERATION) {
            int k = (int) Math.round(10 * Math.pow(0.7, amplifier));
            cir.setReturnValue(k <= 0 || duration % k == 0);
        } else if (effect == MobEffects.POISON) {
            int j = 30 >> amplifier;
            cir.setReturnValue(j <= 0 || duration % j == 0);
        } else if (effect == MobEffects.WITHER) {
            int i = 40 >> amplifier;
            cir.setReturnValue(i <= 0 || duration % i == 0);
        } else {
            cir.setReturnValue(effect == MobEffects.HUNGER);
        }
    }
}
