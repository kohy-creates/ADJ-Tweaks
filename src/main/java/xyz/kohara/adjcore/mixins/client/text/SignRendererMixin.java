package xyz.kohara.adjcore.mixins.client.text;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.SignText;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import oshi.util.tuples.Pair;

import java.util.HashMap;
import java.util.Map;

@Mixin(SignRenderer.class)
public class SignRendererMixin {

    @Unique
    @Final
    private static final Map<Integer, Pair<Integer, Pair<Integer, Integer>>> adj$dyeColorOverrides = new HashMap<>();

    @Unique
    private Pair<Integer, Integer> adj$glowColors = null;

    static {
        adj$dyeColorOverrides.put(DyeColor.BLACK.getTextColor(), new Pair<>(0x000000, new Pair<>(0x000000, 0xEDEDED)));
        adj$dyeColorOverrides.put(DyeColor.BLUE.getTextColor(), new Pair<>(0x3863C7, new Pair<>(0x2667FF, 0x032466)));
        adj$dyeColorOverrides.put(DyeColor.BROWN.getTextColor(), new Pair<>(0xCC956E, new Pair<>(0xCF814A, 0x4F2F17)));
        adj$dyeColorOverrides.put(DyeColor.CYAN.getTextColor(), new Pair<>(0x7CD6D6, new Pair<>(0x13F2F2, 0x006161)));
        adj$dyeColorOverrides.put(DyeColor.GRAY.getTextColor(), new Pair<>(0x535F66, new Pair<>(0x92A4AD, 0x33393D)));
        adj$dyeColorOverrides.put(DyeColor.GREEN.getTextColor(), new Pair<>(0x7AA140, new Pair<>(0x7EBF1D, 0x2C420B)));
        adj$dyeColorOverrides.put(DyeColor.LIGHT_BLUE.getTextColor(), new Pair<>(0x85CEED, new Pair<>(0x4FCAFF, 0x12517A)));
        adj$dyeColorOverrides.put(DyeColor.LIGHT_GRAY.getTextColor(), new Pair<>(0xA2A2A3, new Pair<>(0xCBCED1, 0x4F5459)));
        adj$dyeColorOverrides.put(DyeColor.LIME.getTextColor(), new Pair<>(0xD5FF80, new Pair<>(0xB8F500, 0x4B750C)));
        adj$dyeColorOverrides.put(DyeColor.MAGENTA.getTextColor(), new Pair<>(0xDB7DCD, new Pair<>(0xED64D9, 0x6E026E)));
        adj$dyeColorOverrides.put(DyeColor.ORANGE.getTextColor(), new Pair<>(0xFFBF52, new Pair<>(0xFA9E25, 0x803903)));
        adj$dyeColorOverrides.put(DyeColor.PINK.getTextColor(), new Pair<>(0xEDB2C7, new Pair<>(0xFFC9D8, 0xAB496D)));
        adj$dyeColorOverrides.put(DyeColor.PURPLE.getTextColor(), new Pair<>(0xC194D1, new Pair<>(0xCE7FF5, 0x470F73)));
        adj$dyeColorOverrides.put(DyeColor.RED.getTextColor(), new Pair<>(0xEF6D62, new Pair<>(0xFC4A44, 0x690F19)));
        adj$dyeColorOverrides.put(DyeColor.WHITE.getTextColor(), new Pair<>(0xF2F2F2, new Pair<>(0xFFFFFF, 0x585570)));
        adj$dyeColorOverrides.put(DyeColor.YELLOW.getTextColor(), new Pair<>(0xFCF58D, new Pair<>(0xFFED24, 0x856714)));
    }

    @WrapOperation(
            method = "renderSignText",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/blockentity/SignRenderer;getDarkColor(Lnet/minecraft/world/level/block/entity/SignText;)I"
            )
    )
    private int overwriteBaseTextColor(SignText i, Operation<Integer> original) {
        var col = i.getColor().getTextColor();
        if (adj$dyeColorOverrides.containsKey(col)) {
            var pair = adj$dyeColorOverrides.get(col);
            adj$glowColors = pair.getB();
            return pair.getA();
        }
        adj$glowColors = null;
        return original.call(i);
    }

    @WrapOperation(
            method = "renderSignText",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/Font;drawInBatch8xOutline(Lnet/minecraft/util/FormattedCharSequence;FFIILorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"
            )
    )
    private void overwriteGlowColors(
            Font instance,
            FormattedCharSequence charSequence,
            float x, float y,
            int color, int backgroundColor,
            Matrix4f matrix4f,
            MultiBufferSource bufferSource,
            int packedLight,
            Operation<Void> original
    ) {
        if (adj$glowColors != null) {
            color = adj$glowColors.getA();
            backgroundColor = adj$glowColors.getB();
        }
        original.call(instance, charSequence, x, y, color, backgroundColor, matrix4f, bufferSource, packedLight);
    }
}
