package xyz.kohara.adjtweaks.mixins.tools;

import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.kohara.adjtweaks.ConfigHandler;

@Mixin(ItemStack.class)
public class DurabilityMixin {

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    public void modifyDurability(int amount, RandomSource random, ServerPlayer player, CallbackInfoReturnable<Boolean> cir) {
        if (Math.random() <= ConfigHandler.DURABILITY_SAVE_CHANCE.get()) {
            cir.setReturnValue(false);
        }
    }
}
