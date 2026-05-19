package xyz.kohara.adjcore.mixins.compat.botania;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import vazkii.botania.common.item.equipment.bauble.FlugelTiaraItem;

@Mixin(value = FlugelTiaraItem.class, remap = false)
public class NukeFlugelTiaraAbilityMixin {

	// Removes Flugel Tiara dashes by making the game think the player is never sprinting
	@ModifyExpressionValue(method = "onWornTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isSprinting()Z"))
	private boolean noDashing(boolean original) {
		return false;
	}
}
