package xyz.kohara.adjtweaks.mixins.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.ConduitBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
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
    private static Box aDJTweaks$getAttackRange(BlockPos pos) {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        return (new Box(i, j, k, i + 1, j + 1, k + 1)).expand(aDJTweaks$CONDUIT_RANGE);
    }

    @Inject(
            method = "attackHostileEntity",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void multiTargetAttack(World world, BlockPos pos, BlockState state, List<BlockPos> activatingBlocks, ConduitBlockEntity blockEntity, CallbackInfo ci) {
        int i = activatingBlocks.size();
        if (i < 42) return;

        List<LivingEntity> targets = world.getEntitiesByClass(LivingEntity.class,
                aDJTweaks$getAttackRange(pos),
                (entity) -> entity instanceof Monster && entity.isTouchingWaterOrRain());

        if (targets.isEmpty()) return;

        for (LivingEntity target : targets) {
            if (pos.isWithinDistance(target.getBlockPos(), aDJTweaks$CONDUIT_RANGE)) {
                float aDJTweaks$CONDUIT_DAMAGE = 2.0f;
                target.damage(world.getDamageSources().magic(), aDJTweaks$CONDUIT_DAMAGE);
                world.playSound(null, target.getX(), target.getY(), target.getZ(),
                        SoundEvents.BLOCK_CONDUIT_ATTACK_TARGET, SoundCategory.BLOCKS, 0.75F, 0.85f + new Random().nextFloat() * (1.15f - 0.85f));
            }
        }
        ci.cancel();
    }
}
