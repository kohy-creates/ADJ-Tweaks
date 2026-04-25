package xyz.kohara.adjcore.client.music;

import com.mojang.blaze3d.audio.Channel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import oshi.util.tuples.Pair;
import xyz.kohara.adjcore.registry.ADJAttributes;

import java.util.HashSet;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
public class ADJMusicManager {

    private static ADJMusicManager INSTANCE;

    public static ADJMusicManager getInstance() {
        return INSTANCE;
    }

    public ADJMusicManager() {
        INSTANCE = this;
    }

    private Pair<Music, SoundInstance> currentlyPlaying = new Pair<>(null, null);

    public Music getCurrentlyPlayingMusic() {
        return this.currentlyPlaying.getA();
    }

    public SoundInstance getCurrentlyPlayingInstance() {
        return this.currentlyPlaying.getB();
    }

    public void setCurrentlyPlaying(Music music, SoundInstance soundInstance) {
        if (music != null) System.out.println("Playing music " + music.getEvent().get().getLocation());
        this.currentlyPlaying = new Pair<>(music, soundInstance);
        this.MINECRAFT.getMusicManager().currentMusic = soundInstance;
    }

    private final Minecraft MINECRAFT = Minecraft.getInstance();
    private final RandomSource RANDOM = RandomSource.create();

    private double nextSongDelay;
    public final Set<Channel> musicBlockChannels = new HashSet<>();
    private float musicVolumeMultiplier = 1f;

    private boolean isStopFading = false;


    public void onMusicManagerTick() {
        Music music = ADJMusicPlayer.findMusic(MINECRAFT.getMusicManager());
        if (music == null) return;
        if (getCurrentlyPlayingMusic() != null) {
            if (!music.getEvent().value().getLocation().equals(getCurrentlyPlayingInstance().getLocation())
                    && music.replaceCurrentMusic()) {
                MINECRAFT.getSoundManager().stop(getCurrentlyPlayingInstance());
                nextSongDelay = Mth.nextInt(RANDOM, 0, music.getMinDelay() / 2);
            }


            if (!MINECRAFT.getSoundManager().isActive(getCurrentlyPlayingInstance())) {
                setCurrentlyPlaying(null, null);
                nextSongDelay = Math.min(nextSongDelay, Mth.nextInt(RANDOM, music.getMinDelay(), music.getMaxDelay()));
            }
        }

        nextSongDelay = Math.min(nextSongDelay, music.getMaxDelay());
        if (getCurrentlyPlayingMusic() == null && nextSongDelay-- <= 0) {
            startPlaying(music);
        }

        // Fade outs
        float newVolumeMultiplier = 1f;
        if (MINECRAFT.player != null || isStopFading) {
            // Allow fade-out to continue, but prevent new fade triggers
            if (!isStopFading) {
                musicVolumeMultiplier = 1f;
            }
            float targetMultiplier = shouldFadeMusic() ? 0f : 1f;
            if (targetMultiplier != musicVolumeMultiplier) {
                float volumeChange = targetMultiplier - musicVolumeMultiplier;
                int FADE_IN_TICKS = 40;
                int FADE_OUT_TICKS = 80;
                volumeChange
                        = volumeChange > 0f ? Math.min(volumeChange, 1f / FADE_IN_TICKS)
                        : Math.max(volumeChange, -1f / FADE_OUT_TICKS);
                newVolumeMultiplier = musicVolumeMultiplier + volumeChange;
            } else newVolumeMultiplier = targetMultiplier;
        }

        if (newVolumeMultiplier != musicVolumeMultiplier) {
            updateMusicVolume(newVolumeMultiplier);
        }
    }

    public void startPlaying(Music music) {
        float pitch = 1.0F;
        isStopFading = false;
        musicVolumeMultiplier = 1f;
        if (MINECRAFT.player != null) {
            var attribute = MINECRAFT.player.getAttribute(ADJAttributes.MUSIC_PITCH.get());
            if (attribute != null) {
                pitch = (float) attribute.getValue();
            }
        }

        var soundEvent = music.getEvent().value();
        var current = new SimpleSoundInstance(
                soundEvent.getLocation(), SoundSource.MUSIC,
                1.0F, pitch,
                SoundInstance.createUnseededRandom(),
                false, 0,
                SoundInstance.Attenuation.NONE,
                0.0F, 0.0F, 0.0F, true
        );
        if (current.getSound() != SoundManager.EMPTY_SOUND) {
            MINECRAFT.getSoundManager().play(current);
            setCurrentlyPlaying(music, current);
        }
        nextSongDelay = Integer.MAX_VALUE;
    }

    public void onMusicManagerStopPlaying() {
        if (getCurrentlyPlayingMusic() != null) {
            isStopFading = true;
        }
    }

    private void updateMusicVolume(float volume) {
        this.musicVolumeMultiplier = volume;
        // Apparently this is needed
        MINECRAFT.getSoundManager().updateSourceVolume(SoundSource.MUSIC, 1f);
    }

    /**
     * This is already only called for SoundSource.MUSIC type sounds.
     */
    public float calculateMusicVolume(float baseVolume, float musicSourceVolume) {
        if (this.musicVolumeMultiplier != 1f) {
            // Note: very tiny non-zero min value to stop this Minecraft
            // version from automatically stopping the sound completely
            if (this.musicVolumeMultiplier <= 0.001f && isStopFading) {
                if (getCurrentlyPlayingInstance() != null) {
                    MINECRAFT.getSoundManager().stop(getCurrentlyPlayingInstance());
                }
                setCurrentlyPlaying(null, null);
                isStopFading = false;
                return 0.00001f;
            }
            return Mth.clamp(baseVolume * musicSourceVolume * this.musicVolumeMultiplier, 0.00001f, 1f);
        }
        return Mth.clamp(baseVolume * musicSourceVolume, 0.00001f, 1f);
    }

    private boolean shouldFadeMusic() {
        Vec3 listenerPos = MINECRAFT.gameRenderer.getMainCamera().getPosition();
        for (Channel channel : musicBlockChannels) {
            if (!channel.playing()) continue;
            var mixinAccessor = (SoundChannelMixinAccessor) channel;
            var pos = mixinAccessor.adj$getPos();
            if (pos != null && listenerPos.distanceTo(pos) < 0.95f * 64f) {
                return true;
            }
        }
        // Only fade from stop request if something is currently playing
        return isStopFading && getCurrentlyPlayingInstance() != null;
    }
}
