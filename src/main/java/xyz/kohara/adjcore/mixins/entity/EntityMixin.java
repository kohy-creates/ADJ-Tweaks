package xyz.kohara.adjcore.mixins.entity;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow
    public abstract DamageSources damageSources();

    @Redirect(
            method = "baseTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"
            )
    )
    private boolean modifyOnFireDamage(Entity instance, DamageSource source, float amount) {
        return ((Entity) (Object) this).hurt(this.damageSources().onFire(), 4.0F);
    }

    @Redirect(
            method = "lavaHurt",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z",
                    ordinal = 0
            )
    )
    private boolean modifyLavaDamage(Entity instance, DamageSource source, float amount) {
        return ((Entity) (Object) this).hurt(this.damageSources().lava(), 30.0F);
    }
}
