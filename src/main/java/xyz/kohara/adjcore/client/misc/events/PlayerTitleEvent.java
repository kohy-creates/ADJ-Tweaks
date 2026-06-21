package xyz.kohara.adjcore.client.misc.events;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.Nullable;
import xyz.kohara.adjcore.compat.kubejs.ClientEvents;
import xyz.kohara.adjcore.compat.kubejs.clientevents.PlayerTitleEventJS;

public class PlayerTitleEvent extends Event {

    private final LocalPlayer player;
    public Component title = null;

    public PlayerTitleEvent(LocalPlayer player) {
        this.player = player;

        if (ClientEvents.PLAYER_TITLE_EVENT.hasListeners()) {
            ClientEvents.PLAYER_TITLE_EVENT.post(new PlayerTitleEventJS(this));
        }
    }

    public LocalPlayer getPlayer() {
        return this.player;
    }

    public @Nullable Component getTitle() {
        return title;
    }

    public void setTitle(Component component) {
        title = component;
    }

    public void setTitle(String string) {
        setTitle(Component.literal(string));
    }
}
