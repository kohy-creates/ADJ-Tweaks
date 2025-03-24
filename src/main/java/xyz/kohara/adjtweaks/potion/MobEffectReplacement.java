package xyz.kohara.adjtweaks.potion;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.NotNull;

public class MobEffectReplacement extends net.minecraft.world.effect.MobEffect {

    public MobEffectReplacement(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public @NotNull MobEffect addAttributeModifier(@NotNull Attribute pAttribute, @NotNull String pUuid, double pAmount, AttributeModifier.@NotNull Operation pOperation) {
        return super.addAttributeModifier(pAttribute, pUuid, pAmount, pOperation);
    }
}
