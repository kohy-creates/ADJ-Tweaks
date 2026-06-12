package xyz.kohara.adjcore.mixins.compat.botania;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.botania.common.impl.mana.ManaItemHandlerImpl;

import java.util.Collections;
import java.util.List;

@Mixin(value = ManaItemHandlerImpl.class, remap = false)
public class ManaInventoryMixin {
	@Inject(method = "getManaItems", at = @At("HEAD"), cancellable = true)
	private void onGetManaItems(Player player, CallbackInfoReturnable<List<ItemStack>> cir) {
		cir.setReturnValue(Collections.emptyList());
	}
}
