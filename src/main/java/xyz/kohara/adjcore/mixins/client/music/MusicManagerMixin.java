package xyz.kohara.adjcore.mixins.client.music;

import net.minecraft.client.sounds.MusicManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.kohara.adjcore.client.music.ADJMusicManager;

@Mixin(value = MusicManager.class, priority = Integer.MAX_VALUE)
public abstract class MusicManagerMixin {

    @Inject(method = "tick()V", at = @At("HEAD"), cancellable = true)
    private void onTick(CallbackInfo ci) {
        ci.cancel();
        ADJMusicManager.getInstance().onMusicManagerTick();
    }

    @Inject(method = "stopPlaying()V", at = @At("HEAD"), cancellable = true)
    private void onStopPlaying(CallbackInfo ci) {
        ci.cancel();
        ADJMusicManager.getInstance().onMusicManagerStopPlaying();
    }
}
