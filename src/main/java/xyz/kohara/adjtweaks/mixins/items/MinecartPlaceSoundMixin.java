package xyz.kohara.adjtweaks.mixins.items;

import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.MinecartItem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecartItem.class)
public class MinecartPlaceSoundMixin {

    @Inject(method = "useOnBlock",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/World;emitGameEvent(Lnet/minecraft/world/event/GameEvent;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/event/GameEvent$Emitter;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void auditory_placeSound(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        World level = context.getWorld();
        level.playSound(null, context.getBlockPos(), SoundEvents.BLOCK_NETHERITE_BLOCK_PLACE, SoundCategory.BLOCKS, 1.0f, 1.2f + level.random.nextFloat() * 0.4F);
    }
}
