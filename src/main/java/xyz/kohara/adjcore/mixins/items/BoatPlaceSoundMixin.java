// Auditory
package xyz.kohara.adjcore.mixins.items;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BoatItem.class)
public abstract class BoatPlaceSoundMixin extends Item {


    @Shadow @Final private Boat.Type type;

    public BoatPlaceSoundMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "use",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;gameEvent(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/level/gameevent/GameEvent;Lnet/minecraft/world/phys/Vec3;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void auditory_placeSound(Level level, Player player, InteractionHand usedHand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        if (this.type == Boat.Type.BAMBOO) {
            level.playSound(null, getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY).getBlockPos(), SoundEvents.BAMBOO_WOOD_PLACE, SoundSource.BLOCKS, 1.0f, 0.8f + level.random.nextFloat() * 0.4F);
        } else if (this.type == Boat.Type.CHERRY) {
            level.playSound(null, getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY).getBlockPos(), SoundEvents.CHERRY_WOOD_PLACE, SoundSource.BLOCKS, 1.0f, 0.8f + level.random.nextFloat() * 0.4F);
        } else {
            level.playSound(null, getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY).getBlockPos(), SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 1.0f, 0.8f + level.random.nextFloat() * 0.4F);
        }
    }
}
