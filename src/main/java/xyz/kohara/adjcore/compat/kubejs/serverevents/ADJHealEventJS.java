package xyz.kohara.adjcore.compat.kubejs.serverevents;

import dev.latvian.mods.kubejs.event.EventJS;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import xyz.kohara.adjcore.misc.events.ADJHealEvent;

public class ADJHealEventJS extends EventJS {

	private final ADJHealEvent event;

	public ADJHealEventJS(ADJHealEvent event) {
		this.event = event;
	}

	public float getAmount() {
		return this.event.getAmount();
	}

	public void setAmount(float amount) {
		this.event.setAmount(amount);
	}

	public @Nullable Entity getSourceEntity() {
		return this.event.getSourceEntity();
	}

	public LivingEntity getEntity() {
		return this.event.getEntity();
	}

	public @Nullable String getReason() {
		return this.event.getReason();
	}
}
