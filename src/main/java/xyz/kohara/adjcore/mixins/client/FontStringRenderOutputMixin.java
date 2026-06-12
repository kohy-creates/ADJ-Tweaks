package xyz.kohara.adjcore.mixins.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.font.GlyphInfo;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xyz.kohara.adjcore.ADJCore;

@Mixin(Font.StringRenderOutput.class)
public abstract class FontStringRenderOutputMixin {

	@WrapOperation(
			method = "accept",
			at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/font/GlyphInfo;getAdvance(Z)F")
	)
	private float reduceOutlineAdvance(GlyphInfo instance, boolean bold, Operation<Float> original, @Local(argsOnly = true) Style style) {
		return ADJCore.getFontAdvance(style.getFont(), original.call(instance, bold));
	}
}
