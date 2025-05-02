package xyz.kohara.adjtweaks.potions;


import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.NotNull;

public class MobEffectEditable extends MobEffect {

    public MobEffectEditable(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public @NotNull MobEffect addAttributeModifier(@NotNull Attribute pAttribute, @NotNull String pUuid, double pAmount, AttributeModifier.@NotNull Operation pOperation) {
        return super.addAttributeModifier(pAttribute, pUuid, pAmount, pOperation);
    }
}
