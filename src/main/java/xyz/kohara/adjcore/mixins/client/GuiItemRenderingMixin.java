package xyz.kohara.adjcore.mixins.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.kohara.adjcore.misc.events.ItemIsLockedRenderCheckEvent;

@Mixin(GuiGraphics.class)
public abstract class GuiItemRenderingMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    public abstract int drawString(Font font, Component text, int x, int y, int color, boolean dropShadow);

    @Shadow
    @Final
    private PoseStack pose;

    @Unique
    private boolean adj$shouldHideStack = false;

    @Redirect(
            method = "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;IIII)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V"
            )
    )
    private void redirectItemRender(
            ItemRenderer instance,
            ItemStack itemStack,
            ItemDisplayContext itemDisplayContext,
            boolean posestack$pose,
            PoseStack poseStack,
            MultiBufferSource multiBufferSource,
            int vertexconsumer,
            int rendertype,
            BakedModel model
    ) {
        int light = 15728880;
        adj$shouldHideStack = false;

        ItemIsLockedRenderCheckEvent eventHook = new ItemIsLockedRenderCheckEvent(
                itemStack,
                this.minecraft.player
        );
        if (MinecraftForge.EVENT_BUS.post(eventHook)) {
            light = 0;
            adj$shouldHideStack = true;
        }
        this.minecraft
                .getItemRenderer()
                .render(itemStack, itemDisplayContext, false, this.pose, multiBufferSource, light, OverlayTexture.NO_OVERLAY, model);
    }

    @Inject(
            method = "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V")
    )
    private void renderItemDecorations(Font font, ItemStack stack, int x, int y, String text, CallbackInfo ci) {
        if (adj$shouldHideStack) {

            float yOffset = 0f;
            if (minecraft.level != null) {
                float time = minecraft.level.getGameTime() + minecraft.getFrameTime();
                yOffset = (float) Math.floor(Math.sin(time * 0.04f) * 2.0f);
            }

            this.pose.translate(0.0F, yOffset, 190.0F);

            Component darkText = Component.literal("?");

            this.pose.scale(1.5f, 1.5f, 1);
            this.drawString(font, darkText, (int) ((x + (font.width(darkText))) / 1.5f), (int) (y / 1.5f), 16777215, false);

            this.pose.scale(1 / 1.5f, 1 / 1.5f, 1);
            this.pose.translate(0.0F, -yOffset, -190.0F);
        }
    }
}
