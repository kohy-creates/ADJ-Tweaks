package xyz.kohara.adjcore.mixins.compat.twilightforest.worldgen;

import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import twilightforest.world.components.chunkgenerators.warp.TFTerrainWarp;

@Mixin(TFTerrainWarp.class)
public class TFTerrainWarpMixin {

//	@WrapOperation(
//			method = "fillNoiseColumn",
//			at = @At(
//					value = "INVOKE",
//					target = ""
//			)
//	)
}
