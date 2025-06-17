package xyz.kohara.adjcore.mixins.enchantments;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.MultiShotEnchantment;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = MultiShotEnchantment.class)
public abstract class MultishotMixin extends Enchantment {

    protected MultishotMixin(Rarity rarity, EnchantmentCategory category, EquipmentSlot[] applicableSlots) {
        super(rarity, category, applicableSlots);
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }
}
