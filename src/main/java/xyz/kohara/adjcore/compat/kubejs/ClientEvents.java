package xyz.kohara.adjcore.compat.kubejs;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import xyz.kohara.adjcore.compat.kubejs.clientevents.ItemIsLockedRenderCheckEventJS;

public interface ClientEvents {
    EventGroup GROUP = EventGroup.of("ADJClientEvents");

    EventHandler IS_LOCKED_RENDER_CHECK = GROUP.client("itemIsLockedRenderCheck", () -> ItemIsLockedRenderCheckEventJS.class).hasResult();

}
