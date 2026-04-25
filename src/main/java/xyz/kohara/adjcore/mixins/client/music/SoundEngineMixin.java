package xyz.kohara.adjcore.mixins.client.music;

import com.google.common.collect.Multimap;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.audio.Channel;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.sounds.SoundSource;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.kohara.adjcore.client.music.ADJMusicManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(SoundEngine.class)
public abstract class SoundEngineMixin {

    @Shadow
    @Final
    private Map<SoundInstance, ChannelAccess.ChannelHandle> instanceToChannel;

    @Shadow
    private boolean loaded;

    @Shadow
    @Final
    private ChannelAccess channelAccess;

    @Shadow
    @Final
    private Multimap<SoundSource, SoundInstance> instanceBySource;

    @Shadow
    public abstract float getVolume(@Nullable SoundSource category);

    @Inject(method = "pause", at = @At("HEAD"), cancellable = true)
    private void pauseAllExceptMusic(CallbackInfo ci) {
        ci.cancel();
        if (this.loaded) {
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

            this.channelAccess.executeOnChannels(stream -> stream.forEach(channel -> {
                if (!channelsToSkip.contains(channel)) {
                    channel.pause();
                }
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
}
