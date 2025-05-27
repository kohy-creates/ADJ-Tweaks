package xyz.kohara.adjtweaks.misc;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DelayedTaskScheduler {
    private static final Map<Integer, List<Runnable>> TASKS = new HashMap<>();
    private static int tickCount = 0;

    private static void tick() {
        tickCount++;
        List<Runnable> runnables = TASKS.remove(tickCount);
        if (runnables != null) {
            runnables.forEach(Runnable::run);
        }
    }

    public static void schedule(Runnable task, int delay) {
        int executeTick = tickCount + delay;
        TASKS.computeIfAbsent(executeTick, k -> new ArrayList<>()).add(task);
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            DelayedTaskScheduler.tick();
        }
    }
}
