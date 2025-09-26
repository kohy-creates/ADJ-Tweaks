package xyz.kohara.adjcore.mixins.entity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.kohara.adjcore.Config;

import java.util.List;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyExpressionValue(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;onClimbable()Z"
            )
    )
    private boolean adjUtils$canCrit(boolean isOnClimbable) {
        return Config.DISABLE_CRITS.get() || isOnClimbable;
    }

    @ModifyExpressionValue(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;getEntitiesOfClass(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"
            )
    )
    private List<LivingEntity> adjUtils$modifyListOfSweepAttacks(List<LivingEntity> listOfSweepAttacks) {
        return Config.DISABLE_SWEEP_ATTACKS.get() ? List.of() : listOfSweepAttacks;
    }

    @Redirect(
            method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/item/ItemEntity;setDeltaMovement(DDD)V",
                    ordinal = 0
            )
    )
    private void changeDropVelocity(ItemEntity instance, double x, double y, double z, @Local ItemEntity itementity) {
        float f = this.random.nextFloat() * 0.15F;
        float f1 = this.random.nextFloat() * (float) (Math.PI * 2);
        itementity.setDeltaMovement(-Mth.sin(f1) * f, 0.2F, Mth.cos(f1) * f);
    }

    @ModifyReturnValue(
            method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;",
            at = @At("RETURN")
    )
    private ItemEntity makeDespawnLonger(ItemEntity original) {
        if (original == null) return null;
        original.lifespan *= 3;
        return original;
    }
}
