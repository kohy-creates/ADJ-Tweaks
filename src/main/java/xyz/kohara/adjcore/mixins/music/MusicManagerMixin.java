package xyz.kohara.adjcore.mixins.music;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.kohara.adjcore.ADJCore;

import javax.annotation.Nullable;

@Mixin(value = MusicManager.class, priority = 90000)
public abstract class MusicManagerMixin {

    @Shadow
    @Final
    private Minecraft minecraft;
    @Shadow
    @Nullable
    private SoundInstance currentMusic;

    @Shadow private int nextSongDelay;
    @Shadow @Final private RandomSource random;

    @Shadow public abstract void startPlaying(Music selector);

    // Custom field to track volume from 1.0 down to 0 and back
    @Unique
    private float adj$fadeVolume = 1.0f;

    // True when fading out, false when fading in or idle
    @Unique
    private boolean adj$fadingOut = false;

    @Unique
    private double adj$goalVolume;

    @Inject(method = "tick()V", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        ci.cancel();
        float baseVolume = minecraft.options.getSoundSourceVolume(SoundSource.MUSIC);
        // LOGGER.info("Tick called. currentMusic: {}, adj$fadingOut: {}, adj$fadeVolume: {}, baseVolume: {}", currentMusic, adj$fadingOut, adj$fadeVolume, baseVolume);
        if (adj$fadeVolume == 1.0F) adj$goalVolume = baseVolume;
        if (currentMusic != null && adj$fadingOut) {
            adj$fadeVolume -= (float) (0.025f * adj$goalVolume); // fade out step
            // LOGGER.info("Fading out. New adj$fadeVolume: {}, adj$goalVolume: {}", adj$fadeVolume, adj$goalVolume);
            if (adj$fadeVolume <= 0f) {
                adj$fadeVolume = 0f;
                adj$fadingOut = false;
                // LOGGER.info("Fade out complete, stopping music.");

                SoundManager manager = minecraft.getSoundManager();
                manager.stop(currentMusic);
                currentMusic = null;
            }
            adj$setMusicVolume(adj$fadeVolume);
        } else if (currentMusic != null && adj$fadeVolume < adj$goalVolume) {
            adj$fadeVolume += (float) (0.025f * adj$goalVolume);
            if (adj$fadeVolume > adj$goalVolume) adj$fadeVolume = (float) adj$goalVolume;
            // LOGGER.info("Fading in. New adj$fadeVolume: {}, adj$goalVolume: {}", adj$fadeVolume, adj$goalVolume);
            adj$setMusicVolume(adj$fadeVolume);
        } else if (currentMusic == null && adj$fadingOut) {
            adj$fadeVolume -= (float) (0.025f * adj$goalVolume); // fade out step
            // LOGGER.info("Fading out. New adj$fadeVolume: {}, adj$goalVolume: {}", adj$fadeVolume, adj$goalVolume);
            if (adj$fadeVolume <= 0f) {
                adj$fadeVolume = 0f;
                adj$fadingOut = false;
                // LOGGER.info("Fade out complete, stopping music.");

                SoundManager manager = minecraft.getSoundManager();
                manager.stop(currentMusic);
                currentMusic = null;
            }
            adj$setMusicVolume(adj$fadeVolume);
        }

        Music music = this.minecraft.getSituationalMusic();
        if (this.currentMusic != null) {
            if (!music.getEvent().value().getLocation().equals(this.currentMusic.getLocation()) && music.replaceCurrentMusic()) {
                this.minecraft.getSoundManager().stop(this.currentMusic);
                this.nextSongDelay = Mth.nextInt(this.random, 0, music.getMinDelay() / 2);
            }

            if (!this.minecraft.getSoundManager().isActive(this.currentMusic)) {
                this.currentMusic = null;
                this.nextSongDelay = Math.min(this.nextSongDelay, Mth.nextInt(this.random, music.getMinDelay(), music.getMaxDelay()));
            }
        }

        this.nextSongDelay = Math.min(this.nextSongDelay, music.getMaxDelay());
        if (this.currentMusic == null && this.nextSongDelay-- <= 0) {
            this.startPlaying(music);
        }
    }

    @Inject(method = "stopPlaying()V", at = @At("HEAD"), cancellable = true)
    private void onStopPlaying(CallbackInfo ci) {
        if (currentMusic != null && !adj$fadingOut) {
            ci.cancel();
            adj$fadingOut = true;
            ADJCore.LOGGER.info("Cancelling stopPlaying and starting fade out.");
        }
    }

    @Unique
    private void adj$setMusicVolume(float volume) {
        minecraft.options.getSoundSourceOptionInstance(SoundSource.MUSIC).set((double) volume);
    }
}
