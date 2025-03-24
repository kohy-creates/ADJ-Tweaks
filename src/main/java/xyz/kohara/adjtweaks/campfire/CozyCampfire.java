package xyz.kohara.adjtweaks.campfire;

import net.minecraft.core.BlockPos;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import xyz.kohara.adjtweaks.ConfigHandler;
import xyz.kohara.adjtweaks.effect.ModEffects;

import java.util.List;

public class CozyCampfire {
    public static boolean isCampfireSignal(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.getBlock() instanceof CampfireBlock && state.getValue(CampfireBlock.SIGNAL_FIRE);
    }

    private static boolean isPassiveMob(Entity entity) {
        return !(entity.getType().getCategory() == MobCategory.MONSTER);
    }

    public static void applyEffects(Level level, BlockPos pos) {
        if (!level.isClientSide()) {
            double radius = (isCampfireSignal(level, pos)) ? ConfigHandler.CAMPFIRE_HEAL_RADIUS_SIGNAL.get() : ConfigHandler.CAMPFIRE_HEAL_RADIUS.get();
            AABB area = new AABB(pos).inflate(radius);
            List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, area, livingEntity -> {
                return isPassiveMob(livingEntity) && livingEntity.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= (radius * radius);
            });
            MobEffectInstance cozyCampfireEffect = new MobEffectInstance(ModEffects.COZY_CAMPFIRE.get(),16, 0, true, true, true);
            for (LivingEntity entity : list) {
                entity.addEffect(cozyCampfireEffect);
            }
        }
    }
}
