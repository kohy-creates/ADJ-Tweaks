package xyz.kohara.adjcore.mixins.compat.client;

import com.cyao.holoinventoryrevived.renderers.InventoryRenderer;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = InventoryRenderer.class, remap = false)
public class HoloInventoryRendererMixin {

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Font;m_271703_(Ljava/lang/String;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)I"))
    private static int prettifyName(
            Font instance,
            String text,
            float x, float y,
            int color, boolean dropShadow,
            Matrix4f matrix, MultiBufferSource buffer,
            Font.DisplayMode displayMode,
            int backgroundColor, int packedLightCoords,
            Operation<Integer> original
    ) {
        matrix.translate(0f, 0f, -0.1f);
        instance.drawInBatch(text, x + 1, y, 0, false, matrix, buffer, displayMode, backgroundColor, packedLightCoords);
        instance.drawInBatch(text, x - 1, y, 0, false, matrix, buffer, displayMode, backgroundColor, packedLightCoords);
        instance.drawInBatch(text, x, y + 1, 0, false, matrix, buffer, displayMode, backgroundColor, packedLightCoords);
        instance.drawInBatch(text, x, y - 1, 0, false, matrix, buffer, displayMode, backgroundColor, packedLightCoords);
        matrix.translate(0f, 0f, 0.1f);
        return original.call(instance, text, x, y, 0xFFFFFF, false, matrix, buffer, displayMode, backgroundColor, packedLightCoords);
    }
}
