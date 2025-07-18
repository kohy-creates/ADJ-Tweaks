package xyz.kohara.adjcore.mixins.items.buildtool;

import com.legacy.structure_gel.core.client.renderers.BuildingToolRenderer;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = BuildingToolRenderer.class, remap = true)
public class BuildToolRendererMixin {

    @Redirect(
            method = "render(Lnet/minecraft/client/Minecraft;Lcom/mojang/blaze3d/vertex/PoseStack;Lorg/joml/Matrix4f;DDD)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;isCreative()Z"
            )
    )
    private static boolean allowAlways(LocalPlayer instance) {
        return true;
    }
}
