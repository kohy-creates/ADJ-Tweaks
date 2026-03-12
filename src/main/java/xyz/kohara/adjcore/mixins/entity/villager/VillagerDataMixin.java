package xyz.kohara.adjcore.mixins.entity.villager;

import net.minecraft.world.entity.npc.VillagerData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VillagerData.class)
public abstract class VillagerDataMixin {

    @Shadow @Final @Mutable private static int[] NEXT_LEVEL_XP_THRESHOLDS;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void increaseLevelAmounts(CallbackInfo ci) {
        NEXT_LEVEL_XP_THRESHOLDS = new int[]{0, 35, 220, 500, 1050};
    }
}
