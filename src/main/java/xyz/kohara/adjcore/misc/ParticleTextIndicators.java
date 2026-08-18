package xyz.kohara.adjcore.misc;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.IExtensibleEnum;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.Nullable;
import oshi.util.tuples.Pair;
import xyz.kohara.adjcore.ADJCore;
import xyz.kohara.adjcore.Config;
import xyz.kohara.adjcore.misc.events.ADJHealEvent;
import xyz.kohara.adjcore.misc.events.ADJHurtEvent;
import xyz.kohara.adjcore.client.networking.ADJMessages;
import xyz.kohara.adjcore.client.networking.packet.DamageIndicatorS2CPacket;
import xyz.kohara.adjcore.registry.ADJDamageTypeTags;

import java.awt.*;
import java.util.function.UnaryOperator;

public class ParticleTextIndicators {

	public static void showIndicator(Entity atEntity,
									 @Nullable LivingEntity offsetTo,
									 float amount,
									 Type type,
									 boolean isCrit,
									 boolean isSmall
	) {
		double maxDistance = 64;

		atEntity.level().getServer().getPlayerList().getPlayers().forEach(viewer -> {
			if (viewer.distanceToSqr(atEntity) > maxDistance * maxDistance) return;

			Vec3 pos;

			// player took damage AND this viewer is the victim
			if (type == Type.DAMAGE_PLAYER
					&& viewer == atEntity && offsetTo != null) {
				pos = offsetTowardsEntity(atEntity, offsetTo);

			} else if (viewer == atEntity && offsetTo == null) {
				// place it slightly in front
				Vec3 eyePos = viewer.getEyePosition();
				Vec3 look = viewer.getLookAngle().normalize();

				double distance = 0.5D;
				double spread = 0.2D;

				pos = eyePos.add(
						look.x * distance + (Math.random() * spread * 2 - spread),
						look.y * distance + (Math.random() * spread * 2 - spread),
						look.z * distance + (Math.random() * spread * 2 - spread)
				);

			} else {
				// offset toward viewer
				pos = offsetTowardsEntity(atEntity, viewer);
			}

			ADJMessages.sendToPlayer(
					new DamageIndicatorS2CPacket(
							pos.x, pos.y, pos.z, amount, type.id(), (isCrit ? 1 : 0) + (isSmall ? 2 : 0)
					),
					viewer
			);
		});
	}

	private static Vec3 offsetTowardsEntity(Entity origin, Entity target) {
		Vec3 direction = target.position()
				.subtract(origin.position())
				.normalize();

		Vec3 base = new Vec3(
				origin.getX(),
				origin.getY() + origin.getEyeHeight(),
				origin.getZ()
		);

		double spread = 0.33d;

		double dx = direction.x * 0.66 + (Math.random() * spread * 2 - spread);
		double dy = direction.y * 0.66 + (Math.random() * spread * 2 - spread);
		double dz = direction.z * 0.66 + (Math.random() * spread * 2 - spread);

		return base.add(dx, dy, dz);
	}

	@SubscribeEvent
	public static void onADJHurt(ADJHurtEvent event) {

		float amount = event.getDamage();
		if (amount >= Config.Combat.maxDamageInOneHit) amount = Config.Combat.maxDamageInOneHit;

		Entity victim = event.getVictim();
		LivingEntity attacker = event.getAttacker();

		boolean isSmall = event.getSource().is(ADJDamageTypeTags.DAMAGE_OVER_TIME);

		Type type = event.getStyle();
		if (event.getStyle() == null) {
			type = Type.DAMAGE_ENTITY;
			if (victim instanceof ServerPlayer) {
				type = Type.DAMAGE_PLAYER;
			} else if (event.isCritical()) {
				type = Type.CRIT;
			}
		}

		showIndicator(
				victim,
				attacker,
				amount,
				type,
				event.isCritical(),
				isSmall
		);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	// lowest priority so that it applies after every form of healing modification
	public static void onEntityHeal(LivingHealEvent event) {
		if (ADJHealEvent.HealGuard.isADJHeal()) return; // won't fire twice for ADJ heal events
		LivingEntity entity = event.getEntity();

		if (
				entity.getHealth() != entity.getMaxHealth()
						&& event.getAmount() > 2
						&& !entity.level().isClientSide()
		) {
			showIndicator(
					entity,
					null,
					event.getAmount(),
					Type.HEAL,
					false,
					false
			);
		}
	}

	@SubscribeEvent
	public static void onADJHeal(ADJHealEvent event) {
		LivingEntity entity = event.getEntity();

		if (entity.getHealth() != entity.getMaxHealth()
				&& event.showIndicator
				&& !entity.level().isClientSide()
		) {
			showIndicator(
					entity,
					null,
					event.getAmount(),
					Type.HEAL,
					false,
					event.smallIndicator
			);
		}
	}

	public enum Type implements IExtensibleEnum {
		DAMAGE_ENTITY(0, "#F58E27", "#FAAE64"),
		DAMAGE_PLAYER(1, "#9C0909", "#E33B3B"),
		HEAL(2, "#3BE346", "#7EE686"),
		CRIT(3, "#FF3300", "#FF7E42"),
		MANA(4, "#2787F5", "#2963E3"),
		FIRE(5, "#F55E27", "#F58E27", "🔥"),
		POISON(6, "#39782F", "#45A137"),
		WITHER(7, "#764857", "#6E2F3F", "\uD83D\uDC80"),
		EXPLOSION(8, "#F53C27", "#F56B51", "💥"),
		MIDNIGHT(9, "#F2FBFC", "#D9DDDE", "🌙"),
		ZAP(10, "#F5B027", "#F5AD27", "⚡"),
		PLAYER_RANGED_DAMAGE(11, "#F5CF27", "#FFC300"),
		PLAYER_RANGED_CRIT(12, "#FFCE00", "#FFD042"),
		PLAYER_MAGIC_DAMAGE(13, "#BE27F5", "#C664FA"),
		PLAYER_MAGIC_CRIT(14, "#9500FF", "#A742FF"),
		PLAYER_SUMMON_DAMAGE(15, "#27D6F5", "#64E6FA"),
		PLAYER_RADIANT_DAMAGE(16, "#FCFA53", "#FAF064"),
		PLAYER_RADIANT_CRIT(17, "#FFF200", "#FFE642");

		private final int id;
		private final Pair<Color, Color> colors;
		private @Nullable String icon = null;

		Type(int id, String baseColor, String fadeColor) {
			this.id = id;
			this.colors = new Pair<>(Color.decode(baseColor), Color.decode(fadeColor));
		}

		Type(int id, String baseColor, String fadeColor, @Nullable String icon) {
			this.id = id;
			this.colors = new Pair<>(Color.decode(baseColor), Color.decode(fadeColor));
			this.icon = icon;
		}

		public int id() {
			return this.id;
		}

		public Pair<Color, Color> getColors() {
			return this.colors;
		}

		public @Nullable String getIcon() {
			return this.icon;
		}

		public static Type fromValue(int id) {
			for (var value : values()) {
				if (value.id == id) return value;
			}
			ADJCore.LOGGER.error("Attempted to find a ParticleTextIndicator.Type of unknown id {}!", id);
			return DAMAGE_ENTITY;
		}

		public static Type create(String name, int id, String baseColor, String fadeColor) {
			throw new IllegalStateException("Enum not extended");
		}

		public static Type create(String name, int id, String baseColor, String fadeColor, @Nullable String icon) {
			throw new IllegalStateException("Enum not extended");
		}
	}
}
