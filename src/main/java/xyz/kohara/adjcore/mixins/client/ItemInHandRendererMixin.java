package xyz.kohara.adjcore.mixins.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.kohara.adjcore.misc.events.ItemIsLockedRenderCheckEvent;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Redirect(method = "renderItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderStatic(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/level/Level;III)V"))
    private void redirectRenderItem(
            ItemRenderer instance,
            LivingEntity entity,
            ItemStack itemStack,
            ItemDisplayContext displayContext,
            boolean leftHand,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            Level level, int combinedLight, int combinedOverlay,
            int seed) {

        int light = combinedLight;

        if (minecraft.level != null) {
            ItemIsLockedRenderCheckEvent eventHook = new ItemIsLockedRenderCheckEvent(
                    itemStack,
                    this.minecraft.player
            );
            if (MinecraftForge.EVENT_BUS.post(eventHook)) {
                light = 0;
            }
        }

        instance
                .renderStatic(
                        entity, itemStack, displayContext, leftHand, poseStack, bufferSource, entity.level(), light, combinedOverlay, seed
                );
    }
}
