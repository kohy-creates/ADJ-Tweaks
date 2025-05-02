package xyz.kohara.adjtweaks.mixins.blocks;

// Plays a sound when a disc is inserted or ejected from a jukebox

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.kohara.adjtweaks.sounds.ModSoundEvents;

@Mixin(JukeboxBlock.class)
public class JukeboxDiscSoundMixin {

    @Inject(method = "use",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/entity/JukeboxBlockEntity;popOutRecord()V"
            )
    )
    private void auditory_ejectDiscSound(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        level.playSound(player, pos, ModSoundEvents.JUKEBOX_EJECT.get(), SoundSource.BLOCKS, 1.0f, 0.8f + level.random.nextFloat() * 0.4F);
    }

    @Inject(method = "onRemove", at = @At("HEAD"))
    private void auditory_insertDiscSound(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving, CallbackInfo ci) {
        if (newState.getBlock() instanceof JukeboxBlock && newState.getValue(JukeboxBlock.HAS_RECORD)) {
            level.playSound(null, pos, ModSoundEvents.JUKEBOX_USE.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        }
    }
}
