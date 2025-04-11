package xyz.kohara.adjtweaks.mixins.items;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import xyz.kohara.adjtweaks.Config;

@Mixin(ApplyBonusLootFunction.class)
public class ExtraFortune {

    @Shadow
    @Final
    Enchantment enchantment;

    @ModifyExpressionValue(
            method = "process",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/enchantment/EnchantmentHelper;getLevel(Lnet/minecraft/enchantment/Enchantment;Lnet/minecraft/item/ItemStack;)I"
            )
    )
    private int addFortuneLevel(int level, ItemStack stack, LootContext lootContext) {
        Entity entity = lootContext.get(LootContextParameters.THIS_ENTITY);

        if (entity instanceof LivingEntity
                && this.enchantment != Enchantments.SILK_TOUCH
                && Math.random() <= Config.EXTRA_FORTUNE_CHANCE.get()) {
            level += 1;
        }

        return level;
    }
}