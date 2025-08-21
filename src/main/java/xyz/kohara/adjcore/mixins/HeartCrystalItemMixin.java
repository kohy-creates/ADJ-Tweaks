package xyz.kohara.adjcore.mixins;

import com.rosemods.heart_crystals.common.item.HeartCrystalItem;
import com.rosemods.heart_crystals.core.HCConfig;
import com.rosemods.heart_crystals.core.other.HCEvents;
import com.rosemods.heart_crystals.core.other.HCPlayerInfo;
import com.rosemods.heart_crystals.core.registry.HCSoundEvents;
import it.unimi.dsi.fastutil.objects.ObjectHeaps;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = HeartCrystalItem.class, remap = true)
public abstract class HeartCrystalItemMixin {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void onUse(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        cir.cancel();

        ItemStack stack = player.getItemInHand(hand);
        HCPlayerInfo.PlayerHealthInfo info = HCPlayerInfo.getPlayerHealthInfo(player);

        HeartCrystalItem item = (HeartCrystalItem) (Object) this;

        if (info.heartCount < HCConfig.COMMON.maximum.get()) {
            info.heartCount++;
            info.syncHealthInfo(player);
            HCEvents.setMaxHealthAttribute(info.heartCount * 20, player);
            stack.shrink(1);
            player.heal(2f);
            player.getCooldowns().addCooldown(item, 10);
            level.playSound(player, player.blockPosition(), HCSoundEvents.HEART_CRYSTAL_USE.get(), SoundSource.PLAYERS, .65f, 1f + ((level.random.nextFloat() - .5f) / 8f));

            cir.setReturnValue(InteractionResultHolder.success(stack));
        } else
            player.displayClientMessage(Component.translatable(item.getDescriptionId() + ".maximum"), true);

        cir.setReturnValue(InteractionResultHolder.fail(stack));
    }
}
