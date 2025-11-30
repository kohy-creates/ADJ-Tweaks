package xyz.kohara.adjcore.mixins.client;

import net.minecraft.client.gui.components.ChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatComponent.class)
public class ChatHUDMixin {

    @Inject(method = "clearMessages", at = @At("HEAD"), cancellable = true)
    private void dontClearChatHistory(boolean clearSentMsgHistory, CallbackInfo ci) {
        if (clearSentMsgHistory) {
            ci.cancel();
        }
    }
}
