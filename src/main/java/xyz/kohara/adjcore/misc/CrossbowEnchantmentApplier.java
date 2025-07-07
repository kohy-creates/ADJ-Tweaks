package xyz.kohara.adjcore.misc;

import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

public class CrossbowEnchantmentApplier {
    public static void applyFlame(AbstractArrow entity, ItemStack crossbow) {
        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, crossbow) > 0)
            entity.setSecondsOnFire(100);
    }

    public static void applyInfinity(AbstractArrow entity, ItemStack crossbow, ItemStack projectile) {
        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, crossbow) > 0 && projectile.is(Items.ARROW))
            entity.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
    }

    public static void applyPower(AbstractArrow entity, ItemStack crossbow) {
        int powerLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, crossbow);
        entity.setBaseDamage(entity.getBaseDamage() + (double) powerLevel * 0.5 + 0.5);
    }

    public static void applyPunch(AbstractArrow entity, ItemStack crossbow) {
        int punchLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, crossbow);
        if (punchLevel > 0) entity.setKnockback(punchLevel);
    }
}