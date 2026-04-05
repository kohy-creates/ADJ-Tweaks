package xyz.kohara.adjcore.mixins.entity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ZombifiedPiglin.class)
public class ZombifiedPiglinMixin {

	@Mutable
	@Shadow
	@Final
	private static UniformInt ALERT_INTERVAL;

	@ModifyExpressionValue(
			method = "alertOthers",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/monster/ZombifiedPiglin;getAttributeValue(Lnet/minecraft/world/entity/ai/attributes/Attribute;)D"
			)
	)
	private double reduceAggroRangeOnHit(double original) {
		double r = 12;
		if (((ZombifiedPiglin) (Object) this).level().getLevelData().isHardcore()) {
			r = 20;
		}
		return r;
	}

	@Inject(
			method = "<clinit>",
			at = @At("TAIL")
	)
	private static void increaseAggroInterval(CallbackInfo ci) {
		ALERT_INTERVAL = TimeUtil.rangeOfSeconds(6, 10);
	}
}
