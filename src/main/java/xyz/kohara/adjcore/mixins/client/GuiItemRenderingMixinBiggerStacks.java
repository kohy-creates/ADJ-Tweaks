package xyz.kohara.adjcore.mixins.client;

import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import portb.biggerstacks.config.ClientConfig;
import xyz.kohara.adjcore.misc.events.ItemIsLockedRenderCheckEvent;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;

import static portb.biggerstacks.Constants.*;

@Mixin(GuiGraphics.class)
public class GuiItemRenderingMixinBiggerStacks {

    @Shadow @Final private Minecraft minecraft;
    // EDIT FOR BIGGERSTACKS
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
        poseStack.translate(xTransform, yTransform, 215);

        ItemIsLockedRenderCheckEvent eventHook = new ItemIsLockedRenderCheckEvent(
                itemStack,
                this.minecraft.player
        );
        boolean dark = MinecraftForge.EVENT_BUS.post(eventHook);

        MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
        font.drawInBatch(
                text,
                /*pX*/ 0, //translation is done by poseStack. Doing it here just makes it harder.
                /*pY*/ 0,
                /*pColor*/ 16777215,
                /*pDropShadow*/ true,
                /*pMatrix*/ poseStack.last().pose(),
                /*pBufferSource*/ bufferSource,
                /*pTransparent*/ Font.DisplayMode.NORMAL,
                /*pBackgroundColour*/ 0,
                /*pPackedLightCoords*/ (dark) ? 6579300 : 15728880
        );

        // Without this, the position of the durability bar gets messed up with some mods like EMI
        poseStack.translate(-xTransform, -yTransform, 0);

        bufferSource.endBatch();
    }
}
