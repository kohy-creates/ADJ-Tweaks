package xyz.kohara.adjcore.combat;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xyz.kohara.adjcore.combat.damageevent.ADJHurtEvent;
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

        entity.level().getServer().getPlayerList().getPlayers().forEach(serverPlayer -> {
            if (serverPlayer.distanceToSqr(entity) <= Math.pow(maxDistance, 2)) {
                ModMessages.sendToPlayer(new DamageIndicatorS2CPacket(x, y, z, amount, type), serverPlayer);
            }
        });
    }


    private double particleOffset(double base) {
        double offset = 0.5d;
        return base + (Math.random() * (offset * 2d) - offset);
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

        double x = particleOffset(victim.getX());
        double y = particleOffset(victim.getY() + victim.getEyeHeight());
        double z = particleOffset(victim.getZ());

        showIndicator(victim, x, y, z, event.getDamage(), type);
    }

    @SubscribeEvent
    public void onEntityHeal(LivingHealEvent event) {
        LivingEntity entity = event.getEntity();

        if (!entity.level().isClientSide()
                && entity.getHealth() != entity.getMaxHealth()
                && event.getAmount() > 2) {

            double x = particleOffset(entity.getX());
            double y = particleOffset(entity.getY() + entity.getEyeHeight());
            double z = particleOffset(entity.getZ());

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
