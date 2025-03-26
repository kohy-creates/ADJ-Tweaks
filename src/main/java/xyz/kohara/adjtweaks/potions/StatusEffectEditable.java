package xyz.kohara.adjtweaks.potions;

import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import org.jetbrains.annotations.NotNull;

public class StatusEffectEditable extends StatusEffect {

    public StatusEffectEditable(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public @NotNull StatusEffect addAttributeModifier(@NotNull EntityAttribute pAttribute, @NotNull String pUuid, double pAmount, EntityAttributeModifier.@NotNull Operation pOperation) {
        return super.addAttributeModifier(pAttribute, pUuid, pAmount, pOperation);
    }
}
