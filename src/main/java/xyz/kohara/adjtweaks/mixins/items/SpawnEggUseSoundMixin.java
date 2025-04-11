package xyz.kohara.adjtweaks.mixins.items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.kohara.adjtweaks.sounds.ModSoundEvents;

@Mixin(EntityType.class)
public class SpawnEggUseSoundMixin {

    @Inject(at=@At("RETURN"), method="spawnFromItemStack")
    private void auditory_spawnEggSound(ServerWorld world, ItemStack stack, PlayerEntity player, BlockPos pos, SpawnReason spawnReason, boolean alignPosition, boolean invertY, CallbackInfoReturnable<Entity> cir) {
        if (stack.getItem() instanceof SpawnEggItem) {
            world.playSound(null, pos, ModSoundEvents.ITEM_SPAWN_EGG_USE.get(), SoundCategory.NEUTRAL, 1.0F, 1.0F);
        }
    }
}