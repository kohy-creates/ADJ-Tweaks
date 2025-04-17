package xyz.kohara.adjtweaks.mixins.entity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xyz.kohara.adjtweaks.Config;

import java.util.List;

@Mixin(PlayerEntity.class)
public abstract class PlayerMixin extends LivingEntity {

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @ModifyExpressionValue(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;isClimbing()Z"
            )
    )
    private boolean adjUtils$canCrit(boolean isOnClimbable)
    {
        return Config.DISABLE_CRITS.get() || isOnClimbable;
    }

    @ModifyExpressionValue(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;getNonSpectatingEntities(Ljava/lang/Class;Lnet/minecraft/util/math/Box;)Ljava/util/List;"
            )
    )
    private List<LivingEntity> adjUtils$modifyListOfSweepAttacks(List<LivingEntity> listOfSweepAttacks) {
        return Config.DISABLE_SWEEP_ATTACKS.get() ? List.of() : listOfSweepAttacks;
    }
}
