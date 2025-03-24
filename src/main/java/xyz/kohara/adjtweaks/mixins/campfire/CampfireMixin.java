package xyz.kohara.adjtweaks.mixins.campfire;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.kohara.adjtweaks.campfire.CozyCampfire;

@Mixin(CampfireBlockEntity.class)
public class CampfireMixin {
    @Inject(method = "cookTick", at = @At("HEAD"))
    private static void onCampfireTick(Level level, BlockPos pos, BlockState state, CampfireBlockEntity campfire, CallbackInfo ci) {
        CozyCampfire.applyEffects(level, pos);
    }
}
