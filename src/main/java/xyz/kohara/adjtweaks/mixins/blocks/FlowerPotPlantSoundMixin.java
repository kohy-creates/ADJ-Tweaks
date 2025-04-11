// Auditory
package xyz.kohara.adjtweaks.mixins.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.FlowerPotBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Plays a sound when a plant is put into a flower pot

@Mixin(FlowerPotBlock.class)
public class FlowerPotPlantSoundMixin {

    @Inject(method = "onUse",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/World;emitGameEvent(Lnet/minecraft/entity/Entity;Lnet/minecraft/world/event/GameEvent;Lnet/minecraft/util/math/BlockPos;)V"
            )
    )
    private void auditory_pottingSound(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        world.playSound(player, pos, SoundEvents.BLOCK_HANGING_ROOTS_PLACE, SoundCategory.BLOCKS, 1.0f, 0.8f + world.random.nextFloat() * 0.4F);
    }
}
