package xyz.kohara.adjcore.combat;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xyz.kohara.adjcore.misc.events.ADJHurtEvent;
import xyz.kohara.adjcore.client.networking.ModMessages;
import xyz.kohara.adjcore.client.networking.packet.DamageIndicatorS2CPacket;

public class DamageIndicators {

    private void showIndicator(Entity entity,
                               double x,
                               double y,
                               double z,
                               float amount,
                               int type
    ) {
        double maxDistance = 48;

        entity.level().getServer().getPlayerList().getPlayers().forEach(player -> {
            if (player.distanceToSqr(entity) <= Math.pow(maxDistance, 2)) {
                Vec3 pos = offsetTowardsViewer(entity, player, 0.66);
                ModMessages.sendToPlayer(new DamageIndicatorS2CPacket(pos.x, pos.y, pos.z, amount, type), player);
            }
        });
    }

    private Vec3 offsetTowardsViewer(Entity entity, Entity viewer, double forwardDistance) {
        Vec3 direction = entity.position().subtract(viewer.position()).normalize().scale(-1);
        Vec3 base = new Vec3(entity.getX(), entity.getY() + entity.getEyeHeight(), entity.getZ());
        double spread = 0.33d;
        double dx = direction.x * forwardDistance + (Math.random() * (spread * 2d) - spread);
        double dy = direction.y * forwardDistance + (Math.random() * (spread * 2d) - spread);
        double dz = direction.z * forwardDistance + (Math.random() * (spread * 2d) - spread);

        return base.add(dx, dy, dz);
    }

    @SubscribeEvent
    public void showDamageParticle(ADJHurtEvent event) {

        Entity victim = event.getVictim();
        LivingEntity attacker = event.getAttacker();

        int type = Type.DAMAGE_ENTITY.get();
        if (victim instanceof ServerPlayer) {
            type = Type.DAMAGE_PLAYER.get();
        }
        else if (event.isCritical()) {
            type = Type.CRIT.get();
        }

        double x = victim.getX();
        double y = victim.getY() + victim.getEyeHeight();
        double z = victim.getZ();

        showIndicator(victim, x, y, z, event.getDamage(), type);
    }

    @SubscribeEvent
    public void onEntityHeal(LivingHealEvent event) {
        LivingEntity entity = event.getEntity();

        if (!entity.level().isClientSide()
                && entity.getHealth() != entity.getMaxHealth()
                && event.getAmount() > 2) {

            double x = entity.getX();
            double y = entity.getY() + entity.getEyeHeight();
            double z = entity.getZ();

            showIndicator(entity, x, y, z, event.getAmount(), Type.HEAL.get());
        }

    }

    public enum Type {
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
