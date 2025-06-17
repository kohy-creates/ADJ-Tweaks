package xyz.kohara.adjcore.mixins.enchantments;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.ThornsEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ThornsEnchantment.class)
public class ThornsMixin {

    @Inject(method = "canEnchant", cancellable = true, at = @At("HEAD"))
    public void isAcceptableItem(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(EnchantmentCategory.ARMOR_CHEST.canEnchant(stack.getItem()));
    }

    @Inject(method = "shouldHit", cancellable = true, at = @At("HEAD"))
    private static void shouldDamageAttacker(int level, RandomSource random, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(random.nextFloat() < (0.2F + level * 0.1F));
    }

    @Inject(method = "getDamage", cancellable = true, at = @At("HEAD"))
    private static void getDamageAmount(int level, RandomSource random, CallbackInfoReturnable<Integer> cir) {
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
