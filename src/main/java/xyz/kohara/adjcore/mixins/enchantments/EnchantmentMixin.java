package xyz.kohara.adjcore.mixins.enchantments;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
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

    // From CrossbowEnchants mod
    @ModifyReturnValue(method = "canEnchant", at = @At("RETURN"))
    public boolean modifyIsAcceptableItemReturnValue(boolean original, ItemStack stack) {
        // Returning the original value if:
        // The original was already set to true.
        // The stack isn't a crossbow.
        // Crossbow Enchants is disabled.
        if (original || !(stack.is(Items.CROSSBOW) || stack.is(Items.BOW))) {
            return original;
        }

        Enchantment enchantment = (Enchantment) (Object) this;
        if (stack.is(Items.BOW)) {
            return enchantment == Enchantments.MOB_LOOTING;
        } else if (enchantment == Enchantments.FLAMING_ARROWS) {
            return true;
        } else if (enchantment == Enchantments.INFINITY_ARROWS) {
            return true;
        } else if (enchantment == Enchantments.MOB_LOOTING) {
            return true;
        } else if (enchantment == Enchantments.POWER_ARROWS) {
            return true;
        } else return enchantment == Enchantments.PUNCH_ARROWS;

    }

    /**
     * Makes Infinity and Mending as well as Piercing and Multishot compatible with one another.
     */
    @ModifyReturnValue(method = "checkCompatibility", at = @At("RETURN"))
    private boolean modifyCanCombineReturnValue(boolean original, Enchantment other) {
        // Returning the original value if:
        // The original was already set to true.
        // Crossbow Enchants is disabled.
        if (original) {
            return original;
        }

        Enchantment enchantment = (Enchantment) (Object) this;
        // Checking if the first enchantment is infinity, and the second enchantment is mending, and the opposite order.
        // Then returning the value based on the state of the feature.
        if (enchantment == Enchantments.INFINITY_ARROWS && other == Enchantments.MENDING ||
                enchantment == Enchantments.MENDING && other == Enchantments.INFINITY_ARROWS) {
            return true;
        }

        return false;
    }
}
