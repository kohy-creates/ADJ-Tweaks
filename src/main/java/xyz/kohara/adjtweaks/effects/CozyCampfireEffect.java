package xyz.kohara.adjtweaks.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

public class CozyCampfireEffect extends StatusEffect {

    public CozyCampfireEffect() {
        super(StatusEffectCategory.BENEFICIAL, 13458545);
    }

    @Override
    public void applyUpdateEffect(@NotNull LivingEntity entity, int pAmplifier) {
        if (!entity.getWorld().isClient()) {
            if (entity.age % 100 == 0 && entity.getHealth() < entity.getMaxHealth()) {
                entity.heal(1.0F);
                if (entity instanceof ServerPlayerEntity player) {
                    player.networkHandler.sendPacket(
                            new HealthUpdateS2CPacket(
                                    player.getHealth(),
                                    player.getHungerManager().getFoodLevel(),
                                    player.getHungerManager().getSaturationLevel()
                            )
                    );
                }
            }
        }
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

}
