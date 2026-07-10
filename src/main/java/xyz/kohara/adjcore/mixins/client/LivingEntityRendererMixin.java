package xyz.kohara.adjcore.mixins.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.curios.client.render.CuriosLayer;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {

//	// Adapted from:
//	// https://github.com/ObscuriaLithium/healight/blob/master/common/src/main/java/dev/obscuria/healight/mixin/client/MixinLivingEntityRenderer.java
//	@WrapOperation(
//			method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
//			at = @At(
//					value = "INVOKE",
//					target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V"
//			)
//	)
//	private void healLight(
//			EntityModel<?> instance,
//			PoseStack pose,
//			VertexConsumer consumer,
//			int light,
//			int overlay,
//			float r,
//			float g,
//			float b,
//			float a,
//			Operation<Void> original,
//			@Local(argsOnly = true) LivingEntity entity
//	) {
//		if (entity.adjcore$getHealTime() > 0) {
//			int blockLight = Math.min(15, LightTexture.block(light) + 3);
//			int skyLight = Math.min(15, LightTexture.sky(light) + 3);
//			light = LightTexture.pack(blockLight, skyLight);
//			r = 0.25F;
//			g = 1.0F;
//			b = 0.25F;
//		}
//
//		original.call(instance, pose, consumer, light, overlay, r, g, b, a);
//	}

	@Inject(
			method = "shouldShowName(Lnet/minecraft/world/entity/LivingEntity;)Z",
			at = @At("HEAD"),
			cancellable = true
	)
	private void viewOwnLabel(LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
		Minecraft minecraft = Minecraft.getInstance();
		if (entity == minecraft.player) {
			cir.setReturnValue(!minecraft.options.hideGui && minecraft.options.getCameraType() != CameraType.FIRST_PERSON);
		}
	}

	@SuppressWarnings("unchecked")
	@Redirect(
			method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/layers/RenderLayer;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/Entity;FFFFFF)V")
	)
	private void redirectRenderLayer(
			RenderLayer renderLayer,
			PoseStack poseStack,
			MultiBufferSource bufferSource,
			int packedLight,
			Entity entity,
			float limbSwing,
			float limbSwingAmount,
			float partialTicks,
			float ageInTicks,
			float netHeadYaw,
			float headPitch
	) {
		if (renderLayer instanceof CuriosLayer<?,?> && !(entity instanceof Player)) {
			return;
		}
		renderLayer.render(poseStack, bufferSource, packedLight, entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
	}
}
