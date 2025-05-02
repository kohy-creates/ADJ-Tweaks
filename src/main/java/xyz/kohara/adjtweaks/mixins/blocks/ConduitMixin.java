package xyz.kohara.adjtweaks.mixins.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ConduitBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Random;

@Mixin(ConduitBlockEntity.class)
public class ConduitMixin {

    @Unique
    private static float aDJTweaks$CONDUIT_RANGE = 48.0f;

    @Unique
    private static AABB aDJTweaks$getAttackRange(BlockPos pos) {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        return (new AABB(i, j, k, i + 1, j + 1, k + 1)).inflate(aDJTweaks$CONDUIT_RANGE);
    }

    @Inject(
            method = "updateDestroyTarget",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void multiTargetAttack(Level level, BlockPos pos, BlockState state, List<BlockPos> positions, ConduitBlockEntity blockEntity, CallbackInfo ci) {
        int i = positions.size();
        if (i < 42) return;

        List<Monster> targets = level.getEntitiesOfClass(Monster.class,
                aDJTweaks$getAttackRange(pos),
                (entity) -> entity.isInWaterRainOrBubble());

        if (targets.isEmpty()) return;

        for (LivingEntity target : targets) {
            if (pos.closerThan(target.getOnPos(), aDJTweaks$CONDUIT_RANGE)) {
                float aDJTweaks$CONDUIT_DAMAGE = 2.0f;
                target.hurt(level.damageSources().magic(), aDJTweaks$CONDUIT_DAMAGE);
                level.playSound(null, target.getX(), target.getY(), target.getZ(),
                        SoundEvents.CONDUIT_ATTACK_TARGET, SoundSource.BLOCKS, 0.75F, 0.85f + new Random().nextFloat() * (1.15f - 0.85f));
            }
        }
        ci.cancel();
    }
}
