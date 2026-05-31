package xyz.kohara.adjcore.mixins.compat;

import dev.ftb.mods.ftbquests.client.ClientQuestFile;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.kohara.adjcore.ADJCore;

@Mixin(ReceivingLevelScreen.class)
public class FTBQuestFreezeFix {

	@Inject(method = "onClose", at = @At(value = "HEAD"))
	private void onClose(CallbackInfo ci) {
		if (ADJCore.IsFTBQuestsCached)
			return;

		ADJCore.IsFTBQuestsCached = true;
		var gui = ClientQuestFile.openGui();
		if (gui != null)
			gui.closeGui(true);
		else
			ADJCore.LOGGER.error("[ADJCORE] ERROR! Could not get FTB Quests GUI!");
	}
}