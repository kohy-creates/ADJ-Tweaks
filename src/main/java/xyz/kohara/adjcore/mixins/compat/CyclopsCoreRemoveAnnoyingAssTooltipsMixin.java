package xyz.kohara.adjcore.mixins.compat;

import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "org.cyclops.cyclopscore.item.ItemInformationProvider", remap = false)
public abstract class CyclopsCoreRemoveAnnoyingAssTooltipsMixin {

    @Inject(method = "onTooltip", at = @At("HEAD"), cancellable = true)
    private static void remove(ItemTooltipEvent event, CallbackInfo ci) {
        ci.cancel();
    }
}
