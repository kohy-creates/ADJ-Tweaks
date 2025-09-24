package xyz.kohara.adjcore.kubejs;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import xyz.kohara.adjcore.combat.damageevent.ADJHurtEventJS;

public interface ServerEvents {
    EventGroup GROUP = EventGroup.of("ADJServerEvents");

    EventHandler ADJ_HURT = GROUP.server("apothCritStrike", () -> ADJHurtEventJS.class);
}
