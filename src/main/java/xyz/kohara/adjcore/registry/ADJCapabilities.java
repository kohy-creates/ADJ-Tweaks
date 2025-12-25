package xyz.kohara.adjcore.registry;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import xyz.kohara.adjcore.registry.capabilities.IPlayerLoadouts;

public class ADJCapabilities {

    public static final Capability<IPlayerLoadouts> PLAYER_LOADOUTS =
            CapabilityManager.get(new CapabilityToken<>() {
            });

    public static void register(RegisterCapabilitiesEvent event) {
        event.register(IPlayerLoadouts.class);
    }

}
