// Auditory
package xyz.kohara.adjtweaks.mixins.items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEntity.class)
public abstract class LeadAttachSoundMixin extends LivingEntity {

    protected LeadAttachSoundMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "attachLeash", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerChunkManager;sendToOtherNearbyPlayers(Lnet/minecraft/entity/Entity;Lnet/minecraft/network/packet/Packet;)V"))
    private void auditory_leashSound(Entity entity, boolean sendPacket, CallbackInfo ci) {
            entity.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_LEASH_KNOT_PLACE, SoundCategory.NEUTRAL, 0.5F, 0.8f + this.getWorld().random.nextFloat() * 0.4F);
    }

    @Inject(method = "interact", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/MobEntity;detachLeash(ZZ)V", shift = At.Shift.AFTER))
    private void auditory_unleashSound(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
            player.getWorld().playSound(player, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_LEASH_KNOT_BREAK, SoundCategory.NEUTRAL, 0.5F, 0.8f + this.getWorld().random.nextFloat() * 0.4F);
    }
}