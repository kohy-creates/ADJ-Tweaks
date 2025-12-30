package xyz.kohara.adjcore.combat;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xyz.kohara.adjcore.misc.events.ADJHurtEvent;
import xyz.kohara.adjcore.client.networking.ADJMessages;
import xyz.kohara.adjcore.client.networking.packet.DamageIndicatorS2CPacket;

public class DamageIndicators {

    private void showIndicator(Entity victim,
                               LivingEntity attacker,
                               float amount,
                               int type
    ) {
        double maxDistance = 48;

        victim.level().getServer().getPlayerList().getPlayers().forEach(viewer -> {
            if (viewer.distanceToSqr(victim) > maxDistance * maxDistance) return;

            Vec3 pos;

            // Player took damage AND this viewer is the victim
            if (type == Type.DAMAGE_PLAYER.get()
                    && viewer == victim
                    && attacker != null) {

                pos = offsetTowardsEntity(victim, attacker);

            } else {
                // Default behavior: offset toward viewer
                pos = offsetTowardsEntity(victim, viewer);
            }

            ADJMessages.sendToPlayer(
                    new DamageIndicatorS2CPacket(
                            pos.x, pos.y, pos.z, amount, type
                    ),
                    viewer
            );
        });
    }

    private Vec3 offsetTowardsEntity(Entity origin, Entity target) {
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
    public void showDamageParticle(ADJHurtEvent event) {

        if (event.getDamage() == Integer.MAX_VALUE) return;

        Entity victim = event.getVictim();
        LivingEntity attacker = event.getAttacker();

        int type = Type.DAMAGE_ENTITY.get();
        if (victim instanceof ServerPlayer) {
            type = Type.DAMAGE_PLAYER.get();
        } else if (event.isCritical()) {
            type = Type.CRIT.get();
        }

        showIndicator(
                victim,
                attacker,
                event.getDamage(),
                type
        );
    }

    @SubscribeEvent
    public void onEntityHeal(LivingHealEvent event) {
        LivingEntity entity = event.getEntity();

        if (entity.getHealth() != entity.getMaxHealth() && event.getAmount() > 2) {
            if (!entity.level().isClientSide()) {
                showIndicator(
                        entity,
                        null,
                        event.getAmount(),
                        Type.HEAL.get()
                );
            }
            entity.adjcore$setHealTime(8);
        }
    }

    private enum Type {
        DAMAGE_ENTITY(0),
        DAMAGE_PLAYER(1),
        HEAL(2),
        CRIT(3);

        private final int type;

        Type(int type) {
            this.type = type;
        }

        public int get() {
            return this.type;
        }
    }
}
