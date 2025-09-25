package xyz.kohara.adjcore.client.handler;


import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xyz.kohara.adjcore.ADJCore;
import xyz.kohara.adjcore.client.Keybindings;

@Mod.EventBusSubscriber(modid = ADJCore.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModHandler {

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(Keybindings.INSTANCE.LOADOUT_1);
        event.register(Keybindings.INSTANCE.LOADOUT_2);
        event.register(Keybindings.INSTANCE.LOADOUT_3);
        event.register(Keybindings.INSTANCE.NEW_HIDE_GUI);
    }
}
