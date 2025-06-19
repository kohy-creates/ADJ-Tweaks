package xyz.kohara.adjcore.mixins.enchantments;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.kohara.adjcore.combat.ModDamageTypeTags;

@Mixin(ProtectionEnchantment.class)
public class ProtectionMixin {

    @Inject(method = "getMaxLevel", at = @At("HEAD"), cancellable = true)
    public void getMaxLevel(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(4);
    }

    @Inject(method = "checkCompatibility", at = @At("HEAD"), cancellable = true)
    private void compatibleWithEverything(Enchantment other, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }

    // Environmental Protection
    @Inject(method = "getFireAfterDampener", at = @At("HEAD"), cancellable = true)
    private static void getFireAfterDampener(LivingEntity livingEntity, int level, CallbackInfoReturnable<Integer> cir) {
        int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.BLAST_PROTECTION, livingEntity);
        if (i > 0) {
            level -= Mth.floor(level * (i * 0.15F));
        }

        cir.setReturnValue(level);
    }

    @Inject(method = "getExplosionKnockbackAfterDampener", at = @At("HEAD"), cancellable = true)
    private static void getExplosionKnockbackAfterDampener(LivingEntity livingEntity, double damage, CallbackInfoReturnable<Double> cir) {
        int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.BLAST_PROTECTION, livingEntity);
        if (i > 0) {
            damage *= Mth.clamp(1.0 - i * 0.15, 0.0, 1.0);
        }

        cir.setReturnValue(damage);
    }

    @Inject(method = "getDamageProtection", at = @At("HEAD"), cancellable = true)
    public void getDamageProtection(int level, DamageSource source, CallbackInfoReturnable<Integer> cir) {
        ProtectionEnchantment enchantment = (ProtectionEnchantment) (Object) this;
        if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            cir.setReturnValue(0);
        } else if (enchantment.type == ProtectionEnchantment.Type.ALL && source.is(ModDamageTypeTags.IS_PHYSICAL)) {
            cir.setReturnValue(level);
        } else if (enchantment.type == ProtectionEnchantment.Type.EXPLOSION && source.is(ModDamageTypeTags.IS_ENVIRONMENTAL)) {
            cir.setReturnValue(level * 2);
        } else if (enchantment.type == ProtectionEnchantment.Type.FALL && source.is(DamageTypeTags.IS_FALL)) {
            cir.setReturnValue(level * 4);
        } else {
            cir.setReturnValue(0);
        }
    }
}
