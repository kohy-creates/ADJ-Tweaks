package xyz.kohara.adjcore.combat;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public interface HealingWithSourceEntity {

	default void adjcore$heal(float amount, @Nullable LivingEntity sourceEntity, @Nullable String reason) {
	}
}
