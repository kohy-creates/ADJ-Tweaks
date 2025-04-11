package xyz.kohara.adjtweaks.mixins.blocks;

// Plays a sound when a disc is inserted or ejected from a jukebox

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.kohara.adjtweaks.sounds.ModSoundEvents;

@Mixin(JukeboxBlock.class)
public class JukeboxDiscSoundMixin {

    @Inject(method = "onUse",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/entity/JukeboxBlockEntity;dropRecord()V"
            )
    )
    private void auditory_ejectDiscSound(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        world.playSound(player, pos, ModSoundEvents.BLOCK_JUKEBOX_EJECT.get(), SoundCategory.BLOCKS, 1.0f, 0.8f + world.random.nextFloat() * 0.4F);
    }

    @Inject(method = "onStateReplaced", at = @At("HEAD"))
    private void auditory_insertDiscSound(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved, CallbackInfo ci) {
        if (newState.getBlock() instanceof JukeboxBlock && newState.get(JukeboxBlock.HAS_RECORD)) {
            world.playSound(null, pos, ModSoundEvents.BLOCK_JUKEBOX_USE.get(), SoundCategory.PLAYERS, 1.0F, 1.0F);
        }
    }
}
