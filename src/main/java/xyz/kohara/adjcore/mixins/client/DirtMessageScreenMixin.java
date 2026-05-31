package xyz.kohara.adjcore.mixins.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.GenericDirtMessageScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.kohara.adjcore.registry.ADJSoundEvents;

@Mixin(GenericDirtMessageScreen.class)
public class DirtMessageScreenMixin {

	@Inject(method = "<init>", at = @At("TAIL"))
	private void playSoundOnOpen(Component arg, CallbackInfo ci) {
		Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(ADJSoundEvents.ENTER_WORLD.get(), 1.0F, 1.0F));
	}
}
