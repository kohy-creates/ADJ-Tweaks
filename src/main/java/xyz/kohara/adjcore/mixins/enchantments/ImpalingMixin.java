package xyz.kohara.adjcore.mixins.enchantments;

import net.minecraft.world.item.enchantment.TridentImpalerEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TridentImpalerEnchantment.class)
public class ImpalingMixin {

    @Inject(method = "getMaxLevel", at = @At("HEAD"), cancellable = true)
    public void getLevel(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(3);
    }
}
