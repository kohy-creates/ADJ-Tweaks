package xyz.kohara.adjcore.mixins.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

// Adapted from:
// https://github.com/ObscuriaLithium/healight/blob/master/common/src/main/java/dev/obscuria/healight/mixin/client/MixinLivingEntityRenderer.java

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {

    @WrapOperation(
            method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V"
            )
    )
    private void healLight(
            EntityModel<?> instance,
            PoseStack pose,
            VertexConsumer consumer,
            int light,
            int overlay,
            float r,
            float g,
            float b,
            float a,
            Operation<Void> original,
            @Local(argsOnly = true) LivingEntity entity
    ) {
        if (entity.adjcore$getHealTime() > 0) {
            int blockLight = Math.min(15, LightTexture.block(light) + 3);
            int skyLight = Math.min(15, LightTexture.sky(light) + 3);
            light = LightTexture.pack(blockLight, skyLight);
            r = 0.25F;
            g = 1.0F;
            b = 0.25F;
        }

        original.call(instance, pose, consumer, light, overlay, r, g, b, a);
    }
}
