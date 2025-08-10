package xyz.kohara.adjcore.mixins.effect;

import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEffectUtil.class)
public class MobEffectUtilMixin {

    @Inject(method = "formatDuration", at = @At("HEAD"), cancellable = true)
    private static void formatDuration(MobEffectInstance effect, float durationFactor, CallbackInfoReturnable<Component> cir) {
        if (effect.isInfiniteDuration()) {
            cir.setReturnValue(Component.translatable("effect.duration.infinite"));
        } else if (effect.isAmbient()) {
            cir.setReturnValue(Component.empty());
        } else {
            int i = Mth.floor((float) effect.getDuration() * durationFactor);
            cir.setReturnValue(Component.literal(StringUtil.formatTickDuration(i)));
        }
    }
}
