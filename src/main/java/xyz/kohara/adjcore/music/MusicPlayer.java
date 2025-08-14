package xyz.kohara.adjcore.music;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.registries.ForgeRegistries;
import xyz.kohara.adjcore.ADJCore;
import xyz.kohara.adjcore.mixins.music.MusicManagerAccessor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class MusicPlayer {

    // This will hold pre-built Music objects after load()
    public static Music resolvedMenu;
    public static Map<String, ResolvedEntry> resolvedDefaults = new HashMap<>();
    public static Map<String, ResolvedEntry> resolvedBiomes = new HashMap<>();
    public static Map<String, ResolvedBossMusic> resolvedBosses = new HashMap<>();

    private static final Predicate<WitherBoss> WITHER_PREDICATE = (entity) -> true;
    private static final Predicate<LivingEntity> ENTITY_PREDICATE = (entity) -> true;
    private static Music[] bossMusic;

    public static Music CURRENT_TRACK = null;

    public static void buildResolvedMaps() {
        MusicConfig cfg = MusicConfig.CONFIG;

        resolvedMenu = getTrack(cfg.menu);

        if (cfg.defaults != null) {
            for (Map.Entry<String, MusicConfig.MusicEntry> e : cfg.defaults.entrySet()) {
                resolvedDefaults.put(e.getKey(), resolveEntry(e.getValue()));
            }
        }

        if (cfg.biome != null) {
            for (Map.Entry<String, MusicConfig.MusicEntry> e : cfg.biome.entrySet()) {
                resolvedBiomes.put(e.getKey(), resolveEntry(e.getValue()));
            }
        }

        if (cfg.boss != null) {
            for (Map.Entry<String, MusicConfig.BossMusic> e : cfg.boss.entrySet()) {
                Music start = getTrack(e.getValue().start);
                Music loop = getTrack(e.getValue().loop);
                Music stop = getTrack(e.getValue().stop);
                resolvedBosses.put(e.getKey(), new ResolvedBossMusic(start, loop, stop));
            }
        }
    }

    private static ResolvedEntry resolveEntry(MusicConfig.MusicEntry entry) {
        if (entry == null) return null;
        Music track = getTrack(entry.track);
        Map<String, Music> conds = null;
        if (entry.conditions != null) {
            conds = new HashMap<>();
            for (Map.Entry<String, String> c : entry.conditions.entrySet()) {
                conds.put(c.getKey(), getTrack(c.getValue()));
            }
        }
        return new ResolvedEntry(track, conds);
    }

    public static Music findMusic(MusicManager musicManager) {
        if (resolvedMenu == null) buildResolvedMaps();
        Music track = resolvedMenu;

        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            ResolvedEntry overworld = resolvedDefaults.get("minecraft:overworld");
            if (overworld != null && overworld.conditions != null) {
                track = overworld.conditions.get(getVariant(player.level()).id);
            }

            String dimension = player.level().dimension().location().toString();
            String biomeName = player.level()
                    .getBiome(player.getOnPos())
                    .unwrapKey()
                    .map(ResourceKey::location)
                    .orElse(ResourceLocation.fromNamespaceAndPath("unknown", "unknown"))
                    .toString();

            // dimension-specific
            ResolvedEntry dimEntry = resolvedDefaults.get(dimension);
            if (dimEntry != null) {
                if (dimEntry.track != null) {
                    track = dimEntry.track;
                } else if (dimEntry.conditions != null) {
                    Music condTrack = dimEntry.conditions.get(getVariant(player.level()).id);
                    if (condTrack != null) track = condTrack;
                }
            }

            // biome-specific
            for (String key : resolvedBiomes.keySet()) {
                boolean found = false;
                if (key.startsWith("#") && player.level().getBiome(player.getOnPos())
                        .is(TagKey.create(Registries.BIOME, ResourceLocation.parse(key.substring(1))))) {
                    found = true;
                } else if (biomeName.equals(key)) {
                    found = true;
                }
                if (found) {
                    ResolvedEntry biomeEntry = resolvedBiomes.get(key);
                    if (biomeEntry.track != null) {
                        track = biomeEntry.track;
                    } else if (biomeEntry.conditions != null) {
                        Music condTrack = biomeEntry.conditions.get(getVariant(player.level()).id);
                        if (condTrack != null) track = condTrack;
                    }
                }
            }
        }
        if (CURRENT_TRACK != null && CURRENT_TRACK != track && ((MusicManagerAccessor) musicManager).getCurrentMusic() != null) {
            //System.out.println(CURRENT_TRACK.getEvent() + " <- " + track.getEvent());
            musicManager.stopPlaying(CURRENT_TRACK);
        }
        CURRENT_TRACK = track;
        return track;
    }

    private static Music getTrack(String str) {
        if (str == null) return null;
        ResourceLocation id = ResourceLocation.parse(str);
        Optional<Holder<SoundEvent>> optional = ForgeRegistries.SOUND_EVENTS.getHolder(id);
        if (optional.isEmpty()) {
            ADJCore.LOGGER.error("Unknown sound event {}", str);
            return null;
        }
        Holder<SoundEvent> holder = optional.get();
        return new Music(holder, 10, 20, false);
    }

    public static boolean shouldPlayMusic(MusicManager musicManager) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            String dimension = player.level().dimension().location().toString();
            double i = player.getY();
            double j = player.getX();
            double k = player.getZ();
            float f = 50.0F;
            AABB box = new AABB((float) i - f, (float) j - f, (float) k - f, (float) (i + 1.0F) + f, (float) (j + 1.0F) + f, (float) (k + 1.0F) + f);

            for (LivingEntity e : player.level().getEntities(EntityTypeTest.forClass(LivingEntity.class), box, ENTITY_PREDICATE)) {
                if ((getMonsterName(e).contains("entity.witherstormmod.witherstorm") ||
                        getMonsterName(e).contains("entity.aquamirae.captain_cornelia") ||
                        getMonsterName(e).contains("entity.unusualend.endstone_golem"))) {
                    return false;
                }
            }
            return JukeboxTracker.noJukeboxesInRange();
        }
        return true;
    }

    private static String getMonsterName(LivingEntity e) {
        return e.getName().toString()
                .replace("translation{key='", "")
                .replace("', args=[]}", "");
    }

    private static TrackVariant getVariant(Level level) {
        if (level.isThundering()) return TrackVariant.THUNDER;
        else if (level.isRaining()) return TrackVariant.RAIN;

        long time = level.getDayTime();
        if (time % 24000L < 12550) {
            return TrackVariant.DAY;
        }
        return TrackVariant.NIGHT;
    }

    private enum TrackVariant {
        DAY("day"),
        NIGHT("night"),
        RAIN("rain"),
        THUNDER("thunder");

        public final String id;

        TrackVariant(String text) {
            this.id = text;
        }
    }

    public record ResolvedEntry(Music track, Map<String, Music> conditions) {
    }

    public record ResolvedBossMusic(Music start, Music loop, Music stop) {
    }
}
