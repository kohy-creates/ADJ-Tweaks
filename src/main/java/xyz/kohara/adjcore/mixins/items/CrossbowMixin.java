package xyz.kohara.adjcore.mixins.items;

import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.SpectralArrow;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.kohara.adjcore.misc.CrossbowEnchantmentApplier;

@Mixin(CrossbowItem.class)
public class CrossbowMixin {

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
    private static void stopArrowConsumption(LivingEntity shooter, ItemStack crossbow, ItemStack projectile, boolean simulated, boolean creative, CallbackInfoReturnable<Boolean> cir) {
        // Returning if the player is in creative, the projectile is added by the multishot enchantment, or the shooter
        // isn't a player (pillager, skeleton..).
        if (creative || simulated || !(shooter instanceof Player))
            return;

        // Returning if Crossbow Enchants is disabled, the infinity enchantment is disabled, or the crossbow isn't
        // enchanted with infinity.
        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, crossbow) == 0)
            return;

        // Returning false if the projectile is an empty stack.
        if (projectile.isEmpty()) {
            cir.setReturnValue(false);
            cir.cancel();
        } // Returning if the projectile isn't a regular arrow.
        else if (!projectile.is(Items.ARROW))
            return;

        // Getting the player's inventory slot in which the projectile is in.
        int arrowSlot = ((Player) shooter).getInventory().getSlotWithRemainingSpace(projectile);
        // Creating a dummy arrow stack.
        ItemStack itemStack = new ItemStack(Items.ARROW);

        // Send a slot update packet to stop the client from displaying fake consumption.
        ((ServerPlayer) shooter).connection.send(new ClientboundContainerSetSlotPacket(-2, 0, arrowSlot, projectile));
        // Passing the dummy arrow to the put projectile method.
        CrossbowItemAccessor.addChargedProjectile(crossbow, itemStack);
        cir.setReturnValue(true);
        cir.cancel();
    }
}
