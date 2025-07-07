package xyz.kohara.adjcore.mixins.items;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.SpectralArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.kohara.adjcore.misc.CrossbowEnchantmentApplier;

@Mixin(CrossbowItem.class)
public abstract class CrossbowMixin {

    @Shadow
    private static void addChargedProjectile(ItemStack crossbowStack, ItemStack ammoStack) {
    }

    @Inject(method = "getChargeDuration", at = @At("HEAD"), cancellable = true)
    private static void modifyPullTime(ItemStack crossbowStack, CallbackInfoReturnable<Integer> cir) {
        int lvl = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.QUICK_CHARGE, crossbowStack);

        int reduction = 0;
        for (int i = 0; i < lvl; i++) {
            reduction += (6 - i);
        }
        int pullTime = 30 - reduction;
        cir.setReturnValue(pullTime);
    }

    // Applying the enchants to the arrow via injecting the createArrow method.
    @Inject(method = "getArrow", at = @At("TAIL"))
    private static void applyEnchantsToArrow(Level level, LivingEntity livingEntity, ItemStack crossbowStack, ItemStack ammoStack, CallbackInfoReturnable<AbstractArrow> cir) {

        // Returning if the projectile isn't an arrow.
        AbstractArrow persistentProjectileEntity = cir.getReturnValue();
        if (!(persistentProjectileEntity instanceof Arrow) && !(persistentProjectileEntity instanceof SpectralArrow))
            return;

        // Applying the enchantments onto the arrow.
        CrossbowEnchantmentApplier.applyFlame(persistentProjectileEntity, crossbowStack);
        CrossbowEnchantmentApplier.applyInfinity(persistentProjectileEntity, crossbowStack, ammoStack);
        CrossbowEnchantmentApplier.applyPower(persistentProjectileEntity, crossbowStack);
        CrossbowEnchantmentApplier.applyPunch(persistentProjectileEntity, crossbowStack);
    }

    // Stopping the consumption of the arrow if the crossbow is enchanted with infinity, via injecting the
    // loadProjectile method.
    @Inject(method = "loadProjectile", at = @At("HEAD"), cancellable = true)
    private static void loadProjectile(LivingEntity shooter, ItemStack crossbowStack, ItemStack ammoStack, boolean hasAmmo, boolean isCreative, CallbackInfoReturnable<Boolean> cir) {
        if (ammoStack.isEmpty()) {
            cir.setReturnValue(false);
        } else {
            boolean flag = isCreative && ammoStack.getItem() instanceof ArrowItem;
            boolean saveArrow = isCreative || EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, crossbowStack) > 0;
            ItemStack itemstack;
            if (!flag && !saveArrow && !hasAmmo) {
                itemstack = ammoStack.split(1);
                if (ammoStack.isEmpty() && shooter instanceof Player) {
                    ((Player) shooter).getInventory().removeItem(ammoStack);
                }
            } else {
                itemstack = ammoStack.copy();
            }

            addChargedProjectile(crossbowStack, itemstack);
            cir.setReturnValue(true);
        }
    }
}
