package xyz.kohara.adjcore.mixins.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xyz.kohara.adjcore.client.misc.events.ItemIsLockedRenderCheckEvent;

@Mixin(ItemEntityRenderer.class)
public class DroppedItemRendererMixin {

	@WrapOperation(
			method = "render(Lnet/minecraft/world/entity/item/ItemEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V"
			)
	)
	private void redoThisIdkWhatToCallThis(
			ItemRenderer instance,
			ItemStack itemStack,
			ItemDisplayContext displayContext,
			boolean leftHand,
			PoseStack poseStack,
			MultiBufferSource multiBufferSource,
			int packedLight,
			int overlay,
			BakedModel model,
			Operation<Void> original
	) {
		if (ItemIsLockedRenderCheckEvent.shouldHide(itemStack, Minecraft.getInstance().player)) {
			packedLight = 0;
		}
		original.call(instance, itemStack, displayContext, leftHand, poseStack, multiBufferSource, packedLight, overlay, model);
	}
}
