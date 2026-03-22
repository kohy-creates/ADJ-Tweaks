package xyz.kohara.adjcore.mixins.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;showOnlyReducedInfo()Z"))
    private boolean removeReducedDebugInfo(Minecraft instance) {
        return false;
    }
}
