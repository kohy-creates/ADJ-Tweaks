package xyz.kohara.adjcore.music;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.LevelEvent;

import java.util.Map;

public class JukeboxTracker {
    public static Object2BooleanOpenHashMap<BlockPos> jukeboxes = new Object2BooleanOpenHashMap<>();

    public static void onJukeboxPlay(Level level, Minecraft minecraft, BlockPos pos) {
        if (level != null) {
            minecraft.getMusicManager().stopPlaying(MusicPlayer.CURRENT_TRACK);
            jukeboxes.put(pos, true);
        }
    }

    public static void onJukeboxStop(BlockPos pos) {
            jukeboxes.removeBoolean(pos);
    }

    public static boolean noJukeboxesInRange() {
        return jukeboxes.object2BooleanEntrySet().stream().noneMatch(Object2BooleanMap.Entry::getBooleanValue);
    }

    public static void init() {
        // Clear jukeboxes on client world change
        MinecraftForge.EVENT_BUS.addListener((LevelEvent.Load event) -> {
            if (event.getLevel().isClientSide()) {
                jukeboxes.clear();
            }
        });

        // Tick event
        MinecraftForge.EVENT_BUS.addListener((TickEvent.ClientTickEvent event) -> {
            if (event.phase == TickEvent.Phase.END) {
                Minecraft mc = Minecraft.getInstance();
                if (mc.player != null && !jukeboxes.isEmpty()) {
                    for (Map.Entry<BlockPos, Boolean> entry : jukeboxes.entrySet()) {
                        BlockPos pos = entry.getKey();
                        double distanceSq = mc.player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);

                        if (distanceSq > 48) {
                            entry.setValue(false);
                        } else {
                            entry.setValue(true);
                            mc.getMusicManager().stopPlaying(MusicPlayer.CURRENT_TRACK);
                        }
                    }
                }
            }
        });
    }
}