// Auditory
package xyz.kohara.adjtweaks.mixins.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.BoatItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BoatItem.class)
public abstract class BoatPlaceSoundMixin extends Item {


    @Shadow @Final private BoatEntity.Type type;

    public BoatPlaceSoundMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "use",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/World;emitGameEvent(Lnet/minecraft/entity/Entity;Lnet/minecraft/world/event/GameEvent;Lnet/minecraft/util/math/Vec3d;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void auditory_placeSound(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        if (this.type == BoatEntity.Type.BAMBOO) {
            world.playSound(null, raycast(world, user, RaycastContext.FluidHandling.ANY).getBlockPos(), SoundEvents.BLOCK_BAMBOO_WOOD_PLACE, SoundCategory.BLOCKS, 1.0f, 0.8f + world.random.nextFloat() * 0.4F);
        } else if (this.type == BoatEntity.Type.CHERRY) {
            world.playSound(null, raycast(world, user, RaycastContext.FluidHandling.ANY).getBlockPos(), SoundEvents.BLOCK_CHERRY_WOOD_PLACE, SoundCategory.BLOCKS, 1.0f, 0.8f + world.random.nextFloat() * 0.4F);
        } else {
            world.playSound(null, raycast(world, user, RaycastContext.FluidHandling.ANY).getBlockPos(), SoundEvents.BLOCK_WOOD_PLACE, SoundCategory.BLOCKS, 1.0f, 0.8f + world.random.nextFloat() * 0.4F);
        }
    }
}
