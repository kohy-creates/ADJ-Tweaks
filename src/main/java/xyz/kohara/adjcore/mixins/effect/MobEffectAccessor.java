package xyz.kohara.adjcore.mixins.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(MobEffect.class)
public interface MobEffectAccessor {
    @Accessor("attributeModifiers")
    @Mutable
    Map<Attribute, AttributeModifier> getAttributeModifiers();

    @Accessor("attributeModifiers")
    void setAttributeModifiers(Map<Attribute, AttributeModifier> modifiers);
}