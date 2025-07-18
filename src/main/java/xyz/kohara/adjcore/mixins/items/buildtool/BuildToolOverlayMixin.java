package xyz.kohara.adjcore.mixins.items.buildtool;

import com.legacy.structure_gel.core.client.screen.building_tool.BuildingToolOverlay;
import com.legacy.structure_gel.core.item.building_tool.BuildingToolItem;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BuildingToolOverlay.class, remap = true)
public class BuildToolOverlayMixin {

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isCreative()Z"))
    private static boolean allowAlways(LocalPlayer instance) {
        return true;
    }
}
