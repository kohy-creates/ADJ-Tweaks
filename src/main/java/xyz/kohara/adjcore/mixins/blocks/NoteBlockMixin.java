package xyz.kohara.adjcore.mixins.blocks;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.NoteBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(NoteBlock.class)
public class NoteBlockMixin {

    @WrapOperation(
            method = "triggerEvent",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;playSeededSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;FFJ)V"
            )
    )
    private void changeSoundSourceForNoteBlocks(
            Level instance,
            Player player,
            double x, double y, double z,
            Holder<SoundEvent> soundEventHolder, SoundSource soundSource,
            float volume, float pitch, long seed,
            Operation<Void> original
    ) {
        original.call(instance, player, x, y, z, soundEventHolder, SoundSource.BLOCKS, volume, pitch, seed);
    }
}
