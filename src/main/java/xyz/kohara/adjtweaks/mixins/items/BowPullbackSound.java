package xyz.kohara.adjtweaks.mixins.items;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.kohara.adjtweaks.Config;
import xyz.kohara.adjtweaks.sounds.ModSoundEvents;

@Mixin(BowItem.class)
public class BowPullbackSound {

    @Inject(method = "use", at = @At(value = "RETURN", target = "Lnet/minecraft/world/entity/player/Player;startUsingItem(Lnet/minecraft/world/InteractionHand;)V"))
    private void auditory_pullbackSound(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        world.playSound(null, user.getBlockPos(), ModSoundEvents.ITEM_BOW_PULLING.get(), SoundCategory.PLAYERS, 0.3F, 0.8f + world.random.nextFloat() * 0.4F);
    }

    @ModifyExpressionValue(
            method = "onStoppedUsing",
            at = @At(
                    value = "CONSTANT", args = "floatValue=1.0", ordinal = 0)
    )
    private float addInaccuracy(float original) {
        return Config.BOW_INACCURACY.get().floatValue();
    }

    @Redirect(
            method = "onStoppedUsing",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;setDamage(D)V"
            )
    )
    private void modifySetDamage(PersistentProjectileEntity instance, double damage, ItemStack stack) {
        double powerLevel = stack.getEnchantmentLevel(Enchantments.POWER);
        double multiplier = 1.0;

        if (powerLevel == 1) {
            multiplier = 1.35;
        } else if (powerLevel == 2) {
            multiplier = 1.50;
        } else if (powerLevel == 3) {
            multiplier = 1.60;
        } else if (powerLevel > 3) {
            multiplier = 1.60 + 0.10 * (powerLevel - 3);
        }

        instance.setDamage(instance.getDamage() * multiplier);
    }

}
