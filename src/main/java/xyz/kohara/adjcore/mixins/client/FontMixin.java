package xyz.kohara.adjcore.mixins.client;

import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.kohara.adjcore.ADJCore;

import java.util.function.Function;

@Mixin(Font.class)
public abstract class FontMixin {

	@Mutable
	@Shadow
	@Final
	private StringSplitter splitter;

	@Shadow
	abstract FontSet getFontSet(ResourceLocation fontLocation);

	@Shadow
	@Final
	boolean filterFishyGlyphs;

	@Inject(
			method = "<init>",
			at = @At("TAIL")
	)
	private void reduceOutlineWidth(Function<ResourceLocation, FontSet> fonts, boolean filterFishyGlyphs, CallbackInfo ci) {
		this.splitter = new StringSplitter((i, arg) -> {
			var advance = this.getFontSet(arg.getFont()).getGlyphInfo(i, this.filterFishyGlyphs).getAdvance(arg.isBold());
			return ADJCore.getFontAdvance(arg.getFont(), advance);
		});
	}
}