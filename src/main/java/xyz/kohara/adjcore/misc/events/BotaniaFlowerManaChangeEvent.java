package xyz.kohara.adjcore.misc.events;

import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.Event;
import vazkii.botania.api.block_entity.FunctionalFlowerBlockEntity;
import xyz.kohara.adjcore.compat.kubejs.ServerEvents;
import xyz.kohara.adjcore.compat.kubejs.serverevents.BotaniaFlowerManaChangeEventJS;

public class BotaniaFlowerManaChangeEvent extends Event {

	private final Block block;
	private final FunctionalFlowerBlockEntity blockEntity;
	private int amount;
	private final boolean isGeneration;

	public BotaniaFlowerManaChangeEvent(FunctionalFlowerBlockEntity blockEntity, int amount, boolean isGeneration) {
		this.blockEntity = blockEntity;
		this.block = this.blockEntity.getBlockState().getBlock();
		this.amount = amount;
		this.isGeneration = isGeneration;

		if (ServerEvents.BOTANIA_MANA_CHANGE.hasListeners())
			ServerEvents.BOTANIA_MANA_CHANGE.post(new BotaniaFlowerManaChangeEventJS(this));
	}

	public Block getBlock() {
		return this.block;
	}

	public FunctionalFlowerBlockEntity getFlower() {
		return this.blockEntity;
	}

	public int getAmount() {
		return this.amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public boolean isGeneration() {
		return this.isGeneration;
	}
}
