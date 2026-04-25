package xyz.kohara.adjcore.client.misc.events;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.registries.ForgeRegistries;
import xyz.kohara.adjcore.compat.kubejs.ClientEvents;
import xyz.kohara.adjcore.compat.kubejs.clientevents.FindMusicEventJS;

import org.jetbrains.annotations.Nullable;

public class ADJFindMusicEvent extends Event {

    private final @Nullable LocalPlayer player;
    private final @Nullable ClientLevel level;
    private @Nullable String soundEvent;

    public ADJFindMusicEvent(@Nullable LocalPlayer player, @Nullable ClientLevel level) {
        this.player = player;
        this.level = level;

        if (ClientEvents.FIND_MUSIC_EVENT.hasListeners())
            ClientEvents.FIND_MUSIC_EVENT.post(new FindMusicEventJS(this));
    }

    @Nullable
    public LocalPlayer getPlayer() {
        return player;
    }

    @Nullable
    public ClientLevel getLevel() {
        return level;
    }

    public @Nullable Music getMusic() {
        if (getSoundEvent() != null) {
            var holder = ForgeRegistries.SOUND_EVENTS.getHolder(ResourceLocation.parse(getSoundEvent()));
            return holder.map(soundEventHolder -> new Music(soundEventHolder, 0, 1, false)).orElse(null);
        }
        return null;
    }

    public @Nullable String getSoundEvent() {
        return soundEvent;
    }

    public void setSoundEvent(@Nullable String soundEvent) {
        this.soundEvent = soundEvent;
    }
}
