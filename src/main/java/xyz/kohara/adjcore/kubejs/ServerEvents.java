package xyz.kohara.adjcore.kubejs;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import xyz.kohara.adjcore.combat.critevent.CritStrikeEventJS;

public interface ServerEvents {
    EventGroup GROUP = EventGroup.of("ADJServerEvents");

    EventHandler APOTH_CRIT_STRIKE = GROUP.server("apothCritStrike", () -> CritStrikeEventJS.class);
}
