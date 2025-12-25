package xyz.kohara.adjcore.registry.effects;

import net.minecraft.network.protocol.game.ClientboundSetHealthPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class CozyCampfireEffect extends MobEffect {

    public CozyCampfireEffect() {
        super(MobEffectCategory.BENEFICIAL, 13458545);
    }

    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int pAmplifier) {
        if (!entity.level().isClientSide()) {
            if (entity.tickCount % 20 == 0 && entity.getHealth() < entity.getMaxHealth()) {
                entity.heal(0.5F);
                if (entity instanceof ServerPlayer player) {
                    player.connection.send(
                            new ClientboundSetHealthPacket(
                                    player.getHealth(),
                                    player.getFoodData().getFoodLevel(),
                                    player.getFoodData().getSaturationLevel()
                            )
                    );
                }
            }
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}
