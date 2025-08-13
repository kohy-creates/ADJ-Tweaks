package xyz.kohara.adjcore.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xyz.kohara.adjcore.curio.FireImmunityCurios;

@Mixin(value = Entity.class, priority = 500)
public class TerraCurioEntityMixinFix {

    @ModifyExpressionValue(
            method = "baseTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;isInLava()Z",
                    ordinal = 1
            )
    )
    private boolean redoTheLogic(boolean original) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof Player player) {
            FireImmunityCurios.applyEffect(player);
        }
        return entity.isInLava();
    }
}
