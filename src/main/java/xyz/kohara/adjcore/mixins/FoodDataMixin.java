package xyz.kohara.adjcore.mixins;

import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import xyz.kohara.adjcore.Config;

@Mixin(FoodData.class)
public class FoodDataMixin {

	@ModifyVariable(
			method = {"addExhaustion(F)V"},
			at = @At("HEAD"),
			argsOnly = true
	)
	private float modifyExhaustionAmount(float amountToAdd) {
		return (float) (amountToAdd * Config.Exhaustion.globalMul);
	}
}
