package xyz.kohara.adjcore.mixins.entity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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

    @Shadow public abstract Iterable<ItemStack> getArmorSlots();

    // Yeeting Resistance effect logic out of here
    @Inject(
            method = "getDamageAfterMagicAbsorb",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true
    )
    private void resistanceEdit(DamageSource damageSource, float damageAmount, CallbackInfoReturnable<Float> cir) {
        if (damageSource.is(DamageTypeTags.BYPASSES_EFFECTS)) {
            cir.setReturnValue(damageAmount);
        } else {
            if (damageAmount <= 0.0F) {
                cir.setReturnValue(0.0F);
            } else if (damageSource.is(DamageTypeTags.BYPASSES_ENCHANTMENTS)) {
                cir.setReturnValue(damageAmount);
            } else {
                int k = EnchantmentHelper.getDamageProtection(this.getArmorSlots(), damageSource);
                if (k > 0) {
                    damageAmount = CombatRules.getDamageAfterMagicAbsorb(damageAmount, k);
                }
                cir.setReturnValue(damageAmount);
            }
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
