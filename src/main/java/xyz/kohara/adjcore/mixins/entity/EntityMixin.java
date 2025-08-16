package xyz.kohara.adjcore.mixins.entity;

import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow public abstract DamageSources damageSources();

    @Inject(
            method = "baseTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"
            ),
            cancellable = true
    )
    private void modifyOnFireDamage(CallbackInfo ci) {
        ci.cancel();
        ((Entity) (Object) this).hurt(this.damageSources().onFire(), 4.0F);
    }

    @Inject(
            method = "lavaHurt",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z",
                    ordinal = 0
            ),
            cancellable = true
    )
    private void modifyLavaDamage(CallbackInfo ci) {
        ci.cancel();
        ((Entity) (Object) this).hurt(this.damageSources().lava(), 20.0F);
    }
}
