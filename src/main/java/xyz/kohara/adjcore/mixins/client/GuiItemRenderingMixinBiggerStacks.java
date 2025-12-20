// Edited from BiggerStacks for compatibility

package xyz.kohara.adjcore.mixins.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
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
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import portb.biggerstacks.config.ClientConfig;
import xyz.kohara.adjcore.misc.events.ItemIsLockedRenderCheckEvent;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;

import static portb.biggerstacks.Constants.*;

@Mixin(GuiGraphics.class)
public abstract class GuiItemRenderingMixinBiggerStacks {

    @Shadow
    @Final
    private Minecraft minecraft;
    @Shadow
    @Final
    private PoseStack pose;

    @Shadow
    public abstract int drawString(Font font, @Nullable String text, int x, int y, int color, boolean dropShadow);

    @Shadow
    public abstract int drawString(Font font, Component text, int x, int y, int color, boolean dropShadow);

    @Unique
    private boolean adj$shouldHideStack = false;

    private static final DecimalFormat BILLION_FORMAT = new DecimalFormat("#.##B");
    private static final DecimalFormat MILLION_FORMAT = new DecimalFormat("#.##M");
    private static final DecimalFormat THOUSAND_FORMAT = new DecimalFormat("#.##K");

    private static String getStringForBigStackCount(int count) {
        if (ClientConfig.enableNumberShortening.get()) {
            var decimal = new BigDecimal(count).round(new MathContext(3)); //pinnacle of over engineering

            var value = decimal.doubleValue();

            if (value >= ONE_BILLION)
                return BILLION_FORMAT.format(value / ONE_BILLION);
            else if (value >= ONE_MILLION)
                return MILLION_FORMAT.format(value / ONE_MILLION);
            else if (value > ONE_THOUSAND)
                return THOUSAND_FORMAT.format(value / ONE_THOUSAND);
        }

        return String.valueOf(count);
    }

    private static double calculateStringScale(Font font, String countString) {
        var width = font.width(countString);

        if (width < 16)
            return 1.0;
        else
            return 16.0 / width;
    }

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

    // region "delete" all the vanilla item count rendering
    @Redirect(method = "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V"))
    private void doNothing1(PoseStack instance, float p_254202_, float p_253782_, float p_254238_) {
    }

    @Redirect(method = "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIIZ)I"))
    private int doNothing2(GuiGraphics instance, Font p_283343_, String p_281896_, int p_283569_, int p_283418_, int p_281560_, boolean p_282130_) {
        return 0;
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

            this.pose.pushPose();
            this.pose.translate(0.0F, yOffset, 190.0F);

            Component darkText = Component.literal("?");

            this.pose.scale(1.5f, 1.5f, 1);
            this.drawString(font, darkText, (int) ((x + (font.width(darkText))) / 1.5f), (int) (y / 1.5f), 16777215, false);

            this.pose.popPose();
        }
    }

    //Inject the new text rendering
    @Inject(method = "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIIZ)I"))
    private void renderText(Font font, ItemStack itemStack, int x, int y, String alternateCount, CallbackInfo ci) {
        var poseStack = ((GuiGraphics) (Object) this).pose();

        String text = alternateCount == null ? getStringForBigStackCount(itemStack.getCount()) : alternateCount;

        float scale = (float) calculateStringScale(font, text);
        float inverseScale = 1 / scale;
        float xTransform = (x + 16) * inverseScale - font.width(text);
        float yTransform = (y + 16) * inverseScale - font.lineHeight;

        poseStack.scale(scale, scale, 1);
        poseStack.translate(xTransform, yTransform, 200);

        MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
        font.drawInBatch(
                text,
                /*pX*/ 0, //translation is done by poseStack. Doing it here just makes it harder.
                /*pY*/ 0,
                /*pColor*/ (adj$shouldHideStack) ? 6579300 : 16777215,
                /*pDropShadow*/ true,
                /*pMatrix*/ poseStack.last().pose(),
                /*pBufferSource*/ bufferSource,
                /*pTransparent*/ Font.DisplayMode.NORMAL,
                /*pBackgroundColour*/ 0,
                /*pPackedLightCoords*/ 15728880
        );

        // Without this, the position of the durability bar gets messed up with some mods like EMI
        poseStack.translate(-xTransform, -yTransform, 0);
        poseStack.scale(1 / scale, 1 / scale, 1);

        bufferSource.endBatch();
    }
}
