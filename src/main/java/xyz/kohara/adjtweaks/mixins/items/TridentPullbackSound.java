package xyz.kohara.adjtweaks.mixins.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.kohara.adjtweaks.sounds.ModSoundEvents;

@Mixin(TridentItem.class)
public abstract class TridentPullbackSound {

    @Inject(method = "use", at = @At(value = "RETURN", target = "Lnet/minecraft/world/entity/player/Player;startUsingItem(Lnet/minecraft/world/InteractionHand;)V"))
    private void auditory_pullbackSound(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
            world.playSound(null, user.getBlockPos(), ModSoundEvents.ITEM_TRIDENT_PULLING.get(), SoundCategory.PLAYERS, 0.1F, 0.8f + world.random.nextFloat() * 0.4F);
    }
}
