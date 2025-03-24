package xyz.kohara.adjtweaks.mixins.combat;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xyz.kohara.adjtweaks.ConfigHandler;

import java.util.List;

@Mixin(Player.class)
public class DisableSweepAndCrit {

    @ModifyExpressionValue(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;onClimbable()Z"
            )
    )
    private boolean adjUtils$canCrit(boolean isOnClimbable)
    {
        return ConfigHandler.DISABLE_CRITS.get() || isOnClimbable;
    }

    @ModifyExpressionValue(
            method = "attack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;getEntitiesOfClass(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"
            )
    )
    private List<LivingEntity> adjUtils$modifyListOfSweepAttacks(List<LivingEntity> listOfSweepAttacks)
    {
        return ConfigHandler.DISABLE_SWEEP_ATTACKS.get() ? List.of() : listOfSweepAttacks;
    }
}
