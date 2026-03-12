package xyz.kohara.adjcore.mixins.compat.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tiviacz.travelersbackpack.handlers.NeoForgeClientEventHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = NeoForgeClientEventHandler.class, remap = false)
public class TravelersBackpackRemoveRightClick {

    @ModifyExpressionValue(method = "renderBackpackIcon", at = @At(value = "INVOKE", target = "Lcom/tiviacz/travelersbackpack/items/TravelersBackpackItem;isCreative(Lnet/minecraft/world/entity/player/Player;)Z"))
    private static boolean noRender(boolean original) {
        return true;
    }
}
