package xyz.kohara.adjcore.entity;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xyz.kohara.adjcore.misc.DelayedTaskScheduler;
import xyz.kohara.adjcore.registry.capabilities.BellTraderDataProvider;

public class WanderingTraderEdits {

    @SubscribeEvent
    public void onBellUse(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        if (level.isClientSide) return;

        BlockPos pos = event.getPos();
        Player player = event.getEntity();
        BlockState state = level.getBlockState(pos);

        if (!(state.getBlock() instanceof BellBlock)) return;

        ItemStack heldItem = player.getMainHandItem();
        if (heldItem.getItem() != Items.EMERALD) {
            player.displayClientMessage(
                    Component.literal("Hold an Emerald in your hand to call a Wandering Trader")
                            .withStyle(ChatFormatting.GREEN),
                    true
            );
            return;
        }

        long currentTime = level.getDayTime();

        player.getCapability(BellTraderDataProvider.BELL_TRADER_CAP).ifPresent(data -> {

            long lastSummoned = data.getLastBellDay();

            if (lastSummoned != 0 && currentTime - lastSummoned < 24000) {
                player.displayClientMessage(
                        Component.literal("You can only summon a Wandering Trader once a day")
                                .withStyle(ChatFormatting.RED),
                        true
                );
                return;
            }

            data.setLastBellDay(currentTime);
            heldItem.shrink(1);

            level.playSound(null, pos, SoundEvents.BELL_RESONATE, SoundSource.BLOCKS);

            WanderingTrader trader = EntityType.WANDERING_TRADER.create(level);
            if (trader == null) return;

            trader.addTag("adj.bell");

            DelayedTaskScheduler.schedule(() -> {

                trader.addEffect(new MobEffectInstance(
                        MobEffects.GLOWING, 200, 0, true, true, false
                ));

                trader.setPos(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
                level.addFreshEntity(trader);

                level.playSound(
                        null,
                        trader.getX(), trader.getY(), trader.getZ(),
                        SoundEvents.CHORUS_FRUIT_TELEPORT,
                        SoundSource.NEUTRAL,
                        1F, 1F
                );

                AABB box = trader.getBoundingBox();
                RandomSource random = level.getRandom();

                for (int i = 0; i < 25; i++) {
                    double x = Mth.lerp(random.nextDouble(), box.minX, box.maxX);
                    double y = Mth.lerp(random.nextDouble(), box.minY, box.maxY);
                    double z = Mth.lerp(random.nextDouble(), box.minZ, box.maxZ);

                    ((ServerLevel) level).sendParticles(
                            ParticleTypes.HAPPY_VILLAGER,
                            x, y, z,
                            1, 0, 0, 0, 0.1
                    );
                }

            }, 40);
        });
    }
}
