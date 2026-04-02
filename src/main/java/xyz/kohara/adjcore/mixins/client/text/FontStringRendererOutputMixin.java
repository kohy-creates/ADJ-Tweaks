package xyz.kohara.adjcore.mixins.client.text;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Style;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import oshi.util.tuples.Pair;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

@Mixin(Font.StringRenderOutput.class)
public abstract class FontStringRendererOutputMixin {

    @Shadow
    @Final
    private boolean dropShadow;

    @Unique
    @Final
    private static final Map<Integer, Pair<Integer, Integer>> adj$colorOverrides = new HashMap<>();

    static {
        adj$colorOverrides.put(ChatFormatting.AQUA.getColor(), new Pair<>(0x24FFF8, 0x065D78));
        adj$colorOverrides.put(ChatFormatting.BLACK.getColor(), new Pair<>(0x131517, 0x656D73));
        adj$colorOverrides.put(ChatFormatting.BLUE.getColor(), new Pair<>(0x2E74FF, 0x0E0678));
        adj$colorOverrides.put(ChatFormatting.DARK_AQUA.getColor(), new Pair<>(0x1CB7BD, 0x003E4F));
        adj$colorOverrides.put(ChatFormatting.DARK_BLUE.getColor(), new Pair<>(0x0A3EAD, 0x190038));
        adj$colorOverrides.put(ChatFormatting.DARK_GRAY.getColor(), new Pair<>(0x66686B, 0x282D30));
        adj$colorOverrides.put(ChatFormatting.DARK_GREEN.getColor(), new Pair<>(0x3CAB1A, 0x284700));
        adj$colorOverrides.put(ChatFormatting.DARK_PURPLE.getColor(), new Pair<>(0x9635bd, 0x3F024A));
        adj$colorOverrides.put(ChatFormatting.DARK_RED.getColor(), new Pair<>(0xAB1A21, 0x3D0014));
        adj$colorOverrides.put(ChatFormatting.GOLD.getColor(), new Pair<>(0xFFA024, 0x783506));
        adj$colorOverrides.put(ChatFormatting.GRAY.getColor(), new Pair<>(0xAEB3B8, 0x464B4F));
        adj$colorOverrides.put(ChatFormatting.GREEN.getColor(), new Pair<>(0x53FF38, 0x2C6603));
        adj$colorOverrides.put(ChatFormatting.LIGHT_PURPLE.getColor(), new Pair<>(0xFF6EEC, 0x82074A));
        adj$colorOverrides.put(ChatFormatting.RED.getColor(), new Pair<>(0xF75462, 0x691128));
        adj$colorOverrides.put(ChatFormatting.YELLOW.getColor(), new Pair<>(0xFFF021, 0x785806));
    }

    @Unique
    private int adj$cachedColor;

    @Inject(
            method = "<init>",
            at = @At("TAIL")
    )
    private void cacheRGB(
            Font arg,
            MultiBufferSource bufferSource,
            float x, float y,
            int color,
            boolean dropShadow,
            Matrix4f pose,
            Font.DisplayMode mode,
            int packedLightCoords,
            CallbackInfo ci
    ) {
        this.adj$cachedColor = color & 0x00FFFFFF;
    }

    @Unique
    private float adj$r, adj$g, adj$b;

    @Inject(
            method = "accept",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/chat/Style;getColor()Lnet/minecraft/network/chat/TextColor;",
                    shift = At.Shift.AFTER
            )
    )
    private void saveColor(int j, Style arg, int k, CallbackInfoReturnable<Boolean> cir) {
        var styleColor = arg.getColor();
        int baseColor;

        if (styleColor != null) {
            baseColor = styleColor.getValue();
        } else {
            baseColor = adj$cachedColor;
        }

        var pair = adj$colorOverrides.get(baseColor);
        if (pair != null) {
            baseColor = this.dropShadow ? pair.getB() : pair.getA();
        }

        if (pair == null && this.dropShadow) {
            baseColor = adj$createShadowColor(baseColor);
        }

        int color = baseColor;

        adj$r = ((color >> 16) & 0xFF) / 255.0F;
        adj$g = ((color >> 8) & 0xFF) / 255.0F;
        adj$b = (color & 0xFF) / 255.0F;
    }

    @Unique
    private int adj$createShadowColor(int rgb) {
        var javaCol = new Color(rgb);
        var hsb = Color.RGBtoHSB(javaCol.getRed(), javaCol.getGreen(), javaCol.getBlue(), null);
        return Color.getHSBColor(
                hsb[0],
                Math.min(hsb[1] * 1.2f, 1.0f),
                (hsb[2] * 0.375F)
        ).getRGB() & 0x00FFFFFF;
    }

    @WrapOperation(
            method = "accept",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/Font;renderChar(Lnet/minecraft/client/gui/font/glyphs/BakedGlyph;ZZFFFLorg/joml/Matrix4f;Lcom/mojang/blaze3d/vertex/VertexConsumer;FFFFI)V"
            )
    )
    private void overWriteRenderChar(
            Font instance,
            BakedGlyph glyph,
            boolean bold, boolean italic,
            float boldOffset,
            float x, float y,
            Matrix4f matrix,
            VertexConsumer buffer,
            float red, float green, float blue,
            float alpha,
            int packedLight,
            Operation<Void> original
    ) {
        original.call(instance, glyph, bold, italic, boldOffset, x, y, matrix, buffer, adj$r, adj$g, adj$b, alpha, packedLight);
    }
}
