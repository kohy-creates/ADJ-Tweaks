package xyz.kohara.adjcore.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xyz.kohara.adjcore.Config;

public class HardcoreTweaks {

    // Random respawn
    @SubscribeEvent
    private static void onWorldLoad(LevelEvent.Load event) {
        LevelAccessor accessor = event.getLevel();
        if (accessor instanceof ServerLevel level && level.getLevelData().isHardcore()) {
            level.getGameRules()
                    .getRule(GameRules.RULE_SPAWN_RADIUS)
                    .set(
                            Config.HARDCORE_RESPAW_RADIUS.get(),
                            level.getServer()
                    );

        }
    }
}
