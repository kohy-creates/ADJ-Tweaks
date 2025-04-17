package xyz.kohara.adjtweaks.mixins.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.kohara.adjtweaks.misc.Colors;

@Mixin(value = Enchantment.class, priority = 1500)
public abstract class EnchantmentMixin {

    @Inject(
            method = "getName",
            at = @At("RETURN"),
            cancellable = true
    )
    public void modifyEnchColor(int level, CallbackInfoReturnable<Text> cir) {
        Enchantment enchantment = (Enchantment) (Object) this;
        if (level >= enchantment.getMaxLevel() && cir.getReturnValue() instanceof MutableText text) {
            if (enchantment.isTreasure() && !enchantment.isCursed()) {
                cir.setReturnValue(text.setStyle(Style.EMPTY.withColor(Colors.YELLOW_FLASH)));
            }
            else if (enchantment.isCursed()) {
                cir.setReturnValue(text.setStyle(Style.EMPTY.withColor(Colors.RED_FLASH)));
            }
            else {
                cir.setReturnValue(text.setStyle(Style.EMPTY.withColor(Colors.LIGHT_BLUE_FLASH)));
            }
        }
    }
}
