package xyz.kohara.adjcore.mixins.client.music;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Streams;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.audio.Channel;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundEngineExecutor;
import net.minecraft.sounds.SoundSource;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.kohara.adjcore.client.music.ADJMusicManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Mixin(SoundEngine.class)
public abstract class SoundEngineMixin {

    @Shadow
    @Final
    private Map<SoundInstance, ChannelAccess.ChannelHandle> instanceToChannel;

    @Shadow
	public boolean loaded;

    @Shadow
    @Final
    private ChannelAccess channelAccess;

    @Shadow
    @Final
    private Multimap<SoundSource, SoundInstance> instanceBySource;

    @Shadow
    public abstract float getVolume(@Nullable SoundSource category);

    @Shadow
    @Final
    private SoundEngineExecutor executor;

    @Shadow
    @Final
	public Map<SoundInstance, Integer> queuedSounds;

    @Shadow
    @Final
	public List<TickableSoundInstance> tickingSounds;

    @Shadow
    @Final
	public Map<SoundInstance, Integer> soundDeleteTime;

    @Shadow
    @Final
	public List<TickableSoundInstance> queuedTickableSounds;

    @Inject(method = "pause", at = @At("HEAD"), cancellable = true)
    private void pauseAllExceptMusic(CallbackInfo ci) {
        ci.cancel();
        if (this.loaded) {
            final var channelsToSkip = adj$getChannelsToSkip();
            this.channelAccess.executeOnChannels(stream -> stream.forEach(channel -> {
                if (!channelsToSkip.contains(channel)) channel.pause();
            }));
        }
    }

    @Inject(
            method = "play",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/google/common/collect/Multimap;put(Ljava/lang/Object;Ljava/lang/Object;)Z",
                    shift = At.Shift.AFTER
            )
    )
    private void keepTrackOfJukeboxes(
            SoundInstance sound,
            CallbackInfo ci,
            @Local(name = "soundsource") SoundSource soundSource,
            @Local(name = "channelaccess$channelhandle") ChannelAccess.ChannelHandle channelaccess$channelhandle) {
        if (soundSource == SoundSource.RECORDS) {
            channelaccess$channelhandle.execute(channel -> ADJMusicManager.getInstance().musicBlockChannels.add(channel));
        }
    }

    @Inject(method = "calculateVolume(FLnet/minecraft/sounds/SoundSource;)F", at = @At("HEAD"), cancellable = true)
    private void beforeCalculateVolume(float baseVolume, SoundSource soundSource, CallbackInfoReturnable<Float> cir) {
        if (soundSource == SoundSource.MUSIC)
            cir.setReturnValue(ADJMusicManager.getInstance().calculateMusicVolume(baseVolume, getVolume(SoundSource.MUSIC)));
    }

//    @Inject(method = "stopAll", at = @At("HEAD"), cancellable = true)
//    private void stopAllButMusic(CallbackInfo ci) {
//        ci.cancel();
//        if (!this.loaded) return;
//
//        this.executor.flush();
//        // Only stop non-music stuff
//        this.instanceToChannel.forEach((instance, handle) -> {
//            if (instance.getSource() != SoundSource.MUSIC) {
//                handle.execute(Channel::stop);
//            }
//        });
//        this.instanceToChannel.entrySet().removeIf(entry ->
//                entry.getKey().getSource() != SoundSource.MUSIC
//        );
//        this.instanceBySource.entries().removeIf(entry ->
//                entry.getKey() != SoundSource.MUSIC
//        );
//        this.channelAccess.clear();
//        this.queuedSounds.clear();
//        this.tickingSounds.clear();
//        this.soundDeleteTime.clear();
//        this.queuedTickableSounds.clear();
//    }

    @Unique
    private List<Channel> adj$getChannelsToSkip() {
        final List<SoundInstance> musicInstances = new ArrayList<>();
        instanceBySource.forEach((soundSource, soundInstance) -> {
            if (soundSource.equals(SoundSource.MUSIC)) {
                musicInstances.add(soundInstance);
            }
        });

        List<Channel> channelsToSkip = new ArrayList<>();
        musicInstances.forEach(soundInstance -> {
            if (instanceToChannel.containsKey(soundInstance)) {
                instanceToChannel.get(soundInstance).execute(channelsToSkip::add);
            }
        });
        return channelsToSkip;
    }
}
