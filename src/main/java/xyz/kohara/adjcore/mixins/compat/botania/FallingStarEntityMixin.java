package xyz.kohara.adjcore.mixins.compat.botania;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import vazkii.botania.client.fx.SparkleParticleData;
import vazkii.botania.common.block.flower.functional.ExoflameBlockEntity;
import vazkii.botania.common.block.flower.generating.EndoflameBlockEntity;
import vazkii.botania.common.entity.FallingStarEntity;
import vazkii.botania.common.entity.ThrowableCopyEntity;
import xyz.kohara.adjcore.registry.ADJParticles;

import java.util.List;

@Mixin(value = FallingStarEntity.class)
public abstract class FallingStarEntityMixin extends ThrowableCopyEntity {

    @Shadow
    private boolean hasBeenInAir;

    protected FallingStarEntityMixin(EntityType<? extends ThrowableCopyEntity> entityType, Level level) {
        super(entityType, level);
    }

    @WrapOperation(
            method = "onHitEntity",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", ordinal = 0)
    )
    private boolean overwriteDamageType(Entity instance, DamageSource source, float amount, Operation<Boolean> original) {
        var fallingStar = (FallingStarEntity) (Object) this;
        source = fallingStar.level().damageSources().mobProjectile(instance, (LivingEntity) fallingStar.getOwner());
        return original.call(instance, source, amount);
    }

    /**
     * @author meee
     * @reason redo particles, sort of redo the falling logic
     */
    @Overwrite
    public void tick() {
        super.tick();

        if (!hasBeenInAir && !level().isClientSide) {
            var bs = getFeetBlockState();
            hasBeenInAir = bs.isAir() || isInWater() || isInLava();
        }

        float dist = 0.5F;
        SparkleParticleData data = SparkleParticleData.sparkle(2F, 1F, 0.4F, 1F, 6);
        for (int i = 0; i < 6; i++) {
            float xs = (float) (Math.random() - 0.5) * dist;
            float ys = (float) (Math.random() - 0.5) * dist;
            float zs = (float) (Math.random() - 0.5) * dist;
            level().addAlwaysVisibleParticle(data, true, getX() + xs, getY() + ys, getZ() + zs, 0, 0, 0);
        }
        level().addParticle(ADJParticles.FLASHING_SPARK.get(), true, getX(), getY(), getZ(), (Math.random() - 0.5) * 0.9, (Math.random() - 0.5) * 0.9, (Math.random() - 0.5) * 0.9);

        Entity thrower = getOwner();
        if (!level().isClientSide && thrower != null) {
            AABB axis = new AABB(getX(), getY(), getZ(), xOld, yOld, zOld).inflate(2);
            List<LivingEntity> entities = level().getEntitiesOfClass(LivingEntity.class, axis);
            for (LivingEntity living : entities) {
                if (living == thrower) {
                    continue;
                }

                if (living.hurtTime == 0) {
                    onHit(new EntityHitResult(living));
                    return;
                }
            }
        }

        if (tickCount > 200) {
            discard();
        }
    }
}
