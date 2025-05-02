package xyz.kohara.adjtweaks.mixins.blocks;

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
public class CampfireBlockEntityMixin {
    @Inject(method = "cookTick", at = @At("TAIL"))
    private static void applyCozyCampfire(Level world, BlockPos pos, BlockState state, CampfireBlockEntity campfire, CallbackInfo ci) {
        CozyCampfire.applyEffects(world, pos);
    }
}
