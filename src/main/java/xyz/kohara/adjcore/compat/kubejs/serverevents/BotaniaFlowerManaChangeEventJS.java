package xyz.kohara.adjcore.compat.kubejs.serverevents;

import dev.latvian.mods.kubejs.event.EventJS;
import net.minecraft.world.level.block.Block;
import vazkii.botania.api.block_entity.FunctionalFlowerBlockEntity;
import xyz.kohara.adjcore.misc.events.BotaniaFlowerManaChangeEvent;

public class BotaniaFlowerManaChangeEventJS extends EventJS {
	private final BotaniaFlowerManaChangeEvent event;

	public BotaniaFlowerManaChangeEventJS(BotaniaFlowerManaChangeEvent event) {
		this.event = event;
	}

	public Block getBlock() {
		return this.event.getBlock();
	}

	public FunctionalFlowerBlockEntity getFlower() {
		return this.event.getFlower();
	}

	public int getAmount() {
		return this.event.getAmount();
	}

	public void setAmount(int amount) {
		this.event.setAmount(amount);
	}

	public boolean isGeneration() {
		return this.event.isGeneration();
	}
}
