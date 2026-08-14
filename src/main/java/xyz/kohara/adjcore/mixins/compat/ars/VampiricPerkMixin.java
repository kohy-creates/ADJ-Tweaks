package xyz.kohara.adjcore.mixins.compat.ars;

import com.hollingsworth.arsnouveau.api.event.SpellDamageEvent;
import com.hollingsworth.arsnouveau.api.perk.PerkInstance;
import com.hollingsworth.arsnouveau.api.util.PerkUtil;
import com.hollingsworth.arsnouveau.common.perk.VampiricPerk;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = VampiricPerk.class, remap = false)
public class VampiricPerkMixin {

	@WrapOperation(
			method = "onPostSpellDamageEvent",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/LivingEntity;heal(F)V",
					remap = true
			),
			remap = false
	)
	private void wrapHeal(
			LivingEntity instance,
			float f,
			Operation<Void> original,
			@Local(argsOnly = true) SpellDamageEvent.Post event,
			@Local(argsOnly = true) PerkInstance perkInstance
	) {
		var count = PerkUtil.countForPerk(perkInstance.getPerk(), event.caster);
		event.damage = event.damage - (event.damage * (0.10f * count));
		var heal = event.damage * (0.1f * count);
		instance.adjcore$heal(heal, null, "vampiricPerk", true, false);
	}
}
