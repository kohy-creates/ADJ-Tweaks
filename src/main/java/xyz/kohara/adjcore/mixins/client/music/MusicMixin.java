package xyz.kohara.adjcore.mixins.client.music;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.Music;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public abstract class MusicMixin {

    @Inject(
            method = "getSituationalMusic",
            at = @At("HEAD"),
            cancellable = true
    )
    private void getSituationalMusic(CallbackInfoReturnable<Music> cir) {
//        cir.setReturnValue(ADJMusicPlayer.findMusic(this.musicManager));
    }
}
