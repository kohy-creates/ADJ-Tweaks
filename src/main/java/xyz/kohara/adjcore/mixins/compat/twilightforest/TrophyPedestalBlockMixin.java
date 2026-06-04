package xyz.kohara.adjcore.mixins.compat.twilightforest;

import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import twilightforest.block.TrophyPedestalBlock;

@Mixin(value = TrophyPedestalBlock.class, remap = false)
public class TrophyPedestalBlockMixin {

	/**
	 * @author me
	 * @reason reasons
	 */
	@Overwrite(remap = false)
	private boolean isPlayerEligible(Player player) {
		return true;
	}
}
