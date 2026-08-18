package xyz.kohara.adjcore.registry.effects;

import net.minecraft.world.effect.InstantenousMobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class InstantManaEffect extends InstantenousMobEffect {
    public InstantManaEffect() {
        super(MobEffectCategory.BENEFICIAL, 16543);
    }

    @Override
    public void applyInstantenousEffect(@Nullable Entity source, @Nullable Entity indirectSource, @NotNull LivingEntity livingEntity, int amplifier, double health) {
        if (livingEntity instanceof Player player) {
            player.adjcore$restoreMana(70 * amplifier, true);
        }
    }

}
