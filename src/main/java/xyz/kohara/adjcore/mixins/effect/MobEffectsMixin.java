package xyz.kohara.adjcore.mixins.effect;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.kohara.adjcore.potions.MobEffectEditable;


@Mixin(MobEffects.class)
public class MobEffectsMixin {
    @Inject(method = "register", at = @At(value = "HEAD"), cancellable = true)
    private static void modifyEffects(int id, String key, MobEffect entry, CallbackInfoReturnable<MobEffect> cir) {
        switch (key) {
            case "speed" -> {
                MobEffect effect = new MobEffectEditable(entry.getCategory(), entry.getColor());
                effect.addAttributeModifier(Attributes.MOVEMENT_SPEED, "91AEAA56-376B-4498-935B-2F7F68070635", 0.1D, AttributeModifier.Operation.MULTIPLY_BASE);
                entry = effect;
            }
            case "weakness" -> {
                MobEffect effect = new MobEffectEditable(entry.getCategory(), entry.getColor());
                effect.addAttributeModifier(Attributes.ATTACK_DAMAGE, "22653B89-116E-49DC-9B6B-9971489B5BE5", -0.2D, AttributeModifier.Operation.MULTIPLY_BASE);
                effect.addAttributeModifier(Attributes.MOVEMENT_SPEED, "22653B89-116E-49DC-9B6B-9971489B5BE5", -0.1D, AttributeModifier.Operation.MULTIPLY_BASE);
                effect.addAttributeModifier(Attributes.ATTACK_SPEED, "22653B89-116E-49DC-9B6B-9971489B5BE5", -0.1D, AttributeModifier.Operation.MULTIPLY_BASE);
                entry = effect;
            }
            case "strength" -> {
                MobEffect effect = new MobEffectEditable(entry.getCategory(), entry.getColor());
                effect.addAttributeModifier(Attributes.ATTACK_DAMAGE, "648D7064-6A60-4F59-8ABE-C2C23A6DD7A9", 0.15D, AttributeModifier.Operation.MULTIPLY_BASE);
                entry = effect;
            }
            case "health_boost" -> {
                MobEffect effect = new MobEffectEditable(entry.getCategory(), entry.getColor());
                effect.addAttributeModifier(Attributes.MAX_HEALTH, "5D6F0BA2-1186-46AC-B896-C61C5CEE99CC", 0.2D, AttributeModifier.Operation.MULTIPLY_TOTAL);
                entry = effect;
            }
        }
        cir.setReturnValue(Registry.registerMapping(BuiltInRegistries.MOB_EFFECT, id, key, entry));
    }
}
