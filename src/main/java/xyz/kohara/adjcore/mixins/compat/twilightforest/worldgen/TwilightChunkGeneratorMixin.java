package xyz.kohara.adjcore.mixins.compat.twilightforest.worldgen;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import twilightforest.world.components.chunkgenerators.ChunkGeneratorTwilight;

@Mixin(ChunkGeneratorTwilight.class)
public class TwilightChunkGeneratorMixin {

	@WrapMethod(
			method = "generateBaseState",
			remap = false
	)
	private BlockState higherHighLowerLow(double noiseVal, double level, Operation<BlockState> original) {
		return original.call(noiseVal * 30.0d, level);
	}
}
