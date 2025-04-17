package xyz.kohara.adjtweaks.mixins.items;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CrossbowItem.class)
public class CrossbowMixin {

    @Inject(method = "getPullTime", at = @At("HEAD"), cancellable = true)
    private static void modifyPullTime(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        int lvl = EnchantmentHelper.getLevel(Enchantments.QUICK_CHARGE, stack);

        int reduction = 0;
        for (int i = 0; i < lvl; i++) {
            reduction += (7 - i);
        }
        int pullTime = 40 - reduction;
        cir.setReturnValue(pullTime);
    }
}
