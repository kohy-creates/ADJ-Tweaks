package xyz.kohara.adjcore.misc;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.Nullable;
import oshi.util.tuples.Pair;
import xyz.kohara.adjcore.Config;
import xyz.kohara.adjcore.misc.events.ADJHurtEvent;
import xyz.kohara.adjcore.client.networking.ADJMessages;
import xyz.kohara.adjcore.client.networking.packet.DamageIndicatorS2CPacket;

import java.awt.*;

public class ParticleTextIndicators {

    public static void showIndicator(Entity atEntity,
                                     @Nullable LivingEntity offsetTo,
                                     float amount,
                                     Type type,
                                     int isCrit
    ) {
        double maxDistance = 40;

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
                            pos.x, pos.y, pos.z, amount, type.id(), isCrit
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
                (event.isCritical()) ? 1 : 0
        );
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    // lowest priority so that it applies after every form of healing modification
    public static void onEntityHeal(LivingHealEvent event) {
        LivingEntity entity = event.getEntity();

        if (
                entity.getHealth() != entity.getMaxHealth() &&
                        event.getAmount() > 2) {
            if (!entity.level().isClientSide()) {
                showIndicator(
                        entity,
                        null,
                        event.getAmount(),
                        Type.HEAL,
                        0
                );
            }
            entity.adjcore$setHealTime(8);
        }
    }

    public enum Type {
        DAMAGE_ENTITY(0, "#F58E27", "#FAAE64"),
        DAMAGE_PLAYER(1, "#9C0909", "#E33B3B"),
        HEAL(2, "#3BE346", "#7EE686"),
        CRIT(3, "#FF3300", "#FF7E42"),
        MANA(4, "#2787F5", "#2963E3"),
        FIRE(5, "#F55E27", "#F58E27", "🔥"),
        POISON(6, "#39782F", "#45A137"),
        WITHER(7, "#764857", "#6E2F3F"),
        EXPLOSION(8, "#F53C27", "#F56B51", "💥"),
        MIDNIGHT(9, "#F2FBFC", "#D9DDDE", "🌙"),
        ZAP(10, "#F5B027", "#F5AD27", "⚡");

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
            return values()[id];
        }
    }
}
