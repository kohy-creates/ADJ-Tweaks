package xyz.kohara.adjcore.mixins.music;

import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundManager.class)
public class SoundManagerMixin {

    @Shadow @Final private SoundEngine soundEngine;

    @Inject(method = "tick", at = @At("HEAD"))
    public void alwaysTick(boolean isGamePaused, CallbackInfo ci) {
        this.soundEngine.tick(false);
    }
}
