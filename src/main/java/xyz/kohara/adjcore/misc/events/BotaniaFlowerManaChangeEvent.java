package xyz.kohara.adjcore.misc.events;

import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.Event;
import vazkii.botania.api.block_entity.FunctionalFlowerBlockEntity;
import vazkii.botania.api.block_entity.GeneratingFlowerBlockEntity;
import xyz.kohara.adjcore.compat.kubejs.ServerEvents;
import xyz.kohara.adjcore.compat.kubejs.serverevents.BotaniaFlowerManaChangeEventJS;

public class BotaniaFlowerManaChangeEvent extends Event {

	private final Block block;
	private final GeneratingFlowerBlockEntity generatingFlower;
	private final FunctionalFlowerBlockEntity functionalFlower;
	private int amount;
	private final boolean isGeneration;

	public BotaniaFlowerManaChangeEvent(FunctionalFlowerBlockEntity blockEntity, int amount) {
		this.generatingFlower = null;
		this.functionalFlower = blockEntity;
		this.block = this.functionalFlower.getBlockState().getBlock();
		this.amount = amount;
		this.isGeneration = false;

		if (ServerEvents.BOTANIA_MANA_CHANGE.hasListeners())
			ServerEvents.BOTANIA_MANA_CHANGE.post(new BotaniaFlowerManaChangeEventJS(this));
	}

	public BotaniaFlowerManaChangeEvent(GeneratingFlowerBlockEntity blockEntity, int amount) {
		this.generatingFlower = blockEntity;
		this.functionalFlower = null;
		this.block = this.generatingFlower.getBlockState().getBlock();
		this.amount = amount;
		this.isGeneration = true;

		if (ServerEvents.BOTANIA_MANA_CHANGE.hasListeners())
			ServerEvents.BOTANIA_MANA_CHANGE.post(new BotaniaFlowerManaChangeEventJS(this));
	}

	public Block getBlock() {
		return this.block;
	}

	public FunctionalFlowerBlockEntity getFunctionalFlower() {
		return this.functionalFlower;
	}

	public GeneratingFlowerBlockEntity getGeneratingFlower() {
		return this.generatingFlower;
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
