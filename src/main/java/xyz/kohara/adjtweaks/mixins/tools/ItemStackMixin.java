package xyz.kohara.adjtweaks.mixins.tools;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.kohara.adjtweaks.Config;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Inject(method = "damage(ILnet/minecraft/util/math/random/Random;Lnet/minecraft/server/network/ServerPlayerEntity;)Z", at = @At("HEAD"), cancellable = true)
    public void modifyDurability(int amount, Random random, ServerPlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        if (Math.random() <= Config.DURABILITY_SAVE_CHANCE.get()) {
            cir.setReturnValue(false);
        }
    }
}
