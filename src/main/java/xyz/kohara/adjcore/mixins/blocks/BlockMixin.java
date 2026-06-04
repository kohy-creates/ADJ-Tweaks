package xyz.kohara.adjcore.mixins.blocks;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.kohara.adjcore.Config;
import xyz.kohara.adjcore.registry.ADJAttributes;

@Mixin(Block.class)
public class BlockMixin {

    @WrapOperation(method = "fallOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;causeFallDamage(FFLnet/minecraft/world/damagesource/DamageSource;)Z"))
    private boolean redoFallDamage(Entity entity, float fallDistance, float multiplier, DamageSource source, Operation<Boolean> original) {
        multiplier *= 10f;
        if (entity instanceof LivingEntity livingEntity) {
            AttributeInstance safeFallDistance = livingEntity.getAttribute(ADJAttributes.SAFE_FALL_DISTANCE.get());
            if (safeFallDistance != null) {
                fallDistance -= (float) safeFallDistance.getValue();
            }

            AttributeInstance fallDamageReduction = livingEntity.getAttribute(ADJAttributes.FALL_DAMAGE_REDUCTION.get());
            if (fallDamageReduction != null) {
                multiplier -= (float) fallDamageReduction.getValue() * multiplier;
            }
        }
        return entity.causeFallDamage(fallDistance - 2f /* so that it's 5 safe fall distance base */, multiplier, source);
    }

    @Redirect(
            method = "playerDestroy",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;causeFoodExhaustion(F)V"
            )
    )
    private void redirectBlockMiningExhaustion(Player player, float original) {
        float multiplier = (float) Config.Exhaustion.blockMiningMul;
        float modified = original * multiplier;
        player.causeFoodExhaustion(modified);
    }
}
