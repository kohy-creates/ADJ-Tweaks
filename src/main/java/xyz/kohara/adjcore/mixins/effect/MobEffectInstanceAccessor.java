package xyz.kohara.adjcore.mixins.effect;

import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MobEffectInstance.class)
public interface MobEffectInstanceAccessor {
    @Accessor("duration")
    void setDuration(int duration);
    @Accessor("amplifier")
    void setAmplifier(int amplifier);
}
