package xyz.kohara.adjcore.mixins;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.capability.ability.AbilityProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(value = Entity.class, priority = 3000)
public class TerraCurioEntityMixinFix {
    @Redirect(
            method = "baseTick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;isInLava()Z", ordinal = 1)
    )
    private boolean lavaImmuneFixedBugs(Entity instance) {
        if (instance.isInLava()) {
            AtomicBoolean inLava = new AtomicBoolean(true);
            Entity entity = (Entity) (Object) this;
            if (entity instanceof Player living) {
                living.getCapability(AbilityProvider.CAPABILITY).ifPresent((playerAbility) -> {
                    if (inLava.get()) {
                        if (playerAbility.decreaseLavaImmuneTicks()) {
                            inLava.set(false);
                        }
                    } else {
                        playerAbility.increaseLavaImmuneTicks();
                    }

                });
            }

            return inLava.get();
        }
        return false;
    }
}
