package xyz.kohara.adjcore.registry.effects;

import net.minecraft.world.effect.InstantenousMobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class LesserInstantHealthEffect extends InstantenousMobEffect {
	public LesserInstantHealthEffect() {
		super(MobEffectCategory.BENEFICIAL, 16278114);
	}

	@Override
	public void applyInstantenousEffect(@Nullable Entity source, @Nullable Entity indirectSource, @NotNull LivingEntity livingEntity, int amplifier, double health) {
		if (livingEntity.getMaxHealth() != livingEntity.getHealth()) {
			livingEntity.adjcore$heal(10 * (amplifier + 1), null, "lesserHealingPotion");
		}
	}
}
