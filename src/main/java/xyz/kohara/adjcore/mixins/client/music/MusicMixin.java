package xyz.kohara.adjcore.mixins.client.music;

import net.minecraft.client.Minecraft;
import net.minecraft.client.sounds.SoundManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.kohara.adjcore.client.music.ADJMusicManager;

@Mixin(Minecraft.class)
public abstract class MusicMixin {

    @Shadow
    private volatile boolean pause;

    @Inject(method = "tick", at = @At("HEAD"))
    private void addOntoTick(CallbackInfo ci) {
        ADJMusicManager.getInstance().tick(this.pause);
    }

    @Redirect(method = "updateScreenAndTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sounds/SoundManager;stop()V"))
    private void dont(SoundManager instance) {
    }
}
