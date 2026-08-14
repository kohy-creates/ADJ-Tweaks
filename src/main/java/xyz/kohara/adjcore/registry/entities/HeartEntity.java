package xyz.kohara.adjcore.registry.entities;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import xyz.kohara.adjcore.registry.ADJEntities;
import xyz.kohara.adjcore.registry.ADJItems;

import java.util.List;

public class HeartEntity extends CollectibleEntity {

	public HeartEntity(EntityType<? extends CollectibleEntity> entityType, Level level) {
		super(entityType, level);
	}

	public HeartEntity(Level level, double x, double y, double z) {
		super(ADJEntities.HEART.get(), level, x, y, z);
	}

	@Override
	public void pickupEffects(@NotNull Player player) {
		player.adjcore$heal(20, null, "heart", true, false);
		super.pickupEffects(player);
	}

	@Override
	public @NotNull Item displayItem() {
		return ADJItems.HEART.get();
	}

	@Override
	public @NotNull PickupSound pickupSounds() {
		return new PickupSound(
				List.of(
						SoundEvents.EXPERIENCE_ORB_PICKUP,
						SoundEvents.ITEM_PICKUP
				),
				0,
				1.4f,
				0.4f);
	}
}
