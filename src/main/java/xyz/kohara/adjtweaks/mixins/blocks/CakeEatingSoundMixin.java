// Auditory
package xyz.kohara.adjtweaks.mixins.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.CakeBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Plays the eating sound when a cake is eaten

@Mixin(CakeBlock.class)
public abstract class CakeEatingSoundMixin {

    @Inject(method = "tryEat", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getHungerManager()Lnet/minecraft/entity/player/HungerManager;"))
    private static void auditory_eatingSound(WorldAccess world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfoReturnable<ActionResult> cir) {
        world.playSound(player, pos, SoundEvents.ENTITY_GENERIC_EAT, SoundCategory.PLAYERS, 1.0f, 0.8f + world.getRandom().nextFloat() * 0.4F);

    }
}
