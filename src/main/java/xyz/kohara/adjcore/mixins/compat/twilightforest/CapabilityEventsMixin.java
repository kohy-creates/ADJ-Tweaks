package xyz.kohara.adjcore.mixins.compat.twilightforest;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import twilightforest.capabilities.CapabilityList;
import twilightforest.events.CapabilityEvents;

@Mixin(value = CapabilityEvents.class, remap = false)
public class CapabilityEventsMixin {

    @Inject(
            method = "livingAttack",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void modifyShieldTags(LivingAttackEvent event, CallbackInfo ci) {
        ci.cancel();
        LivingEntity living = event.getEntity();
        // shields
        if (event.getEntity() instanceof Player player && player.getAbilities().invulnerable) return;
        if (!living.level().isClientSide() && !event.getSource().is(DamageTypeTags.BYPASSES_ARMOR) && !event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            living.getCapability(CapabilityList.SHIELDS).ifPresent(cap -> {
                if (cap.shieldsLeft() > 0) {
                    cap.breakShield();
                    event.setCanceled(true);
                }
            });
        }
    }
}
