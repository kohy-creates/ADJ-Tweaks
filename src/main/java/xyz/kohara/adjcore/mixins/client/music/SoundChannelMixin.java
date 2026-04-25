package xyz.kohara.adjcore.mixins.client.music;

import com.mojang.blaze3d.audio.Channel;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.kohara.adjcore.client.music.ADJMusicManager;
import xyz.kohara.adjcore.client.music.SoundChannelMixinAccessor;

@Mixin(Channel.class)
public class SoundChannelMixin implements SoundChannelMixinAccessor {

    @Inject(method = "destroy", at = @At("HEAD"))
    private void onDestroy(CallbackInfo ci) {
        adj$removeSavedRecordChannels();
    }

    @Inject(method = "stop", at = @At("HEAD"))
    private void onStop(CallbackInfo ci) {
        adj$removeSavedRecordChannels();
    }

    @Unique
    private void adj$removeSavedRecordChannels() {
        var channel = (Channel) (Object) this;
        var jukeboxSet = ADJMusicManager.getInstance().musicBlockChannels;
        jukeboxSet.remove(channel);
    }

    @Unique
    private Vec3 adj$pos;

    @Inject(method = "setSelfPosition", at = @At("TAIL"))
    private void setAdj$pos(Vec3 pos, CallbackInfo ci) {
        this.adj$pos = pos;
    }

    @Override
    public Vec3 adj$getPos() {
        return this.adj$pos;
    }
}
