package xyz.kohara.adjcore.mixins;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.kohara.adjcore.Config;

@Mixin(DamageSource.class)
public class DamageSourceMixin {

	@Redirect(
			method = "getFoodExhaustion",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/damagesource/DamageType;exhaustion()F"
			)
	)
	private float redirectExhaustion(DamageType damageType) {
		float original = damageType.exhaustion();
		float multiplier = (float) Config.Exhaustion.damageTypeMul;
		return original * multiplier;
	}
}
