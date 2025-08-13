package xyz.kohara.adjcore.mixins.music;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(MusicManager.class)
public abstract class MusicManagerMixin {

    private static final Logger LOGGER = LogManager.getLogger("MusicManagerMixin");

    @Shadow @Final private Minecraft minecraft;
    @Shadow @Nullable private SoundInstance currentMusic;

    // Custom field to track volume from 1.0 down to 0 and back
    @Unique
    private float fadeVolume = 1.0f;

    // True when fading out, false when fading in or idle
    @Unique
    private boolean fadingOut = false;

    @Inject(method = "tick()V", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        float baseVolume = minecraft.options.getSoundSourceVolume(SoundSource.MUSIC);
        LOGGER.debug("Tick called. currentMusic: {}, fadingOut: {}, fadeVolume: {}, baseVolume: {}", currentMusic, fadingOut, fadeVolume, baseVolume);

        if (currentMusic != null && fadingOut) {
            fadeVolume -= 0.03f; // fade out step
            LOGGER.debug("Fading out. New fadeVolume: {}", fadeVolume);
            if (fadeVolume <= 0f) {
                fadeVolume = 0f;
                fadingOut = false;
                LOGGER.debug("Fade out complete, stopping music.");

                SoundManager manager = minecraft.getSoundManager();
                manager.stop(currentMusic);
                currentMusic = null;
            }
            setMusicVolume(fadeVolume);
        } else if (currentMusic != null && fadeVolume < 1.0f) {
            fadeVolume += 0.03f;
            if (fadeVolume > 1.0f) fadeVolume = 1.0f;
            LOGGER.debug("Fading in. New fadeVolume: {}", fadeVolume);
            setMusicVolume(fadeVolume);
        }
    }

    @Inject(method = "stopPlaying()V", at = @At("HEAD"), cancellable = true)
    private void onStopPlaying(CallbackInfo ci) {
        LOGGER.debug("stopPlaying() called. currentMusic: {}, fadingOut: {}", currentMusic, fadingOut);
        if (currentMusic != null && !fadingOut) {
            ci.cancel();
            fadingOut = true; // start fading out
            LOGGER.debug("Cancelling stopPlaying and starting fade out.");
        }
    }

    @Unique
    private void setMusicVolume(float volume) {
        LOGGER.debug("Setting music volume to {}", volume);
        minecraft.getSoundManager().updateSourceVolume(SoundSource.MUSIC, volume);
    }
}
