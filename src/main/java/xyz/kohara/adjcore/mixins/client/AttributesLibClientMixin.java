package xyz.kohara.adjcore.mixins.client;

import dev.shadowsoffire.attributeslib.client.AttributesLibClient;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AttributesLibClient.class, remap = false)
public class AttributesLibClientMixin {

    @Inject(method = "apothCrit", at = @At("HEAD"), cancellable = true)
    private static void apothCrit(int entityId, CallbackInfo ci) {
        ci.cancel();

        Entity entity = Minecraft.getInstance().level.getEntity(entityId);
        if (entity != null) {
            Minecraft.getInstance().particleEngine.createTrackingEmitter(entity, ParticleTypes.CRIT);
        }
    }
}
