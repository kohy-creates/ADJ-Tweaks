package xyz.kohara.adjcore.mixins.compat.aether;

import com.aetherteam.aether.event.listeners.abilities.WeaponAbilityListener;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WeaponAbilityListener.class, remap = false)
public class WeaponAbilityListenerMixin {

    @Inject(method = "onEntityDamage", at = @At("HEAD"), cancellable = true)
    private static void unnerfNonAetherStuff(LivingDamageEvent event, CallbackInfo ci) {
        ci.cancel();
    }
}
