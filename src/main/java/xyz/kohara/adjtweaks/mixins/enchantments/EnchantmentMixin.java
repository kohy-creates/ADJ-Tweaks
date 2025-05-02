package xyz.kohara.adjtweaks.mixins.enchantments;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Currently unusued
@Mixin(value = Enchantment.class, priority = 900000)
public abstract class EnchantmentMixin {

    @Inject(
            method = "getFullname",
            at = @At("RETURN"),
            cancellable = true
    )
    public void modifyEnchColor(int level, CallbackInfoReturnable<Component> cir) {
        Enchantment enchantment = (Enchantment) (Object) this;
        if (cir.getReturnValue() instanceof MutableComponent text) {
            if (level >= enchantment.getMaxLevel()) {
                if (enchantment.isTreasureOnly() && !enchantment.isCurse()) {
                    text.setStyle(Style.EMPTY.withColor(16761088));
                } else {
                    text.setStyle(Style.EMPTY.withColor(52735));
                }
            } else if (enchantment.isTreasureOnly() && !enchantment.isCurse()) {
                text.setStyle(Style.EMPTY.withColor(6549074));
            } else if (enchantment.isCurse()) {
                text.setStyle(Style.EMPTY.withColor(13701120));
            }
            cir.setReturnValue(text);
        }
    }
}
