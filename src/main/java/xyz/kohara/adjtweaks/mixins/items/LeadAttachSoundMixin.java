// Auditory
package xyz.kohara.adjtweaks.mixins.items;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public abstract class LeadAttachSoundMixin extends LivingEntity {


    protected LeadAttachSoundMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(
            method = "setLeashedTo",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/protocol/game/ClientboundSetEntityLinkPacket;<init>(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;)V"
            )
    )
    private void auditory_leashSound(Entity leashHolder, boolean broadcastPacket, CallbackInfo ci) {
        leashHolder.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.LEASH_KNOT_PLACE, SoundSource.NEUTRAL, 0.5F, 0.8f + this.level().random.nextFloat() * 0.4F);
    }

    @Inject(method = "interact", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;dropLeash(ZZ)V", shift = At.Shift.AFTER))
    private void auditory_unleashSound(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
            player.level().playSound(player, this.getX(), this.getY(), this.getZ(), SoundEvents.LEASH_KNOT_BREAK, SoundSource.NEUTRAL, 0.5F, 0.8f + this.level().random.nextFloat() * 0.4F);
    }
}