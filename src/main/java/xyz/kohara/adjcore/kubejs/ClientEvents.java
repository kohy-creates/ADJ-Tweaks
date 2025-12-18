package xyz.kohara.adjcore.kubejs;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import xyz.kohara.adjcore.kubejs.clientevents.ItemIsLockedRenderCheckEventJS;
import xyz.kohara.adjcore.kubejs.serverevents.ADJHurtEventJS;
import xyz.kohara.adjcore.kubejs.serverevents.RecipeLookupEventJS;

public interface ClientEvents {
    EventGroup GROUP = EventGroup.of("ADJClientEvents");

    EventHandler IS_LOCKED_RENDER_CHECK = GROUP.client("itemIsLockedRenderCheck", () -> ItemIsLockedRenderCheckEventJS.class).hasResult();

}
