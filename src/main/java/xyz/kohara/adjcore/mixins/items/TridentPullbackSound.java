package xyz.kohara.adjcore.mixins.items;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.kohara.adjcore.registry.ADJSoundEvents;

@Mixin(TridentItem.class)
public abstract class TridentPullbackSound {

    @Inject(
            method = "use",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;startUsingItem(Lnet/minecraft/world/InteractionHand;)V"
            )
    )
    private void auditory_pullbackSound(Level level, Player player, InteractionHand usedHand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
           if (!level.isClientSide()) player.playNotifySound(ADJSoundEvents.ITEM_TRIDENT_PULLING.get(), SoundSource.PLAYERS, 0.1F, 0.8f + level.random.nextFloat() * 0.4F);
    }
}
