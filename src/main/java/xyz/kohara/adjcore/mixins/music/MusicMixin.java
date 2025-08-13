package xyz.kohara.adjcore.mixins.music;

import net.minecraft.client.Minecraft;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.sounds.Music;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.kohara.adjcore.music.MusicPlayer;

@Mixin(Minecraft.class)
public class MusicMixin {

    @Shadow
    @Final
    private MusicManager musicManager;

    @Inject(
            method = "getSituationalMusic",
            at = @At("HEAD"),
            cancellable = true
    )
    private void getSituationalMusic(CallbackInfoReturnable<Music> cir) {
        if (MusicPlayer.shouldPlayMusic(this.musicManager)) {
            cir.setReturnValue(MusicPlayer.findMusic(this.musicManager));
        }
    }
}
