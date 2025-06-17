package xyz.kohara.adjcore.mixins.effect;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.AttackDamageMobEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.kohara.adjcore.potions.MobEffectEditable;


@Mixin(MobEffects.class)
public class MobEffectsMixin {
    @Inject(method = "register", at = @At(value = "HEAD"), cancellable = true)
    private static void modifyEffects(int id, String key, MobEffect effect, CallbackInfoReturnable<MobEffect> cir) {
        switch (key) {
            case "weakness", "strength" -> {
                MobEffect newEffect = new MobEffectEditable(effect.getCategory(), effect.getColor());
                effect.addAttributeModifier(Attributes.ARMOR, "648D7064-6A60-4F59-8ABE-C2C23A6DD7A9", 0D, AttributeModifier.Operation.ADDITION);
                effect = newEffect;
            }
        }
        cir.setReturnValue(Registry.registerMapping(BuiltInRegistries.MOB_EFFECT, id, key, effect));
    }

//    @ModifyReturnValue(
//            method = "register",
//            at = @At(value = "INVOKE", target = "Lnet/minecraft/core/Registry;registerMapping(Lnet/minecraft/core/Registry;ILjava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;")
//    )
//    private static MobEffect changeReturn(int id, String key, MobEffect effect, Object original) {
//        if (effect instanceof AttackDamageMobEffect) {
//            return Registry.registerMapping(BuiltInRegistries.MOB_EFFECT, id, key, new MobEffectEditable(effect.getCategory(), effect.getColor()));
//        }
//        return (MobEffect) original;
//    }
}
