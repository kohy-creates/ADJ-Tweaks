package xyz.kohara.adjcore.compat.kubejs;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import xyz.kohara.adjcore.compat.kubejs.serverevents.*;

public interface ServerEvents {
	EventGroup GROUP = EventGroup.of("ADJServerEvents");

	EventHandler ADJ_HURT = GROUP.server("adjHurt", () -> ADJHurtEventJS.class);
	EventHandler RECIPE_LOOKUP = GROUP.server("recipeLookup", () -> RecipeLookupEventJS.class).hasResult();
	EventHandler EXPLOSION_DAMAGE_CALC = GROUP.server("explosionDamageCalc", () -> ADJExplosionDamageCalcEventJS.class);
	EventHandler ITEM_RARITY_GET_EVENT = GROUP.server("itemRarityGet", () -> ItemRarityGetEventJS.class);
	EventHandler BOTANIA_MANA_CHANGE = GROUP.server("botaniaFlowerManaChange", () -> BotaniaFlowerManaChangeEventJS.class);
}
