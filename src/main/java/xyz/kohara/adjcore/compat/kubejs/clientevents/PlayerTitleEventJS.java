package xyz.kohara.adjcore.compat.kubejs.clientevents;

import dev.latvian.mods.kubejs.event.EventJS;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import xyz.kohara.adjcore.client.misc.events.PlayerTitleEvent;

public class PlayerTitleEventJS extends EventJS {

    private final PlayerTitleEvent event;

    public PlayerTitleEventJS(PlayerTitleEvent event) {
        this.event = event;
    }

    public @Nullable Component getTitle() {
        return this.event.getTitle();
    }

    public void setTitle(Component component) {
        this.event.setTitle(component);
    }

    public LocalPlayer getPlayer() {
        return this.event.getPlayer();
    }
}
