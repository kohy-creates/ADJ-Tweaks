//package xyz.kohara.adjtweaks.mixins;
//
//
//import net.minecraft.world.damagesource.CombatRules;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//
//@Mixin(CombatRules.class)
//public class CombatRulesMixin {
//
//    @Inject(method = "getDamageAfterAbsorb", at = @At("HEAD"), cancellable = true)
//    private static void getDamageAfterAbsorb(float damage, float totalArmor, float toughnessAttribute, CallbackInfoReturnable<Float> cir) {
//
//        System.out.println(damage);
//
//        float damageReduction = toughnessAttribute / 100f;
//        System.out.println(damageReduction);
//
//        damage *= (1 - damageReduction);
//        damage -= totalArmor / 3;
//
//        System.out.println(totalArmor / 3);
//        System.out.println(damage);
//        float finalDamage = Math.max(0.01f, damage);
//
//        cir.setReturnValue(finalDamage);
//    }
//
//    @Inject(method = "getDamageAfterMagicAbsorb", at = @At("HEAD"), cancellable = true)
//    private static void getDamageAfterMagicAbsorb(float damage, float enchantModifiers, CallbackInfoReturnable<Float> cir) {
//
//        float damageReduction = enchantModifiers / 100f;
//        float finalDamage = damage * (1 - damageReduction);
//
//        cir.setReturnValue(finalDamage);
//    }
//}
