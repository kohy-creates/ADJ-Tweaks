package xyz.kohara.adjcore.mixins.death;

import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.CombatTracker;
import net.minecraft.world.entity.TamableAnimal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.kohara.adjcore.ADJCore;

@Mixin(TamableAnimal.class)
public class DeathMessageTameableAnimal {

    @Redirect(
            method = "die",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/damagesource/CombatTracker;getDeathMessage()Lnet/minecraft/network/chat/Component;"
            )
    )
    private Component changeDeathMessageColor(CombatTracker instance) {
        return ADJCore.formatDeathMessage(instance.getDeathMessage());
    }
}
