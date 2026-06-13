package xyz.kohara.adjcore.misc.events;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.eventbus.api.Event;
import xyz.kohara.adjcore.compat.kubejs.ServerEvents;
import xyz.kohara.adjcore.compat.kubejs.serverevents.ItemRarityGetEventJS;

public class ItemRarityGetEvent extends Event {

	public Rarity rarity = null;
	private final ItemStack itemStack;

	public ItemRarityGetEvent(ItemStack itemStack) {
		this.itemStack = itemStack;

		if (ServerEvents.ITEM_RARITY_GET_EVENT.hasListeners()) {
			ServerEvents.ITEM_RARITY_GET_EVENT.post(new ItemRarityGetEventJS(this));
		}
	}

	public ItemStack getItemStack() {
		return this.itemStack;
	}

	public Rarity getBaseRarity() {
		return itemStack.getRarity();
	}

	public void setRarity(Rarity rarity) {
		this.rarity = rarity;
	}

}
