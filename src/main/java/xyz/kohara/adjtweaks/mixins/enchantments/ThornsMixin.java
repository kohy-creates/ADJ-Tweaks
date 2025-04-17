package xyz.kohara.adjtweaks.mixins.enchantments;

import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.ThornsEnchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ThornsEnchantment.class)
public class ThornsMixin {

    @Inject(method = "isAcceptableItem", cancellable = true, at = @At("HEAD"))
    public void isAcceptableItem(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(EnchantmentTarget.ARMOR_CHEST.isAcceptableItem(stack.getItem()));
    }

    @Inject(method = "shouldDamageAttacker", cancellable = true, at = @At("HEAD"))
    private static void shouldDamageAttacker(int level, Random random, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(0.3F + (level - 1) * 0.1F);
    }

    @Inject(method = "getDamageAmount", cancellable = true, at = @At("HEAD"))
    private static void getDamageAmount(int level, Random random, CallbackInfoReturnable<Integer> cir) {
        if (level == 1) {
            cir.setReturnValue(1 + random.nextInt(2));
        } else if (level == 2) {
            cir.setReturnValue(1 + random.nextInt(3));
        } else if (level == 3) {
            cir.setReturnValue(2 + random.nextInt(2));
        } else {
            cir.setReturnValue(1 + random.nextInt(4));
        }
    }
}
