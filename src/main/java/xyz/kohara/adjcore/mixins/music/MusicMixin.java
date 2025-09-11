package xyz.kohara.adjcore.mixins.music;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.Music;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.kohara.adjcore.music.MusicPlayer;

@Mixin(Minecraft.class)
public abstract class MusicMixin {

    @Shadow
    @Final
    private MusicManager musicManager;

    @Inject(
            method = "getSituationalMusic",
            at = @At("HEAD"),
            cancellable = true
    )
    private void getSituationalMusic(CallbackInfoReturnable<Music> cir) {
        cir.cancel();
        if (MusicPlayer.shouldPlayMusic(this.musicManager)) {
            cir.setReturnValue(MusicPlayer.findMusic(this.musicManager));
        }
    }

    @WrapWithCondition(method = "updateScreenAndTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sounds/SoundManager;stop()V"))
    private boolean updateScreenAndTick(SoundManager instance) {
        return false;
    }

    @WrapOperation(
            method = "tick",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;pause:Z"),
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;shutdownEffect()V"),
                    to = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;soundManager:Lnet/minecraft/client/sounds/SoundManager;")
            )
    )
    private boolean dontPause(Minecraft instance, Operation<Boolean> original) {
        return false;
    }

//    @Redirect(
//            method = "tick",
//            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sounds/SoundManager;tick(Z)V")
//    )
//    private void alwaysTickSoundManager(SoundManager instance, boolean isGamePaused) {
//        Minecraft mc = (Minecraft) (Object) this;
//        instance.tick(mc.player == null);
//    }
}
