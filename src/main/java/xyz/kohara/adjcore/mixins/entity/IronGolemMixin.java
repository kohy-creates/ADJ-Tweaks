package xyz.kohara.adjcore.mixins.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(IronGolem.class)
public class IronGolemMixin {

	@WrapOperation(method = "mobInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/IronGolem;heal(F)V"))
	private void wrapHeal(IronGolem instance, float v, Operation<Void> original, @Local(argsOnly = true) Player player) {
		instance.adjcore$heal(250f, player, "golemHeal");
	}
}
