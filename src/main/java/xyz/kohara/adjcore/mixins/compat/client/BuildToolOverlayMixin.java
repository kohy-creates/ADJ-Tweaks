package xyz.kohara.adjcore.mixins.compat.client;

import com.legacy.structure_gel.core.client.screen.building_tool.BuildingToolOverlay;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = BuildingToolOverlay.class, remap = true)
public class BuildToolOverlayMixin {

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isCreative()Z"))
    private static boolean allowAlways(LocalPlayer instance) {
        return true;
    }
}
