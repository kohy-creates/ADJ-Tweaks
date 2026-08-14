package xyz.kohara.adjcore.mixins.compat.ars;

import com.hollingsworth.arsnouveau.common.spell.effect.EffectHeal;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = EffectHeal.class, remap = false)
public class HealGlyphMixin {

	@WrapOperation(
			method = "onResolveEntity",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/LivingEntity;heal(F)V",
					remap = true
			),
			remap = false
	)
	private void healWithSource(LivingEntity instance, float healAmount, Operation<Void> original, @Local(name = "shooter") LivingEntity shooter) {
		instance.adjcore$heal(healAmount, shooter, "healSpell", true, false);
	}
}
