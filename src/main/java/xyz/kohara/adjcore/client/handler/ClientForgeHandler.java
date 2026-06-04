package xyz.kohara.adjcore.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xyz.kohara.adjcore.ADJCore;
import xyz.kohara.adjcore.client.Keybindings;
import xyz.kohara.adjcore.client.networking.ADJMessages;
import xyz.kohara.adjcore.client.networking.packet.ChangeLoadOutC2SPacket;
import xyz.kohara.adjcore.misc.events.ItemIsLockedRenderCheckEvent;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = ADJCore.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientForgeHandler {

	@SubscribeEvent
	public static void clientTick(TickEvent.ClientTickEvent event) {
		Minecraft client = Minecraft.getInstance();
		if (client.player == null) return;

		if (Keybindings.INSTANCE.LOADOUT_1.consumeClick()) {
			ADJMessages.sendToServer(new ChangeLoadOutC2SPacket(1));
		}
		if (Keybindings.INSTANCE.LOADOUT_2.consumeClick()) {
			ADJMessages.sendToServer(new ChangeLoadOutC2SPacket(2));
		}
		if (Keybindings.INSTANCE.LOADOUT_3.consumeClick()) {
			ADJMessages.sendToServer(new ChangeLoadOutC2SPacket(3));
		}
		if (Keybindings.INSTANCE.NEW_HIDE_GUI.consumeClick()) {
			client.options.hideGui = !client.options.hideGui;
		}
		if (Keybindings.INSTANCE.HUGE_ASS_SCREENSHOT.consumeClick()) {
			var m = 5;
			client.grabHugeScreenshot(
					client.gameDirectory,
					client.getMainRenderTarget().width,
					client.getMainRenderTarget().height,
					client.getMainRenderTarget().width * m,
					client.getMainRenderTarget().height * m
			);
		}
	}

	@SubscribeEvent
	public static void preClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.START) {
			ItemIsLockedRenderCheckEvent.tick(Minecraft.getInstance());
		}
	}
}
