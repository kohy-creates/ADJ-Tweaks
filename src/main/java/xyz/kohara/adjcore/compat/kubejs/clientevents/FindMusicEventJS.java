package xyz.kohara.adjcore.compat.kubejs.clientevents;

import dev.latvian.mods.kubejs.event.EventJS;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.Music;
import xyz.kohara.adjcore.client.misc.events.ADJFindMusicEvent;

import javax.annotation.Nullable;

public class FindMusicEventJS extends EventJS {

    private final ADJFindMusicEvent event;

    public FindMusicEventJS(ADJFindMusicEvent event) {
        this.event = event;
    }

    @Nullable
    public LocalPlayer getPlayer() {
        return event.getPlayer();
    }

    @Nullable
    public ClientLevel getLevel() {
        return event.getLevel();
    }

    public Music getMusic() {
        return event.getMusic();
    }

    public String getSoundEvent() {
        return event.getSoundEvent();
    }

    public void setSoundEvent(String soundEvent) {
        event.setSoundEvent(soundEvent);
    }
}
