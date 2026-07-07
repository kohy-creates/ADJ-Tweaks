package xyz.kohara.adjcore.misc.events;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.Nullable;
import xyz.kohara.adjcore.compat.kubejs.ServerEvents;
import xyz.kohara.adjcore.compat.kubejs.serverevents.ADJHealEventJS;

@Cancelable
public class ADJHealEvent extends LivingEvent {

	private float amount;
	private final Entity sourceEntity;
	private final String reason;

	public ADJHealEvent(float amount, LivingEntity entity, @Nullable Entity sourceEntity, @Nullable String reason) {
		super(entity);
		this.amount = amount;
		this.sourceEntity = sourceEntity;
		this.reason = reason;

		if (ServerEvents.ADJ_HEAL.hasListeners()) ServerEvents.ADJ_HEAL.post(new ADJHealEventJS(this));
	}

	public @Nullable Entity getSourceEntity() {
		return sourceEntity;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public String getReason() {
		return this.reason;
	}
}
