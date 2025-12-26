package xyz.kohara.adjcore.mixins.compat;

import com.rosemods.heart_crystals.core.HCConfig;
import com.rosemods.heart_crystals.core.other.HCEvents;
import com.rosemods.heart_crystals.core.other.HCPlayerInfo;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = HCEvents.class, remap = false)
public abstract class HCEventsMixin {

    @Inject(method = "onPlayerLoggedIn", at = @At("HEAD"), cancellable = true)
    private static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event, CallbackInfo ci) {
        ci.cancel();
        Player player = event.getEntity();
        adj$syncPlayerInfo(player);
        if (player != null) {
            HCPlayerInfo.PlayerHealthInfo info = HCPlayerInfo.getPlayerHealthInfo(player);
            int minimum = HCConfig.COMMON.minimum.get();
            if (!info.healthSet) {
                HCEvents.setMaxHealthAttribute(minimum * 20, player);
                player.setHealth((float)(minimum * 20));
                info.healthSet = true;
                info.syncHealthInfo(player);
            } else if (info.heartCount < HCConfig.COMMON.minimum.get()) {
                HCEvents.setMaxHealthAttribute(minimum * 20, player);
                player.setHealth((float)(minimum * 20));
                info.heartCount = minimum;
                info.syncHealthInfo(player);
            }
        }

    }

    @Inject(method = "onPlayerRespawn", at = @At("HEAD"), cancellable = true)
    private static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event, CallbackInfo ci) {
        ci.cancel();
        Player player = event.getEntity();
        adj$syncPlayerInfo(event.getEntity());
        if (player != null) {
            HCPlayerInfo.PlayerHealthInfo info = HCPlayerInfo.getPlayerHealthInfo(player);
            HCEvents.setMaxHealthAttribute(info.heartCount * 20, player);
            player.setHealth(Math.max((float) (info.heartCount * 20) / 2F, 100f));
        }

    }

    @Unique
    private static void adj$syncPlayerInfo(Player player) {
        if (player != null && !player.level().isClientSide()) {
            HCPlayerInfo.getPlayerHealthInfo(player).syncHealthInfo(player);
        }

    }
}
