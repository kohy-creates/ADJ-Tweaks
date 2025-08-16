package xyz.kohara.adjcore.mixins.enchantments;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.DamageEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DamageEnchantment.class)
public class SharpnessMixin extends Enchantment {

    protected SharpnessMixin(Rarity rarity, EnchantmentCategory category, EquipmentSlot[] applicableSlots) {
        super(rarity, category, applicableSlots);
    }

    @Inject(method = "getMaxLevel", at = @At("HEAD"), cancellable = true)
    public void getMaxLevel(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(4);
    }

    @Inject(method = "canEnchant", cancellable = true, at = @At("HEAD"))
    public void isAcceptableItem(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        boolean canEnchant = stack.getItem() instanceof AxeItem;
        if (stack.getItem() instanceof TridentItem) canEnchant = true;
        if (super.canEnchant(stack)) canEnchant = true;
        cir.setReturnValue(canEnchant);
    }

    @Inject(method = "checkCompatibility", at = @At("HEAD"), cancellable = true)
    private void compatibleWithEverything(Enchantment other, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }

    @Inject(method = "getDamageBonus", at = @At("HEAD"), cancellable = true)
    public void getDamageBonus(int level, MobType type, CallbackInfoReturnable<Float> cir) {
        cir.cancel();
        DamageEnchantment enchantment = (DamageEnchantment) (Object) this;
        if (enchantment.type == 0) {
            float amount = 6F + 4F * (level - 1);
            cir.setReturnValue(amount);
        } else if (enchantment.type == 1 && type == MobType.UNDEAD) {
            float amount = 9F + 5F * (level - 1);
            cir.setReturnValue(amount);
        } else {
            float amount = 9F + 5F * (level - 1);
            cir.setReturnValue(enchantment.type == 2 && type == MobType.ARTHROPOD ? amount : 0.0F);
        }
    }
}
