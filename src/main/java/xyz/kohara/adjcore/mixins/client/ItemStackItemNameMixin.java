package xyz.kohara.adjcore.mixins.client;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.kohara.adjcore.client.misc.events.ItemIsLockedRenderCheckEvent;

@Mixin(ItemStack.class)
public class ItemStackItemNameMixin {

	@Inject(method = "getHoverName", at = @At(value = "RETURN"), cancellable = true)
	private void obfuscateNameIfNeeded(CallbackInfoReturnable<Component> cir) {
		var itemStack = (ItemStack) (Object) this;
		cir.setReturnValue(ItemIsLockedRenderCheckEvent.getItemName(itemStack, cir.getReturnValue()));
	}
}
