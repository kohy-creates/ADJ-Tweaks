package xyz.kohara.adjcore.registry.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.NotNull;
import xyz.kohara.adjcore.registry.ADJAttributes;

public class FallenKanadeEffect extends MobEffect {

	public FallenKanadeEffect() {
		super(MobEffectCategory.BENEFICIAL, 6814833);
	}

	@Override
	public void addAttributeModifiers(@NotNull LivingEntity entity, @NotNull AttributeMap attributeMap, int amplifier) {
		this.addAttributeModifier(
				ADJAttributes.HEALTH_REGEN.get(),
				"7920bcd0-25bf-4339-a553-f33806b7a55f",
				2d,
				AttributeModifier.Operation.ADDITION
		);
		super.addAttributeModifiers(entity, attributeMap, amplifier);
	}
}
