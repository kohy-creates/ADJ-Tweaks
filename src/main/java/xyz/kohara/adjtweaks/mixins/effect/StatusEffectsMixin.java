package xyz.kohara.adjtweaks.mixins.effect;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.kohara.adjtweaks.potions.StatusEffectEditable;


@Mixin(StatusEffects.class)
public class StatusEffectsMixin {
    @Inject(method = "register", at = @At(value = "HEAD"), cancellable = true)
    private static void modifyEffects(int rawId, String id, StatusEffect entry, CallbackInfoReturnable<StatusEffect> cir) {
        switch (id) {
            case "speed" -> {
                StatusEffect effect = new StatusEffectEditable(entry.getCategory(), entry.getColor());
                effect.addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED, "91AEAA56-376B-4498-935B-2F7F68070635", 0.1D, EntityAttributeModifier.Operation.MULTIPLY_BASE);
                entry = effect;
            }
            case "weakness" -> {
                StatusEffect effect = new StatusEffectEditable(entry.getCategory(), entry.getColor());
                effect.addAttributeModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE, "22653B89-116E-49DC-9B6B-9971489B5BE5", -0.2D, EntityAttributeModifier.Operation.MULTIPLY_BASE);
                effect.addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED, "22653B89-116E-49DC-9B6B-9971489B5BE5", -0.1D, EntityAttributeModifier.Operation.MULTIPLY_BASE);
                effect.addAttributeModifier(EntityAttributes.GENERIC_ATTACK_SPEED, "22653B89-116E-49DC-9B6B-9971489B5BE5", -0.1D, EntityAttributeModifier.Operation.MULTIPLY_BASE);
                entry = effect;
            }
            case "strength" -> {
                StatusEffect effect = new StatusEffectEditable(entry.getCategory(), entry.getColor());
                effect.addAttributeModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE, "648D7064-6A60-4F59-8ABE-C2C23A6DD7A9", 0.15D, EntityAttributeModifier.Operation.MULTIPLY_BASE);
                entry = effect;
            }
            case "health_boost" -> {
                StatusEffect effect = new StatusEffectEditable(entry.getCategory(), entry.getColor());
                effect.addAttributeModifier(EntityAttributes.GENERIC_MAX_HEALTH, "5D6F0BA2-1186-46AC-B896-C61C5CEE99CC", 0.2D, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
                entry = effect;
            }
        }
        cir.setReturnValue(Registry.register(Registries.STATUS_EFFECT, rawId, id, entry));
    }
}
