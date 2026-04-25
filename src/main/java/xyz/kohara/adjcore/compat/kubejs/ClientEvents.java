package xyz.kohara.adjcore.compat.kubejs;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import xyz.kohara.adjcore.compat.kubejs.clientevents.FindMusicEventJS;
import xyz.kohara.adjcore.compat.kubejs.clientevents.ItemIsLockedRenderCheckEventJS;
import xyz.kohara.adjcore.compat.kubejs.clientevents.PlayerTitleEventJS;

public interface ClientEvents {
    EventGroup GROUP = EventGroup.of("ADJClientEvents");

    EventHandler IS_LOCKED_RENDER_CHECK = GROUP.client("itemIsLockedRenderCheck", () -> ItemIsLockedRenderCheckEventJS.class).hasResult();
    EventHandler PLAYER_TITLE_EVENT = GROUP.client("playerTitle", () -> PlayerTitleEventJS.class);
    EventHandler FIND_MUSIC_EVENT = GROUP.client("findMusicEvent", () -> FindMusicEventJS.class);
}
