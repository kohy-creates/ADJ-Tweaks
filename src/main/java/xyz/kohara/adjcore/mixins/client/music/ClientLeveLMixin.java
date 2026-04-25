package xyz.kohara.adjcore.mixins.client.music;

import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.kohara.adjcore.ADJCore;
import xyz.kohara.adjcore.client.music.ADJMusicManager;

@Mixin(ClientLevel.class)
public class ClientLeveLMixin {

    @Inject(method = "disconnect", at = @At("TAIL"))
    private void clearJukeboxCache(CallbackInfo ci) {
        ADJMusicManager.getInstance().musicBlockChannels.clear();
        ADJCore.LOGGER.info("Disconnected from ClientLevel, clearing Jukebox cache set");
    }
}
