package xyz.kohara.adjcore.mixins.compat.aether;

import com.aetherteam.aether.capability.player.AetherPlayerCapability;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Pseudo
@Mixin(value = AetherPlayerCapability.class, remap = false)
public abstract class AetherPlayerCapabilityMixin {

    @Shadow @Final private static UUID LIFE_SHARD_HEALTH_ID;

    @Shadow public abstract int getLifeShardCount();

    @Inject(method = "getLifeShardHealthAttributeModifier", at = @At("HEAD"), cancellable = true)
    private void increaseAmount(CallbackInfoReturnable<AttributeModifier> cir) {
        cir.setReturnValue(new AttributeModifier(LIFE_SHARD_HEALTH_ID, "Life Shard health increase", (float) this.getLifeShardCount() * 10.0F, AttributeModifier.Operation.ADDITION));
    }
}
