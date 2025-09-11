package xyz.kohara.adjcore.mixins.effect;

import net.minecraft.world.effect.AbsoptionMobEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbsoptionMobEffect.class)
public class AbsorptionEffectMixin extends MobEffect  {

    protected AbsorptionEffectMixin(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Inject(method = "removeAttributeModifiers", at = @At("HEAD"), cancellable = true)
    public void removeAttributeModifiers(LivingEntity livingEntity, AttributeMap attributeMap, int amplifier, CallbackInfo ci) {
        ci.cancel();

        livingEntity.setAbsorptionAmount(livingEntity.getAbsorptionAmount() - 20 * (amplifier + 1));
        super.removeAttributeModifiers(livingEntity, attributeMap, amplifier);
    }

    @Inject(method = "addAttributeModifiers", at = @At("HEAD"), cancellable = true)
    public void addAttributeModifiers(LivingEntity livingEntity, AttributeMap attributeMap, int amplifier, CallbackInfo ci) {
        ci.cancel();

        livingEntity.setAbsorptionAmount(livingEntity.getAbsorptionAmount() + 20 * (amplifier + 1));
        super.addAttributeModifiers(livingEntity, attributeMap, amplifier);
    }
}
