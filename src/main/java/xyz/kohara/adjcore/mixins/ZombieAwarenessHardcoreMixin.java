package xyz.kohara.adjcore.mixins;

import com.corosus.coroutil.util.CoroUtilWorldTime;
import com.corosus.zombieawareness.ZAUtil;
import com.corosus.zombieawareness.config.ZAConfigGeneral;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ZAUtil.class, remap = false)
public class ZombieAwarenessHardcoreMixin {

    @Inject(
            method = "isZombieAwarenessActive",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void isZombieAwarenessActive(Level world, CallbackInfoReturnable<Boolean> cir) {
        cir.cancel();
        if (world == null || !world.getLevelData().isHardcore()) cir.setReturnValue(false);
        if (ZAConfigGeneral.daysBeforeFeaturesActivate <= 0) cir.setReturnValue(true);
        double day = ((double) world.getDayTime() / CoroUtilWorldTime.getDayLength());
        if (day >= ZAConfigGeneral.daysBeforeFeaturesActivate) {
            cir.setReturnValue(true);
        } else {
            cir.setReturnValue(false);
        }
    }
}
