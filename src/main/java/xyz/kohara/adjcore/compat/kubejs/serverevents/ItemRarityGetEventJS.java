package xyz.kohara.adjcore.compat.kubejs.serverevents;

import dev.latvian.mods.kubejs.event.EventJS;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import xyz.kohara.adjcore.misc.events.ItemRarityGetEvent;

public class ItemRarityGetEventJS extends EventJS {

	private final ItemRarityGetEvent event;

	public ItemRarityGetEventJS(ItemRarityGetEvent event) {
		this.event = event;
	}

	public Rarity getBaseRarity() {
		return this.event.getBaseRarity();
	}

	public ItemStack getItemStack() {
		return this.event.getItemStack();
	}

	public void setRarity(Rarity rarity) {
		this.event.setRarity(rarity);
	}
}
