package xyz.kohara.adjcore.mixins.compat.twilightforest;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.tags.DamageTypeTags;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import twilightforest.events.CapabilityEvents;

@Mixin(value = CapabilityEvents.class, remap = false)
public class CapabilityEventsMixin {

	@ModifyExpressionValue(
			method = "livingAttack",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSource;is(Lnet/minecraft/tags/TagKey;)Z")
	)
	private static boolean modifyShieldTags(boolean original, @Local(argsOnly = true) LivingAttackEvent event) {
		return original && !event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY);
	}
}
