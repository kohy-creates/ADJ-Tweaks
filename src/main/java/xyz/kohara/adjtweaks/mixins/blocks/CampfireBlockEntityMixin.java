package xyz.kohara.adjtweaks.mixins.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.kohara.adjtweaks.campfire.CozyCampfire;

@Mixin(CampfireBlockEntity.class)
public class CampfireBlockEntityMixin {
    @Inject(method = "litServerTick", at = @At("HEAD"))
    private static void applyCozyCampfire(World world, BlockPos pos, BlockState state, CampfireBlockEntity campfire, CallbackInfo ci) {
        CozyCampfire.applyEffects(world, pos);
    }

    // Maybe it will fix some issues with other mods?
    @Inject(method = "unlitServerTick", at = @At("HEAD"))
    private static void applyCozyCampfireUnlit(World world, BlockPos pos, BlockState state, CampfireBlockEntity campfire, CallbackInfo ci) {
        if (state.get(CampfireBlock.LIT)) CozyCampfire.applyEffects(world, pos);
    }
}
