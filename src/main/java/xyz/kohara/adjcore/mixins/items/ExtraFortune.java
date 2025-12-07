package xyz.kohara.adjcore.mixins.items;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import xyz.kohara.adjcore.Config;
import xyz.kohara.adjcore.attributes.ModAttributes;

@Mixin(ApplyBonusCount.class)
public class ExtraFortune {

    @Shadow
    @Final
    Enchantment enchantment;

    @ModifyExpressionValue(
            method = "run",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getItemEnchantmentLevel(Lnet/minecraft/world/item/enchantment/Enchantment;Lnet/minecraft/world/item/ItemStack;)I"
            )
    )
    private int addExtraFortuneLevels(int level, ItemStack stack, LootContext lootContext) {
        Entity entity = lootContext.getParamOrNull(LootContextParams.THIS_ENTITY);
        if (entity instanceof LivingEntity livingEntity && this.enchantment != Enchantments.SILK_TOUCH) {
            AttributeInstance inst = livingEntity.getAttribute(ModAttributes.EXTRA_ORE_DROPS.get());
            if (inst != null) {
                int amount = (int) Math.round(inst.getValue());
                level += amount;
            }
        }

        return level;
    }
}