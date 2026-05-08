package xyz.kohara.adjcore.mixins.effect;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.kohara.adjcore.registry.ADJEffects;

@Mixin(MobEffectInstance.class)
public abstract class MobEffectInstanceMixin {

	@Shadow
	public abstract MobEffect getEffect();

	@Shadow
	@Final
	private MobEffect effect;
	@Unique
	LivingEntity adj$entity = null;

	@Inject(method = "tick", at = @At("HEAD"))
	private void getEntity(LivingEntity entity, Runnable onExpirationRunnable, CallbackInfoReturnable<Boolean> cir) {
		adj$entity = entity;
	}

	@WrapOperation(
			method = "tickDownDuration",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/effect/MobEffectInstance;mapDuration(Lit/unimi/dsi/fastutil/ints/Int2IntFunction;)I"
			)
	)
	private int tickDownFasterWithKanade(MobEffectInstance instance, Int2IntFunction mapper, Operation<Integer> original) {
		if (adj$entity == null) {
			return original.call(instance, mapper);
		}

		int reduceBy;
		if (adj$entity.hasEffect(ADJEffects.FALLEN_KANADE.get())) {
			if (!effect.isInstantenous() && !effect.isBeneficial()) {
				if (Math.random() <= 0.25) reduceBy = 2;
				else {
					reduceBy = 1;
				}
			} else {
				reduceBy = 1;
			}
		} else {
			reduceBy = 1;
		}
		return instance.mapDuration(i -> i - reduceBy);
	}
}
