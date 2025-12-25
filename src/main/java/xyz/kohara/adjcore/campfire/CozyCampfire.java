package xyz.kohara.adjcore.campfire;


import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import xyz.kohara.adjcore.Config;
import xyz.kohara.adjcore.registry.ADJEffects;

import java.util.List;

public class CozyCampfire {

    public static boolean isCampfireSignal(Level world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return (state.getBlock() instanceof CampfireBlock && state.getValue(BlockStateProperties.SIGNAL_FIRE));
    }

    private static boolean isPassiveMob(Entity entity) {
        return !(entity.getType().getCategory() == MobCategory.MONSTER);
    }

    public static void applyEffects(Level world, BlockPos pos) {
        if (!world.isClientSide()) {
            double radius = (isCampfireSignal(world, pos)) ? Config.CAMPFIRE_HEAL_RADIUS_SIGNAL.get() : Config.CAMPFIRE_HEAL_RADIUS.get();
            AABB area = new AABB(pos).inflate(radius);
            List<LivingEntity> list = world.getEntitiesOfClass(LivingEntity.class, area, livingEntity -> isPassiveMob(livingEntity) && livingEntity.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= (radius * radius));
            MobEffectInstance cozyCampfireEffect = new MobEffectInstance(ADJEffects.COZY_CAMPFIRE.get(),16, 0, true, true, true);
            for (LivingEntity entity : list) {
                entity.addEffect(cozyCampfireEffect);
            }
        }
    }
}
