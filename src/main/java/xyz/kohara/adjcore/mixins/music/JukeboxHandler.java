package xyz.kohara.adjcore.mixins.music;

import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.kohara.adjcore.client.music.JukeboxTracker;

@Mixin(JukeboxBlockEntity.class)
public class JukeboxHandler {

    @Inject(method = "stopPlaying", at = @At(value = "HEAD"))
    private void onStopPlaying(CallbackInfo ci) {
        JukeboxBlockEntity jbe = (JukeboxBlockEntity) (Object) this;
        JukeboxTracker.onJukeboxStop(jbe.getBlockPos());
    }
}
