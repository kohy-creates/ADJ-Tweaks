package xyz.kohara.adjcore.mixins.music;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.audio.Channel;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(SoundEngine.class)
public class SoundEngineMixin {

    @Shadow
    @Final
    private Map<SoundInstance, ChannelAccess.ChannelHandle> instanceToChannel;

    @WrapMethod(method = "pause")
    private void pauseAllExceptMusic(Operation<Void> original) {
        for (Map.Entry<SoundInstance, ChannelAccess.ChannelHandle> entry : instanceToChannel.entrySet()) {
            if (!entry.getKey().getSource().equals(SoundSource.MUSIC)) {
                entry.getValue().execute(Channel::pause);
            }
        }
    }
}
