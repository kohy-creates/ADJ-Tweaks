package xyz.kohara.adjcore.kubejs;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import xyz.kohara.adjcore.kubejs.serverevents.ADJHurtEventJS;
import xyz.kohara.adjcore.kubejs.serverevents.RecipeLookupEventJS;

public interface ServerEvents {
    EventGroup GROUP = EventGroup.of("ADJServerEvents");

    EventHandler ADJ_HURT = GROUP.server("adjHurt", () -> ADJHurtEventJS.class);
    EventHandler RECIPE_LOOKUP = GROUP.server("recipeLookup", () -> RecipeLookupEventJS.class).hasResult();

}
