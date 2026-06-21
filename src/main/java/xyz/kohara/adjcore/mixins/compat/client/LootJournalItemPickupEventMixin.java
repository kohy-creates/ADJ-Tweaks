package xyz.kohara.adjcore.mixins.compat.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.obscuria.lootjournal.client.events.ItemPickupEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import xyz.kohara.adjcore.client.misc.events.ItemIsLockedRenderCheckEvent;

@Mixin(value = ItemPickupEvent.class, remap = false)
public abstract class LootJournalItemPickupEventMixin {

	@Shadow
	@Final
	private ItemStack stack;

	@WrapOperation(method = "updateDisplayName", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/Component;m_237110_(Ljava/lang/String;[Ljava/lang/Object;)Lnet/minecraft/network/chat/MutableComponent;"))
	private MutableComponent updateDisplayName(String s, Object[] objects, Operation<MutableComponent> original) {
		Object[] object = {ItemIsLockedRenderCheckEvent.getItemName(this.stack, this.stack.getHoverName())};
		return original.call(s, object);
	}
}
