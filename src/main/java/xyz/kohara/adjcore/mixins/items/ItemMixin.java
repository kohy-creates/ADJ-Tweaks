package xyz.kohara.adjcore.mixins.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.common.extensions.IForgeItem;
import org.spongepowered.asm.mixin.Mixin;
import xyz.kohara.adjcore.Config;

@Mixin(Item.class)
public abstract class ItemMixin implements IForgeItem {

    @Override
    public int getMaxDamage(ItemStack stack) {
        int durability = IForgeItem.super.getMaxDamage(stack);
        int i = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING, stack);
        if (i > 0) {
            durability *= Config.UNBREAKNG_DURABILITY_MULTIPLIER.get();
        }
        return durability;
    }
}
