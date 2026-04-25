package xyz.kohara.adjcore.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import xyz.kohara.adjcore.misc.events.ADJExplosionDamageCalcEvent;

@Mixin(Explosion.class)
public abstract class ExplosionMixin {

    @Shadow
    @Final
    private double x;

    @Shadow
    @Final
    private double y;

    @Shadow
    @Final
    private double z;

    @Shadow
    @Final
    private float radius;

    @WrapOperation(
            method = "explode",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"
            )
    )
    private boolean redoExplosionDamageLogic(Entity instance, DamageSource source, float amount, Operation<Boolean> original) {

        var explosion = (Explosion) (Object) this;

        var pos = new Vec3(x, y, z);
        var distance = instance.distanceToSqr(pos);

        ADJExplosionDamageCalcEvent event = new ADJExplosionDamageCalcEvent(
                explosion,
                instance,
                explosion.getDirectSourceEntity(),
                explosion.getIndirectSourceEntity(),
                distance,
                Explosion.getSeenPercent(pos, instance),
                radius * 2
        );
        MinecraftForge.EVENT_BUS.post(event);

        return original.call(instance, source, (float) event.calculateDamage());
    }
}
