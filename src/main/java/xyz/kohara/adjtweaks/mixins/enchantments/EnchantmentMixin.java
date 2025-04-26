package xyz.kohara.adjtweaks.mixins.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Enchantment.class, priority = 1500)
public abstract class EnchantmentMixin {

    @Inject(
            method = "getName",
            at = @At("RETURN"),
            cancellable = true
    )
    public void modifyEnchColor(int level, CallbackInfoReturnable<Text> cir) {
        Enchantment enchantment = (Enchantment) (Object) this;
        if (cir.getReturnValue() instanceof MutableText text) {
            if (level >= enchantment.getMaxLevel()) {
                if (enchantment.isTreasure() && !enchantment.isCursed()) {
                    text.setStyle(Style.EMPTY.withColor(16761088));
                } else {
                    text.setStyle(Style.EMPTY.withColor(52735));
                }
            } else if (enchantment.isTreasure() && !enchantment.isCursed()) {
                text.setStyle(Style.EMPTY.withColor(6549074));
            } else if (enchantment.isCursed()) {
                text.setStyle(Style.EMPTY.withColor(13701120));
            }
            cir.setReturnValue(text);
        }
    }
}
