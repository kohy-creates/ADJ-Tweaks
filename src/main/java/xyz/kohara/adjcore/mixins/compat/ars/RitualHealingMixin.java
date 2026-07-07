package xyz.kohara.adjcore.mixins.compat.ars;

import com.hollingsworth.arsnouveau.common.ritual.RitualHealing;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = RitualHealing.class, remap = false)
public class RitualHealingMixin {

	@WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;heal(F)V"))
	private void wrapHealing(LivingEntity instance, float f, Operation<Void> original) {
		instance.adjcore$heal(25f, null, "ritualHealing");
	}
}
