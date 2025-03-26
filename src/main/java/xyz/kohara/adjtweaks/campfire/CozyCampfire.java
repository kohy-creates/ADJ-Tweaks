package xyz.kohara.adjtweaks.campfire;


import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import xyz.kohara.adjtweaks.Config;
import xyz.kohara.adjtweaks.effects.ModEffects;

import java.util.List;

public class CozyCampfire {
    public static boolean isCampfireSignal(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return (state.getBlock() instanceof CampfireBlock && state.get(Properties.SIGNAL_FIRE));
    }

    private static boolean isPassiveMob(Entity entity) {
        return !(entity.getType().getSpawnGroup() == SpawnGroup.MONSTER);
    }

    public static void applyEffects(World world, BlockPos pos) {
        if (!world.isClient()) {
            double radius = (isCampfireSignal(world, pos)) ? Config.CAMPFIRE_HEAL_RADIUS_SIGNAL.get() : Config.CAMPFIRE_HEAL_RADIUS.get();
            Box area = new Box(pos).expand(radius);
            List<LivingEntity> list = world.getEntitiesByClass(LivingEntity.class, area, livingEntity -> isPassiveMob(livingEntity) && livingEntity.squaredDistanceTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= (radius * radius));
            StatusEffectInstance cozyCampfireEffect = new StatusEffectInstance(ModEffects.COZY_CAMPFIRE.get(),16, 0, true, true, true);
            for (LivingEntity entity : list) {
                entity.addStatusEffect(cozyCampfireEffect);
            }
        }
    }
}
