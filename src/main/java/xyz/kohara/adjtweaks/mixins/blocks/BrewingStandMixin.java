package xyz.kohara.adjtweaks.mixins.blocks;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(BrewingStandBlockEntity.class)
public class BrewingStandMixin {

    @Unique
    private static final List<Item> FUEL_ITEMS = List.of(
            Items.BLAZE_POWDER,
            Items.GLOWSTONE_DUST
    );

    @ModifyExpressionValue(
            method = "serverTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"
            )
    )
    private static boolean isAllowedItem(boolean original, @Local(ordinal = 0) ItemStack itemstack) {
        return original || (FUEL_ITEMS.contains(itemstack.getItem()));
    }

    @Inject(
            method = "canPlaceItem",
            at = @At("HEAD"),
            cancellable = true
    )
    private void isCorrectItem(int index, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (index == 4) {
            if (FUEL_ITEMS.contains(stack.getItem())) {
                cir.setReturnValue(true);
            }
        }
    }
}
