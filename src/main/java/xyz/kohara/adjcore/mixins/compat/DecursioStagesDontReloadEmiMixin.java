package xyz.kohara.adjcore.mixins.compat;

import com.decursioteam.decursio_stages.compat.plugins.DecursioStagesEMI;
import net.minecraftforge.fml.LogicalSide;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DecursioStagesEMI.class)
public class DecursioStagesDontReloadEmiMixin {

	@Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/LogicalSide;isClient()Z"))
	private boolean neverReloadEMI(LogicalSide instance) {
		return false;
	}
}
