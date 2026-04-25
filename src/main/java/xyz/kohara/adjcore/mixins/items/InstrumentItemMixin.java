package xyz.kohara.adjcore.mixins.items;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.InstrumentItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(InstrumentItem.class)
public class InstrumentItemMixin {

    @WrapOperation(
            method = "play",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"
            )
    )
    private static void changeSoundSource(
            Level instance,
            Player player,
            Entity entity,
            SoundEvent event, SoundSource category,
            float volume, float pitch,
            Operation<Void> original
    ) {
        original.call(instance, player, entity, event, SoundSource.NEUTRAL, volume, pitch);
    }
}
