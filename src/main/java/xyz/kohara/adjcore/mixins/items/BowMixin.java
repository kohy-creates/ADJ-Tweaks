package xyz.kohara.adjcore.mixins.items;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.kohara.adjcore.Config;
import xyz.kohara.adjcore.registry.ADJSoundEvents;

@Mixin(BowItem.class)
public class BowMixin {

    @Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/InteractionResultHolder;consume(Ljava/lang/Object;)Lnet/minecraft/world/InteractionResultHolder;"))
    private void auditory_pullbackSound(Level level, Player player, InteractionHand usedHand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        if (!level.isClientSide()) player.playNotifySound(ADJSoundEvents.ITEM_BOW_PULLING.get(), SoundSource.PLAYERS, 0.3F, 0.8f + level.random.nextFloat() * 0.4F);
    }

    @ModifyExpressionValue(
            method = "releaseUsing",
            at = @At(
                    value = "CONSTANT", args = "floatValue=1.0", ordinal = 0)
    )
    private float addInaccuracy(float original) {
        return Config.BOW_INACCURACY.get().floatValue();
    }

    @Redirect(
            method = "releaseUsing",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;setBaseDamage(D)V"
            )
    )
    private void modifySetDamage(AbstractArrow instance, double baseDamage, ItemStack stack) {
        double powerLevel = stack.getEnchantmentLevel(Enchantments.POWER_ARROWS);
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

        instance.setBaseDamage(instance.getBaseDamage() * multiplier);
    }

}
