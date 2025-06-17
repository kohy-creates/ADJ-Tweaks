package xyz.kohara.adjcore.mixins.items;

import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CrossbowItem.class)
public class CrossbowMixin {

    @Inject(method = "getChargeDuration", at = @At("HEAD"), cancellable = true)
    private static void modifyPullTime(ItemStack crossbowStack, CallbackInfoReturnable<Integer> cir) {
        int lvl = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.QUICK_CHARGE, crossbowStack);

        int reduction = 0;
        for (int i = 0; i < lvl; i++) {
            reduction += (7 - i);
        }
        int pullTime = 40 - reduction;
        cir.setReturnValue(pullTime);
    }
}
