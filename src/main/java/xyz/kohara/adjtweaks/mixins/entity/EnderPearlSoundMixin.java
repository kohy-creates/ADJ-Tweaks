package xyz.kohara.adjtweaks.mixins.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnderPearlEntity.class)
abstract class EnderPearlSoundMixin extends ThrownItemEntity {

    public EnderPearlSoundMixin(EntityType<? extends ThrownItemEntity> arg, World arg2) {
        super(arg, arg2);
    }

    @Inject(method = "onCollision",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z",
                    shift = At.Shift.AFTER
            )
    )
    private void auditory_teleportSound(HitResult hitResult, CallbackInfo ci) {
        if (this.getOwner() instanceof PlayerEntity player) {
            player.playSound(SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.PLAYERS, 0.8f, 1.0f);
        }
    }
}
