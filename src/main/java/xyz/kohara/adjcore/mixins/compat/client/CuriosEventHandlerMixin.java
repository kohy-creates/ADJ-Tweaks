package xyz.kohara.adjcore.mixins.compat.client;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.common.event.CuriosEventHandler;

@Mixin(value = CuriosEventHandler.class, remap = false)
public class CuriosEventHandlerMixin {

	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraftforge/common/util/LazyOptional;ifPresent(Lnet/minecraftforge/common/util/NonNullConsumer;)V"
			),
			cancellable = true
	)
	private void removeRenderLayer(LivingEvent.LivingTickEvent evt, CallbackInfo ci) {
		if (!(evt.getEntity() instanceof Player)) {
			ci.cancel();
		}
	}
}
