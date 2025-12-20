package xyz.kohara.adjcore.mixins.enchantments;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.TridentImpalerEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TridentImpalerEnchantment.class)
public class ImpalingMixin extends Enchantment {

    protected ImpalingMixin(Rarity rarity, EnchantmentCategory category, EquipmentSlot[] applicableSlots) {
        super(rarity, category, applicableSlots);
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        Item item = stack.getItem();
        return item instanceof SwordItem || item instanceof TridentItem;
    }

    @Inject(method = "getMaxLevel", at = @At("HEAD"), cancellable = true)
    public void getLevel(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(4);
    }

    @Inject(method = "getDamageBonus", at = @At("HEAD"), cancellable = true)
    private void noDamageBonus(int level, MobType type, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(0F);
    }
}
