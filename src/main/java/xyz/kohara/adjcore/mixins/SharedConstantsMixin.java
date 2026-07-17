package xyz.kohara.adjcore.mixins;

import net.minecraft.SharedConstants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(SharedConstants.class)
public class SharedConstantsMixin {

	/**
	 * @author plenty of different people at this point
	 * @reason Disables any possibility of enabling DFU "optimizations" ~Lazy DFU mod
	 */
	@Overwrite
	public static void enableDataFixerOptimizations() {
	}
}
